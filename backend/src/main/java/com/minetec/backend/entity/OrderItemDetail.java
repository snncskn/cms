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
    name = "ORDER_ITEM_DETAIL",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ORDER_ITEM_DETAIL_UUID")},
    indexes = {
        @Index(columnList = "ORDER_ITEM_ID", name = "IX_ORDER_ITEM_DETAIL__ORDER_ITEM_ID")
    })

@Data
@ToString(exclude = {"orderItem"})
@EqualsAndHashCode(callSuper = true, exclude = {"orderItem"})
public class OrderItemDetail extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ITEM_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK__ORDER_ITEM_DETAIL__ORDER_ITEM_ID"))
    private OrderItem orderItem;

    @Column(name = "QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "DESCRIPTION")
    private String description;

}
