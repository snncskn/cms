package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;

@Entity
@Table(
    name = "CONTACT",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_CONTACT__UUID"),
        @UniqueConstraint(columnNames = {"NAME"}, name = "UX_CONTACT__NAME")},
    indexes = {
        @Index(columnList = "SUPPLIER_ID", name = "IX_CONTACT__SUPPLIER_ID")}
)

@Data
@ToString(exclude = {"supplier"})
@EqualsAndHashCode(callSuper = true, exclude = {"supplier"})
public class Contact extends AbstractEntity {

    @Column(name = "NAME", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "ROLE", length = 50)
    private String role;

    @Column(name = "EMAIL", length = 120)
    private String email;

    @Column(name = "PHONE_NO", length = 20)
    private String phoneNo;

    @Column(name = "LAND_LINE", length = 20)
    private String landLine;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPPLIER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_CONTACT__SUPPLIER_ID"))
    private Supplier supplier;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

}
