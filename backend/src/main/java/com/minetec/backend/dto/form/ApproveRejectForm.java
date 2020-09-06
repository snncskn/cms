package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class ApproveRejectForm {

    private UUID transferUuid;
    private UUID orderUuid;
    @NotNull
    @NotEmpty
    private String status;
    private String rejectionReason;

}
