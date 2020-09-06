package com.minetec.backend.repository;


import com.minetec.backend.entity.VehicleTypeAttribute;
import com.minetec.backend.repository.projection.VehicleTypeAttributeListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;
import java.util.List;


/**
 * @author Sinan
 */

@Repository
public interface VehicleTypeAttributeRepository extends BaseRepository<VehicleTypeAttribute> {
    @Query("select vta from VehicleTypeAttribute vta ")
    Page<VehicleTypeAttributeListItemProjection> list(Pageable pageable);

    @Query("select vta from VehicleTypeAttribute vta where vta.vehicleType.id = ?1 " +
        " order by vta.vehicleAttribute.name ")
    List<VehicleTypeAttribute> findByVehicleTypeId(long vehicleTypeId);

    Page<VehicleTypeAttributeListItemProjection> findAll(@NotNull Specification specification,
                                                         @NotNull Pageable pageable);
}
