package com.minetec.backend.repository;


import com.minetec.backend.entity.ItemTypeAttribute;
import com.minetec.backend.repository.projection.ItemTypeAttributeListItemProjection;
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
public interface ItemTypeAttributeRepository extends BaseRepository<ItemTypeAttribute> {

    @Query("select vta from ItemTypeAttribute vta ")
    Page<ItemTypeAttributeListItemProjection> list(Pageable pageable);

    @Query("select vta from ItemTypeAttribute vta where vta.itemType.id = ?1 ")
    List<ItemTypeAttribute> findByItemTypeId(long itemTypeId);

    Page<ItemTypeAttributeListItemProjection> findAll(@NotNull Specification specification,
                                                      @NotNull Pageable pageable);
}
