package com.minetec.backend.entity.warehouse;

import com.minetec.backend.dto.enums.TransferStatus;
import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.User;
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
@Table(name = "TRANSFER_HISTORY",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_TRANSFER_HISTORY__UUID")},
    indexes = {
        @Index(columnList = "TRANSFER_ID", name = "IX_TRANSFER_HISTORY__TRANSFER_ID"),
        @Index(columnList = "USER_ID", name = "IX_TRANSFER_HISTORY__USER_ID")
    })
@Data
@ToString(exclude = {"transfer"})
@EqualsAndHashCode(callSuper = true, exclude = {"transfer"})
public class TransferHistory extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TRANSFER_ID", nullable = false, foreignKey =
        @ForeignKey(name = "FK_TRANSFER_HISTORY__TRANSFER_ID"))
    private Transfer transfer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_TRANSFER_HISTORY__USER_ID"))
    private User user;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private TransferStatus status;
}
