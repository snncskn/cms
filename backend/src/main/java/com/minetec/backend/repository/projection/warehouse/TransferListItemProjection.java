package com.minetec.backend.repository.projection.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import com.minetec.backend.repository.projection.SiteListItemProjection;
import com.minetec.backend.repository.projection.UserListItemProjection;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * @author Sinan
 */
public interface TransferListItemProjection {

    UUID getUuid();

    SiteListItemProjection getSourceSite();

    SiteListItemProjection getTargetSite();

    TransferStatus getStatus();

    String getTransferNumber();

    String getRejectionReason();

    UserListItemProjection getSourceOwner();

    UserListItemProjection getTargetOwner();

    LocalDateTime getRequestDate();

    LocalDateTime getTransferDate();

    LocalDateTime getDeliverDate();

}
