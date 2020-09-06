package com.minetec.backend.service;

import com.minetec.backend.dto.info.OrderHistoryInfo;
import com.minetec.backend.dto.mapper.OrderHistoryMapper;
import com.minetec.backend.entity.Order;
import com.minetec.backend.entity.OrderHistory;
import com.minetec.backend.repository.OrderHistoryRepository;
import com.minetec.backend.repository.projection.OrderHistoryListItemProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;

@Service
public class OrderHistoryService extends EntityService<OrderHistory, OrderHistoryRepository> {

    public Page<OrderHistoryListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    public OrderHistoryInfo create(@NotNull final Order order) {
        var entity = new OrderHistory();
        entity.setOrder(order);
        entity.setDescription(order.getRejectionReason());
        entity.setStatus(order.getStatus());
        entity.setUser(this.getCurrentUser());
        var newEntity = this.persist(entity);
        return OrderHistoryMapper.toInfo(newEntity);
    }

}
