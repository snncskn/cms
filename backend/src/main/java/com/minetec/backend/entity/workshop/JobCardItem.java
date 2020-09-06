package com.minetec.backend.entity.workshop;

import com.minetec.backend.dto.enums.JobCardItemStatus;
import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.Item;
import com.minetec.backend.entity.User;
import com.minetec.backend.util.converter.LocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * @author Sinan
 */
@Entity
@Table(
    name = "JOB_CARD_ITEM",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_JOB_CARD_ITEM__UUID")},
    indexes = {
        @Index(columnList = "JOB_CARD_ID", name = "IX_JOB_CARD_ITEM__JOB_CARD_ID"),
        @Index(columnList = "ITEM_ID", name = "IX_JOB_CARD_ITEM__ITEM_ID"),
        @Index(columnList = "RECEIVED_USER_ID", name = "IX_JOB_CARD_ITEM__RECEIVED_USER_ID"),
        @Index(columnList = "REQUEST_USER_ID", name = "IX_JOB_CARD_ITEM__REQUEST_USER_ID"),
        @Index(columnList = "DELIVERED_USER_ID", name = "IX_JOB_CARD_ITEM__DELIVERED_USER_ID"),

    })
@Data
@ToString(exclude = {"jobCard", "item", "jobCardItemStatus", "receivedUser", "requestUser", "deliveredUser"})
@EqualsAndHashCode(callSuper = true, exclude = {"jobCard", "item", "jobCardItemStatus",
    "receivedUser", "requestUser", "deliveredUser"})
public class JobCardItem extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_CARD_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__JOB_CARD_ID"))
    private JobCard jobCard;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__ITEM_ID"))
    private Item item;

    @Column(name = "STATUS", nullable = false)
    @Enumerated(EnumType.STRING)
    private JobCardItemStatus jobCardItemStatus;

    @Column(name = "QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal quantity = BigDecimal.ZERO;

    @Column(name = "APPROVE_QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal approveQuantity = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RECEIVED_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__RECEIVED_USER_ID"))
    private User receivedUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__REQUEST_USER_ID"))
    private User requestUser;

    @Column(name = "REQUEST_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime requestDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DELIVERED_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__DELIVERED_USER_ID"))
    private User deliveredUser;

    @Column(name = "DELIVERED_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime deliveredDate;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "DESCRIPTION", nullable = false, length = 2000)
    private String description;

    @Transient
    private BigDecimal deliveredQuantity = BigDecimal.ZERO;

    @Transient
    private BigDecimal availableQuantity = BigDecimal.ZERO;

}
