package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

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
    name = "ITEM_TYPE_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM_TYPE_ATTRIBUTE__UUID")},
    indexes = {
        @Index(columnList = "ITEM_TYPE_ID", name = "IX_ITEM_TYPE_ATTRIBUTE__ITEM_TYPE_ID"),
        @Index(columnList = "ITEM_ATTRIBUTE_ID", name = "IX_ITEM_TYPE_ATTRIBUTE__ITEM_ATTRIBUTE_ID"),
    })

@Data
@ToString(exclude = {"itemType", "itemAttribute"})
@EqualsAndHashCode(callSuper = true, exclude = {"itemType", "itemAttribute"})
public class ItemTypeAttribute extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_TYPE_ID", foreignKey = @ForeignKey(name = "FK_ITEM_TYPE_ATTRIBUTE__ITEM_TYPE_ID"))
    private ItemType itemType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ATTRIBUTE_ID",
        foreignKey = @ForeignKey(name = "FK_ITEM_TYPE_ATTRIBUTE__ITEM_ATTRIBUTE_ID"))
    private ItemAttribute itemAttribute;

}
