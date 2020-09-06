package com.minetec.backend.repository.projection;

import java.util.UUID;

public interface SupplierListItemProjection {

    UUID getUuid();

    String getName();

    String getAddress();

    String getRegisterNumber();

    String getTaxNumber();

}
