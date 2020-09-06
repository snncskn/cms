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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "ITEM",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM__UUID"),
        @UniqueConstraint(columnNames = {"BARCODE"}, name = "UX_ITEM__BARCODE"),
        @UniqueConstraint(columnNames = {"STORE_PART_NUMBER"}, name = "UX_ITEM__STORE_PART_NUMBER")},
    indexes = {
        @Index(columnList = "ITEM_TYPE_ID", name = "IX_ITEM__ITEM_TYPE_ID"),
    })

@Data
@ToString(exclude = {"itemType"})
@EqualsAndHashCode(callSuper = true, exclude = {"itemType"})
public class Item extends AbstractEntity {

    @Column(name = "STORE_PART_NUMBER", nullable = false, unique = true)
    private String storePartNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_TYPE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ITEM__ITEM_TYPE_ID"))
    private ItemType itemType;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "ITEM_DESCRIPTION")
    private String itemDescription;

    @Column(name = "BARCODE", nullable = false, unique = true, length = 50)
    private String barcode;

    @Column(name = "MIN_STOCK_QUANTITY", nullable = false)
    private BigDecimal minStockQuantity;

    @Column(name = "UNIT", length = 20, nullable = false)
    private String unit;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<Stock> stocks = new ArrayList<>();
}
