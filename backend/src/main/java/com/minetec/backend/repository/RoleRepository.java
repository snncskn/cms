package com.minetec.backend.repository;

import com.minetec.backend.entity.Role;
import com.minetec.backend.repository.projection.RoleListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * @author Brandon Tabe
 */
@Repository
public interface RoleRepository extends BaseRepository<Role> {
    @Query("select r from Role r")
    Page<RoleListItemProjection> list(Pageable pageable);
}

