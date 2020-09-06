package com.minetec.backend.service.workshop;

import com.minetec.backend.dto.form.workshop.BreakDownCreateUpdateForm;
import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import com.minetec.backend.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JobCardFacade {

    private final JobCardService jobCardService;
    private final VehicleService vehicleService;

    /**
     * @param form
     * @return
     */
    public BreakDownListInfo createBreakDown(@NotNull final BreakDownCreateUpdateForm form) {
        return jobCardService.createBreakDown(vehicleService.findByUuid(form.getVehicleUuid()), form);
    }

    /**
     * @param jobCardUuid
     */
    public void updateVehicleLastServiceDate(@NotNull final UUID jobCardUuid) {
        final var jobCard = jobCardService.findByUuid(jobCardUuid);
        if (jobCard.getReportNumber().startsWith("SRH")) {
            vehicleService.updateLastServiceDate(jobCard.getVehicle().getUuid());
            /**
             * TODO : Last service hours will update, when coming data on device
             */
        }
    }


}
