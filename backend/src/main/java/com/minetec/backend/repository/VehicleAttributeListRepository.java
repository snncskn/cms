package com.minetec.backend.repository;

import com.minetec.backend.entity.VehicleAttributeList;
import com.minetec.backend.repository.projection.VehicleAttributeListListItemProjection;
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
public interface VehicleAttributeListRepository extends BaseRepository<VehicleAttributeList> {

    @Query("select va from VehicleAttributeList va where va.vehicle.id = ?1 ")
    List<VehicleAttributeList> findByVehicleId(long vehicleId);

    @Query("select va from VehicleAttributeList va where va.vehicleAttributeValue.id = ?1 ")
    List<VehicleAttributeList> findByVehicleAttributeValues(long vehicleAttributeValueId);

    Page<VehicleAttributeListListItemProjection> findAll(@NotNull Specification specification,
                                                         @NotNull Pageable pageable);

}
