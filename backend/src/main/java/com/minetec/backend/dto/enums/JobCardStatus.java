package com.minetec.backend.dto.enums;


/**
 * @author Sinan
 */
public enum JobCardStatus {
    ACTIVE_BREAKDOWN_REPORT("Active Breakdown Report"),
    ACTIVE_JOB_CARD("Active Job Card"),
    CLOSED_JOB_CARD("Closed Job Card");

    private String status;

    JobCardStatus(final String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
