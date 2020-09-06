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
@Table(name = "ITEM_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ITEM_TYPE__UUID"),
    @UniqueConstraint(columnNames = {"NAME", "ACTIVE"}, name = "UX_ITEM_TYPE__NAME_ACTIVE")
})
@Data
@ToString(exclude = {"itemTypeAttributes"})
@EqualsAndHashCode(callSuper = true, exclude = {"itemTypeAttributes"})
public class ItemType extends AbstractEntity {

    @Column(name = "NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "itemAttribute", fetch = FetchType.LAZY)
    private List<ItemTypeAttribute> itemTypeAttributes = new ArrayList<>();
}
