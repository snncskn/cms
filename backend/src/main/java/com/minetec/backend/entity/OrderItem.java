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
    name = "ORDER_ITEM",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ORDER_ITEM__UUID")},
    indexes = {
        @Index(columnList = "ORDER_ID", name = "IX_ORDER_ITEM__ORDER_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_ORDER_ITEM__ITEM_ID")
    })

@Data
@ToString(exclude = {"order", "item", "messages"})
@EqualsAndHashCode(callSuper = true, exclude = {"order", "item", "messages"})
public class OrderItem extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEM__ORDER_ID"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEM__ITEM_ID"))
    private Item item;

    @Column(name = "BARCODE", nullable = false, length = 50)
    private String barcode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "UNIT_PRICE", scale = CURRENCY_SCALE)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_PERCENT", scale = CURRENCY_SCALE)
    private BigDecimal discountPercent = BigDecimal.ZERO;

    @Column(name = "TAX_PERCENT", scale = CURRENCY_SCALE)
    private BigDecimal taxPercent = BigDecimal.ZERO;

    @Column(name = "TOTAL_QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Column(name = "TOTAL", scale = CURRENCY_SCALE)
    private BigDecimal total = BigDecimal.ZERO;

    @Column(name = "TAX_TOTAL", scale = CURRENCY_SCALE)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "UNIT", length = 20, nullable = false)
    private String unit;

    @Column(name = "APPROVE_QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal approveQuantity = BigDecimal.ZERO;

    @Column(name = "APPROVE", nullable = false)
    private boolean approve = true;

    @OneToMany(mappedBy = "orderItem", fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();

}
