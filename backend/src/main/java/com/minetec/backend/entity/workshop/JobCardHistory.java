package com.minetec.backend.entity.workshop;

import com.minetec.backend.entity.AbstractEntity;
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


@Entity
@Table(
    name = "JOB_CARD_HISTORY",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_JOB_CARD_HISTORY__UUID")},
    indexes = {
        @Index(columnList = "JOB_CARD_ID", name = "IX_JOB_CARD_HISTORY__JOB_CARD_ID"),
    })

@Data
@ToString(exclude = {"jobCard"})
@EqualsAndHashCode(callSuper = true, exclude = {"jobCard"})
public class JobCardHistory extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "JOB_CARD_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_JOB_CARD_ITEM__JOB_CARD_ID"))
    private JobCard jobCard;

    @Column(name = "DESCRIPTION", nullable = false)
    private String description;

}
