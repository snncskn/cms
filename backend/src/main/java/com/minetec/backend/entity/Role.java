package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Sinan
 * <p>
 * <p>
 * role
 * -----------------
 * id
 * name
 * ==========================================
 * 1      ROLE_ADMIN
 * 2      ROLE_WH_MANAGER
 * <p>
 * <p>
 * permission
 * -----------------
 * id
 * name
 * ==========================================
 * 1      ITEM_LIST
 * 2      ITEM_DETAIL
 * 3      ITEM_CREATE
 * <p>
 * role_permission
 * -----------------
 * |-role_id
 * |-permission_id
 * ==========================================
 * 1 1
 * 1 2
 * 1 3
 * <p>
 * /login
 * <p>
 * {
 * token: 'addadad'
 * permissions [
 * 'ITEM_LIST', 'ITEM_DETAIL'
 * ]
 * }
 * <p>
 * /permissions
 * <p>
 * <div ng-if="permissionService.hasPermission('ITEM_DETAIL')">
 * </div>
 */

@Entity
@Table(name = "ROLE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_ROLE__UUID"),
    @UniqueConstraint(columnNames = {"NAME"}, name = "UX_ROLE__NAME")})

@Data
@ToString(exclude = {"users", "permissions"})
@EqualsAndHashCode(callSuper = true, exclude = {"users", "permissions"})
public class Role extends AbstractEntity {

    @Column(name = "NAME", unique = true, nullable = false, length = 50)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private List<User> users = new ArrayList<>();

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "ROLE_PERMISSION", joinColumns = {
        @JoinColumn(name = "ROLE_ID", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "PERMISSION_ID", nullable = false, updatable = false)})
    private List<Permission> permissions = new ArrayList<>();

}
