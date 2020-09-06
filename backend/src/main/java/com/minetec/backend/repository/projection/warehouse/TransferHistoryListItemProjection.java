package com.minetec.backend.repository.projection.warehouse;

import com.minetec.backend.dto.enums.OrderStatus;
import com.minetec.backend.repository.projection.UserListItemProjection;

import java.util.UUID;

public interface TransferHistoryListItemProjection {

    UUID getUuid();

    TransferListItemProjection getOrder();

    UserListItemProjection getUser();

    String getDescription();

    OrderStatus getStatus();
}
