package com.minetec.backend.entity;

import com.minetec.backend.dto.enums.OrderStatus;
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

@Entity
@Table(name = "ORDER_HISTORY",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ORDER_HISTORY__UUID")},
    indexes = {
        @Index(columnList = "ORDER_ID", name = "IX_ORDER_HISTORY__ORDER_ID"),
        @Index(columnList = "USER_ID", name = "IX_ORDER_HISTORY__USER_ID")
    })
@Data
@ToString(exclude = {"order", "user", "status"})
@EqualsAndHashCode(callSuper = true, exclude = {"order", "user", "status"})
public class OrderHistory extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_HISTORY__ORDER_ID"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_HISTORY__USER_ID"))
    private User user;

    @Column(name = "DESCRIPTION", length = 200)
    private String description;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
}
