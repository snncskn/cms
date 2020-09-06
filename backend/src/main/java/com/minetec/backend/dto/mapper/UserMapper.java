package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.UserInfo;
import com.minetec.backend.entity.User;
import com.minetec.backend.util.Utils;

import javax.validation.constraints.NotNull;

public class UserMapper {
    public static UserInfo toInfo(@NotNull final User user) {
        if (Utils.isEmpty(user)) {
            return null;
        }
        var info = new UserInfo();
        info.setEmail(user.getEmail());
        info.setFirstName(user.getFirstName());
        info.setLastName(user.getLastName());
        info.setContactNumber(user.getContactNumber());
        info.setExpireDate(user.getExpireDate());
        info.setUuid(user.getUuid());
        info.setPosition(user.getPosition());
        info.setStaffNumber(user.getStaffNumber());
        info.setRoleInfos(RoleMapper.toInfos(user.getRoles()));
        info.setSiteInfos(SiteMapper.toInfos(user.getSites()));
        /**
         * TODO : one image for now
         */
        if (!Utils.isEmpty(user.getImages())) {
            info.setImageInfo(ImageMapper.toInfo(user.getImages().get(0)));
        }
        return info;
    }

    /**
     * @param user
     * @return
     */
    public static BasicUserInfo toBasicInfo(@NotNull final User user) {
        if (Utils.isEmpty(user)) {
            return null;
        }
        var basicInfo = new BasicUserInfo();
        basicInfo.setCreateDate(Utils.toString(user.getCreatedAt()));
        basicInfo.setFullName(user.getFirstName() + " " + user.getLastName());
        basicInfo.setPosition(user.getPosition());
        basicInfo.setUuid(user.getUuid());
        basicInfo.setStaffNumber(user.getStaffNumber());
        return basicInfo;
    }

}
