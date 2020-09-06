package com.minetec.backend.dto.filter;

import lombok.Data;

@Data
public class ItemFilterForm {

    private String storePartNumber = "";
    private String barcode = "";
    private String itemDescription = "";
    private String filter = "";

    private int page;
    private int size;
}
