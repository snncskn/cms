package com.minetec.backend.dto.form;

import com.minetec.backend.dto.info.ImageInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

@Data
public class VehicleImageCreateForm {

    @NotNull
    private UUID vehicleUuid;

    @NotNull
    private List<ImageInfo> imageInfos;

}
