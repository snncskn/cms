package com.minetec.backend.repository;

import com.minetec.backend.entity.ItemAttributeValue;
import com.minetec.backend.repository.projection.ItemAttributeValueListItemProjection;
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
public interface ItemAttributeValueRepository extends BaseRepository<ItemAttributeValue> {

    @Query("select vav from ItemAttributeValue vav ")
    Page<ItemAttributeValueListItemProjection> list(Pageable pageable);

    Page<ItemAttributeValueListItemProjection> findAll(@NotNull Specification specification,
                                                       @NotNull Pageable pageable);
}
