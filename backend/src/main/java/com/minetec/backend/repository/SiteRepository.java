package com.minetec.backend.repository;

import com.minetec.backend.entity.Site;
import com.minetec.backend.repository.projection.SiteListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Sinan
 */
@Repository
public interface SiteRepository extends BaseRepository<Site> {
    @Query("select s from Site s")
    Page<SiteListItemProjection> list(Pageable pageable);
}
