package com.minetec.backend.dto.form;

import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.RoleInfo;
import com.minetec.backend.dto.info.SiteInfo;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class UserCreateUpdateForm {

    @NotNull
    @NotEmpty
    @Email
    private String email;

    private String password;
    private String verifyPassword;

    private UUID uuid;
    private String firstName;
    private String lastName;
    private String contactNumber;
    private String expireDate;

    private String position;
    private String staffNumber;
    private ImageInfo imageInfo;

    @NotNull
    private List<SiteInfo> siteInfos;

    @NotNull
    private List<RoleInfo> roleInfos;

}
