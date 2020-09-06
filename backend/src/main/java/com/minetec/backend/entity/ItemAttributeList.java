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
    name = "ITEM_ATTRIBUTE_LIST",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM_ATTRIBUTE_LIST__UUID")},
    indexes = {
        @Index(columnList = "ITEM_ID", name = "IX_ITEM_ATTRIBUTE_LIST__ITEM_ID"),
        @Index(columnList = "ATTRIBUTE_ID", name = "IX_ITEM_ATTRIBUTE_LIST__ATTRIBUTE_ID"),
        @Index(columnList = "ATTRIBUTE_VALUE_ID", name = "IX_ITEM_ATTRIBUTE_LIST__ATTRIBUTE_VALUE_ID"),
    })

@Data
@ToString(exclude = {"item", "itemAttribute", "itemAttributeValue"})
@EqualsAndHashCode(callSuper = true, exclude = {"item", "itemAttribute", "itemAttributeValue"})
public class ItemAttributeList extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ITEM_ATTRIBUTE_LIST__ITEM_ID"))
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_VALUE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_ITEM_ATTRIBUTE_LIST__ATTRIBUTE_VALUE_ID"))
    private ItemAttributeValue itemAttributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ATTRIBUTE_ID", nullable = false,
        foreignKey = @ForeignKey(name = "FK_ITEM_ATTRIBUTE_LIST__ATTRIBUTE_ID"))
    private ItemAttribute itemAttribute;

}
