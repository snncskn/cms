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
import java.math.BigDecimal;

@Entity
@Table(
    name = "STOCK",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_STOCK__UUID"),
        @UniqueConstraint(columnNames = {"SITE_ID", "ITEM_ID"}, name = "UX_STOCK__SITE_ITEM")},
    indexes = {
        @Index(columnList = "SITE_ID", name = "IX_STOCK__SITE_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_STOCK__ITEM_ID")
    })

@Data
@ToString(exclude = {"site", "item"})
@EqualsAndHashCode(callSuper = true, exclude = {"site", "item"})
public class Stock extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_STOCK__SITE_ID"))
    private Site site;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_STOCK__ITEM_ID"))
    private Item item;

    @Column(name = "QUANTITY", nullable = false, scale = CURRENCY_SCALE)
    private BigDecimal quantity = BigDecimal.ZERO;

}

