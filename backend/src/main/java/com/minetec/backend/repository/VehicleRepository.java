package com.minetec.backend.repository;

import com.minetec.backend.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Sinan
 */
@Repository
public interface VehicleRepository extends BaseRepository<Vehicle> {

    @Query("select v from Vehicle v  where v.isActive = true and v.site.id = :siteId ")
    Page<Vehicle> list(Long siteId, Pageable pageable);

    /**
     * @param fleetNo
     * @param registrationNo
     * @param serialNo
     * @param vinNo
     * @param vehicleTypeName
     * @param pageable
     * @return
     */
    @Query("select v from Vehicle v  where " +
        "v.isActive = true and " +
        "v.isUsable = :isUsable and " +
        "v.site.id = :siteId and " +
        "v.fleetNo like %:fleetNo% and " +
        "v.registrationNo like %:registrationNo% and " +
        "v.serialNo like %:serialNo% and " +
        "v.vinNo like %:vinNo% and " +
        "COALESCE (v.currentMachineHours, '') like %:currentMachineHours% and " +
        "COALESCE (v.serviceIntervalHours, '') like %:serviceIntervalHours% and " +
        "COALESCE (v.lastServiceHours, '') like %:lastServiceHours% and " +
        "v.vehicleType.name like %:vehicleTypeName% ")
    Page<Vehicle> filter(boolean isUsable, Long siteId, String fleetNo, String registrationNo, String serialNo,
                         String vinNo, String currentMachineHours, String serviceIntervalHours,
                         String lastServiceHours, String vehicleTypeName, Pageable pageable);
}
