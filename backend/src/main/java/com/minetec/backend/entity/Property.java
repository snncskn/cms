package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "PROPERTY", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_PROPERTY__UUID"),
    @UniqueConstraint(columnNames = {"GROUP_NAME", "KEY_LABEL"}, name = "UX_PROPERTY__GROUP_KEY")}
)
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class Property extends AbstractEntity {

    @Column(name = "GROUP_NAME", nullable = false, length = 50)
    private String groupName;

    @Column(name = "KEY_LABEL", nullable = false, length = 50)
    private String keyLabel;

    @Column(name = "KEY_VALUE", nullable = false, length = 50)
    private String keyValue;

}
