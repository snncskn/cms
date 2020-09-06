package com.minetec.backend.service;


import com.minetec.backend.dto.form.MessageCreateUpdateForm;
import com.minetec.backend.dto.info.MessageInfo;
import com.minetec.backend.dto.mapper.MessageMapper;
import com.minetec.backend.entity.Message;
import com.minetec.backend.entity.OrderItem;
import com.minetec.backend.repository.MessageRepository;
import com.minetec.backend.repository.projection.MessageListItemProjection;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class MessageService extends EntityService<Message, MessageRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<MessageInfo> findByOrderItemId(final Long orderItemId, final Pageable pageable) {
        Page<Message> messages = this.getRepository().findByOrderItemId(orderItemId, pageable);
        final List<MessageInfo> items = messages.stream().map(MessageMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, messages.getTotalElements());

    }

    /**
     * @param form
     * @return
     */
    public MessageInfo create(@NotNull final OrderItem orderItem,
                              @NotNull final MessageCreateUpdateForm form) {
        var orderItemMessage = new Message();
        orderItemMessage.setOrderItem(orderItem);
        orderItemMessage.setMessage(form.getMessage());
        var newEntity = this.persist(orderItemMessage);
        return MessageMapper.toInfo(newEntity);
    }

    public MessageInfo update(@NotNull final UUID uuid,
                              @NotNull final MessageCreateUpdateForm form) {
        var entity = this.findByUuid(uuid);
        entity.setMessage(form.getMessage());
        var newEntity = this.persist(entity);
        return MessageMapper.toInfo(newEntity);
    }

    /**
     * @param uuid
     * @return
     */
    public MessageInfo find(@NotNull final UUID uuid) {
        return MessageMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(@NotNull final UUID uuid) {
        this.deleteByUuid(uuid);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<MessageListItemProjection> findBy(@NotNull final String value,
                                                  @NotNull final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }

    /**
     * @param filter
     * @return
     */
    public Specification<Message> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("message", filter))
            .toPredicate(root, query, cb);
    }
}
