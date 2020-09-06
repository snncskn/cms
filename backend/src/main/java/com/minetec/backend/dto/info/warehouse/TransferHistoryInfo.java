package com.minetec.backend.dto.info.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import com.minetec.backend.dto.info.BasicUserInfo;
import lombok.Data;

import java.util.UUID;

@Data
public class TransferHistoryInfo {

    private UUID uuid;
    private TransferInfo transferInfo;
    private BasicUserInfo userInfo;
    private String description;
    private TransferStatus status;
}

