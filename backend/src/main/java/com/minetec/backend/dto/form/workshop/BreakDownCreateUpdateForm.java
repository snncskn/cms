package com.minetec.backend.dto.form.workshop;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class BreakDownCreateUpdateForm {
    private UUID vehicleUuid;
    private UUID operatorUuid;
    private String reportType;
    private String startDate;
    private String reportNumber;
    private String description;
    private String currentKmHour;
    private List<JobCardImageCreateForm> jobCardImageCreateForms;
}
