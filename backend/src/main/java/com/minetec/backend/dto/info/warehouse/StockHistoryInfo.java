package com.minetec.backend.dto.info.warehouse;

import lombok.Data;

@Data
public class StockHistoryInfo {

    private String createdDate;
    private String stockCode;
    private String itemDescription;
    private String siteName;
    private String moveType;
    private String quantity;
    private String moveNumber;
}
