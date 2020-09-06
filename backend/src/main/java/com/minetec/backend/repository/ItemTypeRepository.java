package com.minetec.backend.repository;

import com.minetec.backend.entity.ItemType;
import com.minetec.backend.repository.projection.ItemTypeListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Sinan
 */
@Repository
public interface ItemTypeRepository extends BaseRepository<ItemType> {
    @Query("select vt from ItemType vt where vt.isActive = true")
    Page<ItemTypeListItemProjection> list(Pageable pageable);
}
