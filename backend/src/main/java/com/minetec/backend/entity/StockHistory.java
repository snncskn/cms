package com.minetec.backend.entity;

import com.minetec.backend.dto.enums.StockType;
import com.minetec.backend.entity.warehouse.TransferItem;
import com.minetec.backend.entity.workshop.JobCardItem;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
    name = "STOCK_HISTORY",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_STOCK_HISTORY__UUID")},
    indexes = {
        @Index(columnList = "ORDER_ITEM_ID", name = "IX_STOCK_HISTORY__ORDER_ITEM_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_STOCK_HISTORY__ITEM_ID"),
        @Index(columnList = "SOURCE_SITE_ID", name = "IX_STOCK_HISTORY__SOURCE_SITE_ID"),
        @Index(columnList = "TARGET_SITE_ID", name = "IX_STOCK_HISTORY___TARGET_SITE_ID"),
        @Index(columnList = "JOB_CARD_ITEM_ID", name = "IX_STOCK_HISTORY__JOB_CARD_ITEM_ID"),
        @Index(columnList = "TRANSFER_ITEM_ID", name = "IX_STOCK_HISTORY__TRANSFER_ITEM_ID"),
        @Index(columnList = "RECEIVED_USER_ID", name = "IX_STOCK_HISTORY__RECEIVED_USER_ID"),
    })

@Data
@ToString(exclude = {"transferItem", "jobCardItem", "orderItem", "item", "sourceSite", "targetSite"})
@EqualsAndHashCode(callSuper = true, exclude = {"transferItem", "jobCardItem",
    "orderItem", "item", "sourceSite", "targetSite"})
public class StockHistory extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_ITEM_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__TRANSFER_ITEM_ID"))
    private TransferItem transferItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_CARD_ITEM_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__JOB_CARD_ITEM_ID"))
    private JobCardItem jobCardItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ITEM_ID", foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__ORDER_ITEM_ID"))
    private OrderItem orderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__ITEM_ID"))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_SITE_ID", foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__SOURCE_SITE_ID"))
    private Site sourceSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_SITE_ID", foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__TARGET_SITE_ID"))
    private Site targetSite;

    @Column(name = "QUANTITY", scale = CURRENCY_SCALE, nullable = false)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private StockType stockType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVED_USER_ID", foreignKey = @ForeignKey(name = "FK_STOCK_HISTORY__RECEIVED_USER_ID"))
    private User receivedUser;


}

