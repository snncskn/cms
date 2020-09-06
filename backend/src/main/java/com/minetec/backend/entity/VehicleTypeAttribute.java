package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(
    name = "VEHICLE_TYPE_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE_TYPE_ATTRIBUTE__UUID")},
    indexes = {
        @Index(columnList = "VEHICLE_TYPE_ID", name = "IX_VEHICLE_TYPE_ATTRIBUTE__VEHICLE_TYPE_ID"),
        @Index(columnList = "VEHICLE_ATTRIBUTE_ID", name = "IX_VEHICLE_TYPE_ATTRIBUTE__VEHICLE_ATTRIBUTE_ID"),
    })

@Data
@ToString(exclude = {"vehicleType", "vehicleAttribute"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicleType", "vehicleAttribute"})
public class VehicleTypeAttribute extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_TYPE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE_TYPE_ATTRIBUTE__VEHICLE_TYPE_ID"))
    private VehicleType vehicleType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ATTRIBUTE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE_TYPE_ATTRIBUTE__VEHICLE_ATTRIBUTE_ID"))
    private VehicleAttribute vehicleAttribute;
}
