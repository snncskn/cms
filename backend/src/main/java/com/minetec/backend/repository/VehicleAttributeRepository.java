package com.minetec.backend.repository;


import com.minetec.backend.entity.VehicleAttribute;
import com.minetec.backend.repository.projection.VehicleAttributeListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * @author Sinan
 */
@Repository
public interface VehicleAttributeRepository extends BaseRepository<VehicleAttribute> {
    @Query("select va from VehicleAttribute va ")
    Page<VehicleAttributeListItemProjection> list(Pageable pageable);
}
