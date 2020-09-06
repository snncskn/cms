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
    name = "VEHICLE_ATTRIBUTE_LIST",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE_ATTRIBUTE_LIST__UUID")},
    indexes = {
        @Index(columnList = "VEHICLE_ID", name = "IX_VEHICLE_ATTRIBUTE_LIST__VEHICLE_ID"),
        @Index(columnList = "ATTRIBUTE_ID", name = "IX_VEHICLE_ATTRIBUTE_LIST__ATTRIBUTE_ID"),
        @Index(columnList = "ATTRIBUTE_VALUE_ID", name = "IX_VEHICLE_ATTRIBUTE_LIST__ATTRIBUTE_VALUE_ID"),
    })

@Data
@ToString(exclude = {"vehicle", "vehicleAttribute", "vehicleAttributeValue"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicle", "vehicleAttribute", "vehicleAttributeValue"})
public class VehicleAttributeList extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE_ATTRIBUTE_LIST__VEHICLE_ID"))
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE_ATTRIBUTE_LIST__ATTRIBUTE_ID"))
    private VehicleAttribute vehicleAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_VALUE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE_ATTRIBUTE_LIST__ATTRIBUTE_VALUE_ID"))
    private VehicleAttributeValue vehicleAttributeValue;

}
