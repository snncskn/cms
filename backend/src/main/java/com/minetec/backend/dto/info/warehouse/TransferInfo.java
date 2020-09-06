package com.minetec.backend.dto.info.warehouse;

import com.minetec.backend.dto.info.BasicUserInfo;
import com.minetec.backend.dto.info.SiteInfo;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class TransferInfo {

    @NotNull
    private UUID uuid;
    private SiteInfo sourceSiteInfo;
    private SiteInfo targetSiteInfo;
    private List<TransferItemInfo> transferItemInfos;
    private String status;
    private String transferDate;
    private String transferNumber;
    private String rejectionReason;
    private BigDecimal totalQuantity;
    private BasicUserInfo basicUserInfo;
}
