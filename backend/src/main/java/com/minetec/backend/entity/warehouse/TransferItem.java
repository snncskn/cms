package com.minetec.backend.entity.warehouse;

import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.Item;
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
    name = "TRANSFER_ITEM",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_TRANSFER_ITEM__UUID")},
    indexes = {
        @Index(columnList = "TRANSFER_ID", name = "IX_TRANSFER_ITEM__TRANSFER_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_TRANSFER_ITEM__ITEM_ID")
    })

@Data
@ToString(exclude = {"transfer", "item"})
@EqualsAndHashCode(callSuper = true, exclude = {"transfer", "item"})
public class TransferItem extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_TRANSFER_ITEM__TRANSFER_ID"))
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TRANSFER_ITEM__ITEM_ID"))
    private Item item;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "APPROVE_QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal approveQuantity = BigDecimal.ZERO;

    @Column(name = "APPROVE", nullable = false)
    private boolean approve = true;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

}
