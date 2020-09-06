package com.minetec.backend.entity.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.Site;
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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sinan
 */
@Entity
@Table(
    name = "TRANSFER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_TRANSFER__UUID")},
    indexes = {
        @Index(columnList = "SOURCE_SITE_ID", name = "IX_TRANSFER__SOURCE_SITE_ID"),
        @Index(columnList = "TARGET_SITE_ID", name = "IX_TRANSFER__TARGET_SITE_ID"),
        @Index(columnList = "SOURCE_OWNER_ID", name = "IX_TRANSFER__SOURCE_OWNER_ID"),
        @Index(columnList = "TARGET_OWNER_ID", name = "IX_TRANSFER__TARGET_OWNER_ID")
    })
@Data
@ToString(exclude = {"sourceSite", "targetSite", "transferItems"})
@EqualsAndHashCode(callSuper = true, exclude = {"sourceSite", "targetSite", "transferItems"})
public class Transfer extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_SITE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_TRANSFER__SOURCE_SITE_ID"))
    private Site sourceSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_SITE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_TRANSFER__TARGET_SITE_ID"))
    private Site targetSite;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SOURCE_OWNER_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_TRANSFER__SOURCE_OWNER_ID"))
    private User sourceOwner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TARGET_OWNER_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_TRANSFER__TARGET_OWNER_ID"))
    private User targetOwner;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TransferStatus status;

    @Column(name = "TRANSFER_NUMBER", length = 50)
    private String transferNumber;

    @Column(name = "REQUEST_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime requestDate;

    @Column(name = "TRANSFER_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime transferDate;

    @Column(name = "DELIVER_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime deliverDate;

    @Column(name = "REJECTION_REASON")
    private String rejectionReason;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "transfer", fetch = FetchType.LAZY)
    private List<TransferItem> transferItems = new ArrayList<>();

}
