package com.minetec.backend.dto.form.workshop;

import com.minetec.backend.dto.info.ImageInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class JobCardImageCreateForm {

    @NotNull
    private UUID jobCardUuid;

    @NotNull
    private List<ImageInfo> imageInfos;

}
