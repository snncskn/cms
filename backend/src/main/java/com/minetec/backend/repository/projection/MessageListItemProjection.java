package com.minetec.backend.repository.projection;

import java.util.UUID;

public interface MessageListItemProjection {

    UUID getUuid();

    String getMessage();

}
