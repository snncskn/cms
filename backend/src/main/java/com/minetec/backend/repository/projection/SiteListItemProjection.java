package com.minetec.backend.repository.projection;


import java.util.UUID;

public interface SiteListItemProjection {
    Long getId();
    UUID getUuid();
    String getDescription();
}
