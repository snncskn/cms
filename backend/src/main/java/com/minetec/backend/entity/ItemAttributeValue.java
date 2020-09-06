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

@Entity
@Table(
    name = "ITEM_ATTRIBUTE_VALUE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"VALUE", "ITEM_ATTRIBUTE_ID"}, name = "UX_ITEM_ATTR_VALUE__VALUE__ID"),
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM_ATTRIBUTE_VALUE__UUID")},
    indexes = {
        @Index(columnList = "ITEM_ATTRIBUTE_ID", name = "IX_ITEM_ATTRIBUTE_VALUE__ITEM_ATTRIBUTE_ID"),
    })

@Data
@ToString(exclude = {"itemAttribute"})
@EqualsAndHashCode(callSuper = true, exclude = {"itemAttribute"})
public class ItemAttributeValue extends AbstractEntity {

    @Column(name = "VALUE", nullable = false, length = 20)
    private String value;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ITEM_ATTRIBUTE_ID", foreignKey = @ForeignKey(name = "FK_ITEM_ATTRIBUTE_VALUE__ITEM_ATTR_ID"))
    private ItemAttribute itemAttribute;

}
