package com.minetec.backend.entity;

import com.minetec.backend.entity.workshop.JobCard;
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
    name = "IMAGE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_IMAGE__UUID")},
    indexes = {
        @Index(columnList = "VEHICLE_ID", name = "IX_IMAGE__VEHICLE_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_IMAGE__ITEM_ID"),
        @Index(columnList = "USER_ID", name = "IX_IMAGE__USER_ID"),
        @Index(columnList = "JOB_CARD_ID", name = "IX_IMAGE__JOB_CARD_ID"),
        @Index(columnList = "SUPPLIER_ID", name = "IX_IMAGE__SUPPLIER_ID")
    })

@Data
@ToString(exclude = {"vehicle", "item", "user", "jobCard", "supplier"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicle", "item", "user", "jobCard", "supplier"})
public class Image extends AbstractEntity {

    @Column(name = "SIZE")
    private long size;

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "SELECTED", nullable = false)
    private boolean isSelected = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", foreignKey = @ForeignKey(name = "FK_IMAGE__VEHICLE_ID"))
    private Vehicle vehicle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", foreignKey = @ForeignKey(name = "FK_IMAGE__ITEM_ID"))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", foreignKey = @ForeignKey(name = "FK_IMAGE__USER_ID"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_CARD_ID", foreignKey = @ForeignKey(name = "FK_IMAGE__JOB_CARD_ID"))
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID", foreignKey = @ForeignKey(name = "FK_IMAGE__SUPPLIER_ID"))
    private Supplier supplier;
}
