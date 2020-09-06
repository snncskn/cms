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
    name = "SUPPLIER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_SUPPLIER__UUID"),
        @UniqueConstraint(columnNames = {"NAME"}, name = "UX_SUPPLIER__NAME"),
        @UniqueConstraint(columnNames = {"REGISTER_NUMBER"}, name = "UX_SUPPLIER__REGISTER_NUMBER"),
        @UniqueConstraint(columnNames = {"TAX_NUMBER"}, name = "UX_SUPPLIER__TAX_NUMBER")
    })

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class Supplier extends AbstractEntity {

    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "ADDRESS", nullable = false, length = 300)
    private String address;

    @Column(name = "REGISTER_NUMBER", nullable = false, length = 70, unique = true)
    private String registerNumber;

    @Column(name = "TAX_NUMBER", length = 70, unique = true)
    private String taxNumber;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<Contact> contacts = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "supplier", fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

}
