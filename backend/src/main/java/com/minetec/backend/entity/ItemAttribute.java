package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(
    name = "ITEM_ATTRIBUTE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM_ATTRIBUTE__UUID"),
        @UniqueConstraint(columnNames = {"NAME"}, name = "UX_ITEM_ATTRIBUTE__NAME")
    })
@Data
@ToString(exclude = {"itemAttributeValues"})
@EqualsAndHashCode(callSuper = true, exclude = {"itemAttributeValues"})
public class ItemAttribute extends AbstractEntity {

    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    private String name;

    @OneToMany(mappedBy = "itemAttribute", fetch = FetchType.LAZY)
    private List<ItemAttributeValue> itemAttributeValues = new ArrayList<>();

}
