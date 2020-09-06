package com.minetec.backend.repository.projection;

import java.util.List;
import java.util.UUID;

public interface VehicleAttributeListItemProjection {

    UUID getUuid();

    String getName();

    List<VehicleAttributeValueListItemProjection> getVehicleAttributeValues();
}
