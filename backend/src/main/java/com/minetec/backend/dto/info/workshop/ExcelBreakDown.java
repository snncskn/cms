package com.minetec.backend.dto.info.workshop;

import lombok.Data;

@Data
public class ExcelBreakDown extends BreakDownListInfo {

    public String getOperatorUserName() {
        return super.getOperatorUser().getFullName();
    }
}
