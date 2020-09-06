package com.minetec.backend.service;

import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.User;
import com.minetec.backend.error_handling.exception.ItemNotFoundException;
import com.minetec.backend.repository.BaseRepository;
import com.minetec.backend.security.filter.AuthorizationDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

/**
 * @author Sinan
 */

@Slf4j
@Transactional(timeout = 100)
public abstract class EntityService<E extends AbstractEntity, R extends BaseRepository<E>> extends BaseService {

    @Autowired
    public R repository;

    /**
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsById(@NotNull final Long id) {
        log.debug("[existsById Entity: {}]", id);
        return repository.existsById(id);
    }

    /**
     * @param uuid
     * @return
     */
    @Transactional(readOnly = true)
    public boolean existsByUuid(@NotNull final UUID uuid) {
        log.debug("[existsByUuid Entity: {}]", uuid);
        return !repository.findByUuid(uuid).isEmpty();
    }

    /**
     * findAll
     *
     * @return
     */
    @Transactional(readOnly = true)
    public Iterable<E> findAll() {
        log.debug("[findAll Entities]");
        return repository.findAll(Sort.by(Order.asc("id")));

    }

    /**
     * Get entity object with given Id.
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public E findById(@NotNull final Long id) {
        log.debug("[findById Entity: {}][EntityId: {}]", id.getClass().getSimpleName(), id);
        return repository.findById(id).orElseThrow(() -> new ItemNotFoundException("entity-00005", "ID not found.."));
    }


    /**
     * @param entity
     * @return
     */
    public <S extends E> S persist(@NotNull final S entity) {
        log.debug("[Entity: {}][EntityId: {}]", entity.getClass().getSimpleName(), entity.getId());
        return repository.save(entity);
    }


    /**
     * delete entity
     *
     * @param entity
     */
    public void delete(@NotNull final E entity) {
        repository.delete(entity);
        log.debug("[delete Entity: {}][EntityId: {}]", entity.getClass().getSimpleName(), entity.getId());
    }

    /**
     * delete entity
     *
     * @param entities
     */
    public void deleteAll(@NotNull final List<E> entities) {
        entities.forEach(entity ->
            log.debug("[deleteAll Entity: {}][EntityId: {}]", entity.getClass().getSimpleName(), entity.getId()));
        repository.deleteAll(entities);
    }

    /**
     * delete entity
     *
     * @param id
     */
    public void deleteById(@NotNull final Long id) {
        repository.deleteById(id);
        log.debug("[deleteById Entity: {}][EntityId: {}]", id.getClass().getSimpleName(), id);
    }

    /**
     * delete entity
     *
     * @param uuid
     */
    public void deleteByUuid(@NotNull final UUID uuid) {
        E entity = repository.findByUuid(uuid).orElseThrow(() ->
            new ItemNotFoundException("entity-00001", "UUID not found.."));
        repository.delete(entity);
        log.debug("[UpdateById Entity: {}][EntityId: {}]", uuid.getClass().getSimpleName(), uuid);
    }

    /**
     * Get entity object with given Id.
     *
     * @param uuid
     * @return
     */
    @Transactional(readOnly = true)
    public E findByUuid(@NotNull final UUID uuid) {
        log.debug("[findByUuid Entity: {}][EntityId: {}]", uuid.getClass().getSimpleName(), uuid);
        return repository.findByUuid(uuid).orElseThrow(() ->
            new ItemNotFoundException("entity-00001", "UUID not found.."));
    }

    public R getRepository() {
        return repository;
    }

    public AuthorizationDetail getContextDetail() {
        return (AuthorizationDetail) SecurityContextHolder.getContext().getAuthentication().getDetails();

    }

    public User getCurrentUser() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    }
}


