package com.minetec.backend.entity;

import com.minetec.backend.annotation.Filter;
import com.minetec.backend.util.converter.LocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sinan
 */

@Entity
@Table(
    name = "VEHICLE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_VEHICLE__UUID"),
        @UniqueConstraint(columnNames = {"FLEET_NO"}, name = "UX_VEHICLE__FLEET_NO")},
    indexes = {
        @Index(columnList = "FLEET_NO, SITE_ID", name = "IX_VEHICLE__FLEET_SITE"),
        @Index(columnList = "SITE_ID", name = "IX_VEHICLE__SITE_ID"),
        @Index(columnList = "VEHICLE_TYPE_ID", name = "IX_VEHICLE__VEHICLE_TYPE_ID")
    })
@Data
@ToString(exclude = {"site", "vehicleType"})
@EqualsAndHashCode(callSuper = true, exclude = {"site", "vehicleType"})
public class Vehicle extends AbstractEntity {

    @Filter(like = true)
    @Column(name = "FLEET_NO", unique = true)
    private String fleetNo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_VEHICLE__SITE_ID"))
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_TYPE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_VEHICLE__VEHICLE_TYPE_ID"))
    private VehicleType vehicleType;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "VIN_NO")
    private String vinNo;

    @Column(name = "SERIAL_NO")
    private String serialNo;

    @Column(name = "UNIT")
    private String unit;

    @Column(name = "USABLE")
    private boolean isUsable = true;

    @Column(name = "REGISTRATION_NO")
    private String registrationNo;

    @Column(name = "CURRENT_MACHINE_HOURS")
    private String currentMachineHours;

    @Column(name = "SERVICE_INTERVAL_HOURS", nullable = false)
    private String serviceIntervalHours;

    @Column(name = "LAST_SERVICE_DATE", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastServiceDate;

    @Column(name = "LAST_SERVICE_HOURS")
    private String lastServiceHours;

    @Column(name = "REMAINING_HOURS", nullable = false)
    private String remainingHours;

    @OneToMany(mappedBy = "vehicle", fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

}
