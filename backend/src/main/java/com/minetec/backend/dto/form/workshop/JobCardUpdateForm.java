package com.minetec.backend.dto.form.workshop;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class JobCardUpdateForm {

    @NotNull
    private UUID jobCardUuid;

    @NotNull
    private UUID operatorUserUuid;
    @NotNull
    private UUID mechanicUserUuid;
    @NotNull
    private UUID foremanUserUuid;

    private boolean riskAssessment;
    private boolean lockOutProcedure;
    private boolean wheelNuts;
    private boolean oilLevel;
    private boolean machineGrease;
}
