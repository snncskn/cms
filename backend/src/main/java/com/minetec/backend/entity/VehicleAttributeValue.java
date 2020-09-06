package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
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
    name = "VEHICLE_ATTRIBUTE_VALUE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"VALUE", "VEHICLE_ATTRIBUTE_ID"},
            name = "UX_VEHICLE_ATTRIBUTE_VALUE__VALUE__ATTR_ID"),
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE_ATTRIBUTE_VALUE__UUID")},
    indexes = {
        @Index(columnList = "VEHICLE_ATTRIBUTE_ID", name = "IX_VEHICLE_ATTRIBUTE_VALUE__VEHICLE_ATTRIBUTE_ID"),
    })
@Data
@ToString(exclude = {"vehicleAttribute"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicleAttribute"})
public class VehicleAttributeValue extends AbstractEntity {

    @Column(name = "VALUE", nullable = false, length = 100)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ATTRIBUTE_ID",
        foreignKey = @ForeignKey(name = "FK_VEHICLE_ATTRIBUTE_VALUE__VEHICLE_ATTRIBUTE_ID"))
    private VehicleAttribute vehicleAttribute;

}
