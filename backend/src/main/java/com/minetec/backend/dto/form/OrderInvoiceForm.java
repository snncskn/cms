package com.minetec.backend.dto.form;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Sinan
 */
@Data
public class OrderInvoiceForm {

    @NotNull
    @NotEmpty
    private String invoiceNumber;

    @NotNull
    @NotEmpty
    private String invoiceDate;

}
