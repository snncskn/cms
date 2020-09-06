package com.minetec.backend.repository;

import com.minetec.backend.entity.Supplier;
import com.minetec.backend.repository.projection.SupplierListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends BaseRepository<Supplier> {

    @Query("select sup from Supplier sup where sup.isActive = true")
    Page<SupplierListItemProjection> list(Pageable pageable);

}
