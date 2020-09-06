package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sinan
 */
@Data
public class OrderCreateUpdateForm {
    @NotNull
    private UUID supplierUuid;
    private UUID siteUuid;
    @NotNull
    private String status;
    private String requestDate;
    private String requestNumber;
    private String orderNumber;
    private String invoiceDate;
    private String invoiceNumber;
    private String totalQuantity;
    private String grandTotal;
    private String taxTotal;
    private String discountTotal;
    private String orderCreationDate;
    private String referenceNumber;
    @NotNull
    @NotEmpty
    private String addressDetail;
}
