package com.minetec.backend.service;

import com.minetec.backend.dto.form.UserCreateUpdateForm;
import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.UserInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.SiteMapper;
import com.minetec.backend.dto.mapper.UserMapper;
import com.minetec.backend.entity.Role;
import com.minetec.backend.entity.Site;
import com.minetec.backend.entity.User;
import com.minetec.backend.error_handling.exception.AccessNotAllowedException;
import com.minetec.backend.repository.UserRepository;
import com.minetec.backend.repository.projection.UserListItemProjection;
import com.minetec.backend.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService extends EntityService<User, UserRepository> {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ImageService imageService;
    private final SiteService siteService;
    private final RoleService roleService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<UserListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param userCreateForm
     * @return
     */

    public UserInfo create(@NotNull final UserCreateUpdateForm userCreateForm) {
        if (!userCreateForm.getPassword().equals(userCreateForm.getVerifyPassword())) {
            throw new AccessNotAllowedException("f12864a626ak", "verify-incorrect");
        }
        var user = new User();
        user.setPassword(bCryptPasswordEncoder.encode(userCreateForm.getPassword()));
        return UserMapper.toInfo(initEntity(userCreateForm, user));
    }

    private User initEntity(final UserCreateUpdateForm userCreateForm, final User user) {
        user.setEmail(userCreateForm.getEmail());
        user.setContactNumber(userCreateForm.getContactNumber());
        user.setFirstName(userCreateForm.getFirstName());
        user.setLastName(userCreateForm.getLastName());
        user.setExpireDate(Utils.toLocalDate(userCreateForm.getExpireDate()));
        user.setPosition(userCreateForm.getPosition());
        user.setStaffNumber(userCreateForm.getStaffNumber());

        var sites = new ArrayList<Site>();
        userCreateForm.getSiteInfos().forEach(siteInfo -> sites.add(siteService.findByUuid(siteInfo.getUuid())));
        user.setSites(sites);

        var roles = new ArrayList<Role>();
        userCreateForm.getRoleInfos().forEach(roleInfo -> roles.add(roleService.findByUuid(roleInfo.getUuid())));
        user.setRoles(roles);

        var newUser = this.persist(user);

        if (!Utils.isEmpty(userCreateForm.getImageInfo()) && !Utils.isEmpty(userCreateForm.getImageInfo().getUuid())) {
            imageService.updateTo(newUser, userCreateForm.getImageInfo());
        }
        return newUser;
    }

    /**
     * @param userUpdateForm
     * @return
     */

    public UserInfo update(@NotNull final UUID uuid, @NotNull final UserCreateUpdateForm userUpdateForm) {
        final var user = this.findByUuid(uuid);
        final var updatedUser = initEntity(userUpdateForm, user);
        return UserMapper.toInfo(updatedUser);
    }

    /**
     * @param uuid
     * @return
     */
    public UserInfo find(@NotNull final UUID uuid) {
        final var user = this.findByUuid(uuid);
        final var userInfo = UserMapper.toInfo(user);
        final var image = imageService.getRepository().findByUserId(user.getId());
        if (!Utils.isEmpty(image)) {
            userInfo.setImageInfo(ImageMapper.toInfo(image));
            userInfo.getImageInfo().setDownloadUrl(imageService.imageUrl(image.getUuid()));
        }
        userInfo.setSiteInfos(SiteMapper.toInfos(user.getSites()));
        return userInfo;
    }

    /**
     * @param uuid
     */
    public void softDelete(@NotNull final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }

    /**
     * @param filter
     * @param pageable
     * @return
     */
    public Page<UserListItemProjection> findBy(final String filter, final Pageable pageable) {
        Page<User> users = null;
        if ("*".equals(filter)) {
            users = this.getRepository().findAll(pageable);
        } else {
            users = this.getRepository().findAll(this.getFilter(filter), pageable);
        }
        final List<BasicUserInfo> items = users.stream().map(UserMapper::toBasicInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, users.getTotalElements());
    }


    /**
     * @param filter
     * @return
     */
    private Specification<User> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("firstName", filter)
                .or(contains("lastName", filter))
                .or(contains("email", filter))
                .or(contains("contactNumber", filter))
                .or(contains("address", filter))
                .or(contains("position", filter))
                .or(contains("staffNumber", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

}
