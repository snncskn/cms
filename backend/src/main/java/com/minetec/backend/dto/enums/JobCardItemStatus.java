package com.minetec.backend.dto.enums;


/**
 * @author Sinan
 */
public enum JobCardItemStatus {

    REQUESTED("Requested"), DELIVERED("Delivered"), PARTIAL("Partial");

    private String status;

    JobCardItemStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
