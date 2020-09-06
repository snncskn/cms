package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "SITE",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_SITE__UUID"),
        @UniqueConstraint(columnNames = {"DESCRIPTION"}, name = "UX_SITE__DESCRIPTION")},
    indexes = {
        @Index(columnList = "SUPPLIER_ID", name = "IX_SITE__SUPPLIER_ID"),
    })

@Data
@ToString(exclude = {"orders", "users"})
@EqualsAndHashCode(callSuper = true, exclude = {"orders", "users"})
public class Site extends AbstractEntity {

    @Column(name = "DESCRIPTION", unique = true)
    private String description;

    @OneToMany(mappedBy = "site", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(mappedBy = "sites", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @Column(name = "SUPPLIER_ID")
    private Long supplierId;
}
