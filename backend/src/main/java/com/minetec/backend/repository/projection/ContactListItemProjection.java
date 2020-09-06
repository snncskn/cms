package com.minetec.backend.repository.projection;

import java.util.UUID;

public interface ContactListItemProjection {

    UUID getUuid();

    String getName();

    String getRole();

    String getEmail();

    String getPhoneNo();

    String getLandLine();
}
