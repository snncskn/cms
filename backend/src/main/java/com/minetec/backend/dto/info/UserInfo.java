package com.minetec.backend.dto.info;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@Data
public class UserInfo {

    @NotNull
    private String email;
    private UUID uuid;
    private String firstName;
    private String lastName;
    private String position;
    private String contactNumber;
    private LocalDateTime expireDate;
    private boolean locked;
    private String token;
    private List<RoleInfo> roleInfos;
    private List<String> permissions;
    private ImageInfo imageInfo;
    private List<SiteInfo> siteInfos;
    /**
     * TODO : current site is not ready
     */
    private SiteInfo currentSite;
    private String staffNumber;
}
