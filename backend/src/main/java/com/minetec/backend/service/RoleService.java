package com.minetec.backend.service;

import com.minetec.backend.dto.form.RoleCreateUpdateForm;
import com.minetec.backend.dto.info.RoleInfo;
import com.minetec.backend.dto.mapper.RoleMapper;
import com.minetec.backend.entity.Role;
import com.minetec.backend.repository.RoleRepository;
import com.minetec.backend.repository.projection.RoleListItemProjection;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class RoleService extends EntityService<Role, RoleRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<RoleListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param form
     * @return
     */
    public RoleInfo create(final RoleCreateUpdateForm form) {
        if ((form.getUuid() == null) && !(StringUtils.isWhitespace(form.getName()))) {
            var role = new Role();
            role.setName(form.getName());
            this.persist(role);
            return RoleMapper.toInfo(role);
        } else {
            return update(form.getUuid(), form);
        }
    }

    /**
     * @param uuid
     * @param form
     * @return
     */
    public RoleInfo update(final UUID uuid, final RoleCreateUpdateForm form) {
        var roleUpdate = this.findByUuid(uuid);
        roleUpdate.setName(form.getName());
        this.persist(roleUpdate);
        return RoleMapper.toInfo(roleUpdate);
    }

    /**
     * @param uuid
     * @return
     */
    public RoleInfo find(final UUID uuid) {
        return RoleMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.deleteByUuid(uuid);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<RoleListItemProjection> findBy(final String value, final Pageable pageable) {
        Page<Role> role = this.getRepository().findAll(this.getFilter(value), pageable);
        final List<RoleInfo> items = role.stream().map(RoleMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, role.getTotalElements());
    }


    /**
     * @param value
     * @return
     */
    public Specification<Role> getFilter(@Valid @NotNull final String value) {
        return (root, query, cb) -> where(
            contains("name", value))
            .toPredicate(root, query, cb);
    }
}
