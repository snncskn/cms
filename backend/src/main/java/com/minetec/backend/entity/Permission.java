package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PERMISSION", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_PERMISSION__UUID"),
    @UniqueConstraint(columnNames = {"NAME"}, name = "UX_PERMISSION__NAME")})

@Data
@ToString(exclude = {"roles"})
@EqualsAndHashCode(callSuper = true, exclude = {"roles"})
public class Permission extends AbstractEntity {

    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "permissions")
    private List<Role> roles = new ArrayList<>();

}
