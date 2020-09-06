package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VEHICLE_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE_TYPE__UUID"),
    @UniqueConstraint(columnNames = {"NAME", "ACTIVE"}, name = "UX_VEHICLE_TYPE__NAME_ACTIVE")
})
@Data
@ToString(exclude = {"vehicleTypeAttributes"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicleTypeAttributes"})
public class VehicleType extends AbstractEntity {

    @Column(name = "NAME", nullable = false, length = 50)
    private String name;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "vehicleAttribute", fetch = FetchType.LAZY)
    private List<VehicleTypeAttribute> vehicleTypeAttributes = new ArrayList<>();

}
