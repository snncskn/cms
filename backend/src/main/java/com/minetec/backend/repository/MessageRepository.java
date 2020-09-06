package com.minetec.backend.repository;

import com.minetec.backend.entity.Message;
import com.minetec.backend.repository.projection.MessageListItemProjection;
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
public interface MessageRepository extends BaseRepository<Message> {

    @Query("select m from Message m ")
    Page<MessageListItemProjection> list(Pageable pageable);

    Page<Message> findByOrderItemId(Long orderItemId, Pageable pageable);

    Page<MessageListItemProjection> findAll(@NotNull Specification specification, @NotNull Pageable pageable);

}
