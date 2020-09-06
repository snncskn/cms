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
@Table(name = "VEHICLE_ATTRIBUTE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE_ATTRIBUTE__UUID"),
    @UniqueConstraint(columnNames = {"NAME"}, name = "UX_VEHICLE_ATTRIBUTE__NAME"),
})

@Data
@ToString(exclude = {"vehicleAttributeValues"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicleAttributeValues"})
public class VehicleAttribute extends AbstractEntity {

    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "vehicleAttribute", fetch = FetchType.LAZY)
    private List<VehicleAttributeValue> vehicleAttributeValues = new ArrayList<>();
}
