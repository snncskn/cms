package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UserSiteUpdateForm {
    @NotNull
    private UUID siteUuid;

}
