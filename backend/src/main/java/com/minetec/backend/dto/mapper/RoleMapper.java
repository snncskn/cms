package com.minetec.backend.dto.mapper;

import com.minetec.backend.dto.info.RoleInfo;
import com.minetec.backend.entity.Role;

import java.util.ArrayList;
import java.util.List;

public class RoleMapper {

    public static RoleInfo toInfo(final Role role) {
        var info = new RoleInfo();
        info.setName(role.getName());
        info.setUuid(role.getUuid());

        return info;
    }

    public static List<RoleInfo> toInfos(final List<Role> roles) {
        var infos = new ArrayList<RoleInfo>();
        roles.forEach(role -> infos.add(toInfo(role)));
        return infos;
    }
}
