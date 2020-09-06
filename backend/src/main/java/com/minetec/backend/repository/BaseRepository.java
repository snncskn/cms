package com.minetec.backend.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * @param <E>
 * @author Sinan
 */

@NoRepositoryBean
public interface BaseRepository<E> extends PagingAndSortingRepository<E, Long>, JpaSpecificationExecutor<E> {
    Optional<E> findByUuid(UUID uuid);
}
