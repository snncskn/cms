package com.minetec.backend.dto.form.warehouse;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Sinan
 */
@Data
public class TransferDeliverForm {

    @NotNull
    @NotEmpty
    private String transferNumber;

    @NotNull
    @NotEmpty
    private String transferDate;

}
