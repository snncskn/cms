package com.minetec.backend.repository.projection;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UserListItemProjection {

    LocalDateTime getLastLoginTime();

    UUID getUuid();

    String getFirstName();

    String getLastName();

    String getContactNumber();

    String getStaffNumber();

    LocalDateTime getExpireDate();

    String getEmail();

    String getAddress();


}
