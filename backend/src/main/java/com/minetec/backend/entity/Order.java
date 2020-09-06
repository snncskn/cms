package com.minetec.backend.entity;

import com.minetec.backend.dto.enums.OrderStatus;
import com.minetec.backend.util.converter.LocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Enumerated;
import javax.persistence.Convert;
import javax.persistence.OneToMany;
import javax.persistence.EnumType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sinan
 */
@Entity
@Table(
    name = "ORDER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ORDER__UUID")},
    indexes = {
        @Index(columnList = "SUPPLIER_ID", name = "IX_ORDER__SUPPLIER_ID"),
        @Index(columnList = "SITE_ID", name = "IX_ORDER__SITE_ID"),

    })
@Data
@ToString(exclude = {"supplier", "site"})
@EqualsAndHashCode(callSuper = true, exclude = {"supplier", "site"})
public class Order extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER__SUPPLIER_ID"))
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SITE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER__SITE_ID"))
    private Site site;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "REQUEST_NUMBER", nullable = false, length = 50)
    private String requestNumber;

    @Column(name = "ORDER_NUMBER", length = 50)
    private String orderNumber;

    @Column(name = "INVOICE_NUMBER", length = 50)
    private String invoiceNumber;

    @Column(name = "REFERENCE_NUMBER", length = 50)
    private String referenceNumber;

    @Column(name = "ADDRESS_DETAIL", length = 500)
    private String addressDetail;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    @Column(name = "REQUEST_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime requestDate;

    @Column(name = "ORDER_CREATION_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime orderCreationDate;

    @Column(name = "INVOICE_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime invoiceDate;

    @Column(name = "GRAND_TOTAL", scale = CURRENCY_SCALE)
    private BigDecimal grandTotal = BigDecimal.ZERO;

    @Column(name = "TAX_TOTAL", scale = CURRENCY_SCALE)
    private BigDecimal taxTotal = BigDecimal.ZERO;

    @Column(name = "DISCOUNT_TOTAL", scale = CURRENCY_SCALE)
    private BigDecimal discountTotal = BigDecimal.ZERO;

    @Column(name = "TOTAL_QUANTITY", scale = CURRENCY_SCALE)
    private BigDecimal totalQuantity = BigDecimal.ZERO;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY)
    private List<OrderItem> orderItems = new ArrayList<>();

}
