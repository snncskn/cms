package com.minetec.backend.dto.form.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferHistoryCreateUpdateForm {
    private UUID uuid;
    private UUID transferUuid;
    private String description;
    private TransferStatus status;
}
