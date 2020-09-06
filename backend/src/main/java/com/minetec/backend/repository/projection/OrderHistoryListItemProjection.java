package com.minetec.backend.repository.projection;

import com.minetec.backend.dto.enums.OrderStatus;

import java.util.UUID;

public interface OrderHistoryListItemProjection {

    UUID getUuid();

    OrderListItemProjection getOrder();

    UserListItemProjection getUser();

    String getDescription();

    OrderStatus getStatus();
}
