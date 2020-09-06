package com.minetec.backend.repository.projection;

import java.util.UUID;

public interface PropertyListItemProjection {

    UUID getUuid();

    String getGroupName();

    String getKeyLabel();

    String getKeyValue();
}
