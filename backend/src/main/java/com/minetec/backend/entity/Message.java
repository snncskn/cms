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

/**
 * @author Sinan
 */

@Entity
@Table(name = "MESSAGE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_MESSAGE__UUID")},
    indexes = {
        @Index(columnList = "ORDER_ITEM_ID", name = "IX_MESSAGE__ORDER_ITEM_ID"),
    })

@Data
@ToString(exclude = {"orderItem"})
@EqualsAndHashCode(callSuper = true, exclude = {"orderItem"})
public class Message extends AbstractEntity {

    @Column(name = "MESSAGE", nullable = false, length = 2000)
    private String message;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ITEM_ID", foreignKey = @ForeignKey(name = "FK_MESSAGE__ORDER_ITEM_ID"))
    private OrderItem orderItem;
}
