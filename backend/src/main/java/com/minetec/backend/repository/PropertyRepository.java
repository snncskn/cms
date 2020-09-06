package com.minetec.backend.repository;

import com.minetec.backend.entity.Property;
import com.minetec.backend.repository.projection.PropertyListItemProjection;
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
public interface PropertyRepository extends BaseRepository<Property> {

    @Query("select p from Property p")
    Page<PropertyListItemProjection> list(Pageable pageable);

    Page<PropertyListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);

    Page<PropertyListItemProjection> findByGroupName(@NotNull String groupName, @NotNull Pageable pageable);
}
