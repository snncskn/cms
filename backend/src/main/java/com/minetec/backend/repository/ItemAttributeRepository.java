package com.minetec.backend.repository;


import com.minetec.backend.entity.ItemAttribute;
import com.minetec.backend.repository.projection.ItemAttributeListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


/**
 * @author Sinan
 */
@Repository
public interface ItemAttributeRepository extends BaseRepository<ItemAttribute> {
    @Query("select va from ItemAttribute va ")
    Page<ItemAttributeListItemProjection> list(Pageable pageable);
}
