package com.minetec.backend.repository;

import com.minetec.backend.entity.VehicleAttributeValue;
import com.minetec.backend.repository.projection.VehicleAttributeValueListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.validation.constraints.NotNull;

/**
 * @author Sinan
 */
@Repository
public interface VehicleAttributeValueRepository extends BaseRepository<VehicleAttributeValue> {

    @Query("select vav from VehicleAttributeValue vav")
    Page<VehicleAttributeValueListItemProjection> list(Pageable pageable);

    Page<VehicleAttributeValueListItemProjection> findAll(@NotNull Specification specification,
                                                          @NotNull Pageable pageable);
}
