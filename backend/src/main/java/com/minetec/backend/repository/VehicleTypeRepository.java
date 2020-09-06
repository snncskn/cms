package com.minetec.backend.repository;

import com.minetec.backend.entity.VehicleType;
import com.minetec.backend.repository.projection.VehicleTypeListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Sinan
 */

@Repository
public interface VehicleTypeRepository extends BaseRepository<VehicleType> {

    @Query("select vt from VehicleType vt where vt.isActive = true ")
    Page<VehicleTypeListItemProjection> list(Pageable pageable);
}
