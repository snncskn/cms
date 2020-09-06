package com.minetec.backend.entity;

import com.minetec.backend.util.converter.LocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Sinan
 */

@Entity
@Table(name = "USER",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_USER__UUID"),
        @UniqueConstraint(columnNames = {"EMAIL"}, name = "UX_USER__EMAIL")}
)
@Data
@ToString(exclude = {"roles", "sites", "images"})
@EqualsAndHashCode(callSuper = true, exclude = {"roles", "sites", "images"})
public class User extends AbstractEntity {

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "FIRST_NAME", nullable = false)
    private String firstName;

    @Column(name = "LAST_NAME", nullable = false)
    private String lastName;

    @Column(name = "EXPIRE_DATE", nullable = false)
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime expireDate;

    @Column(name = "EMAIL", unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "CONTACT_NUMBER", nullable = false)
    private String contactNumber;

    @Column(name = "ADDRESS", length = 500)
    private String address;

    @Column(name = "LOCKED")
    private boolean locked = false;

    @Column(name = "LAST_LOGIN_TIME")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime lastLoginTime;

    @Column(name = "PASSWORD", length = 500)
    private String password;

    @Column(name = "POSITION", length = 50)
    private String position;

    @Column(name = "STAFF_NUMBER", length = 50)
    private String staffNumber;

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE", joinColumns = {
        @JoinColumn(name = "USER_ID", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "ROLE_ID", nullable = false, updatable = false)})
    private List<Role> roles = new ArrayList<>();

    @Fetch(FetchMode.SUBSELECT)
    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinTable(name = "USER_SITE", joinColumns = {
        @JoinColumn(name = "USER_ID", nullable = false, updatable = false)},
        inverseJoinColumns = {@JoinColumn(name = "SITE_ID", nullable = false, updatable = false)})
    private List<Site> sites = new LinkedList<>();

    @OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
    private List<Image> images = new ArrayList<>();

}
