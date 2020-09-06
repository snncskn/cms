package com.minetec.backend.dto.enums;


/**
 * @author Sinan
 */
public enum ReportType {
    BREAKDOWN("BRK"), SERVICE("SRH");

    private String type;

    ReportType(final String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
