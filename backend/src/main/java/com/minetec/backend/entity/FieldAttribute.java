package com.minetec.backend.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * @author Sinan
 */

@Entity
@Table(name = "FIELD_ATTRIBUTE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"UUID"}, name = "UX_FIELD_ATTRIBUTE__UUID"),
    @UniqueConstraint(columnNames = {"FIELD_ID"}, name = "UX_FIELD_ATTRIBUTE__FIELD_ID")
})

@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class FieldAttribute extends AbstractEntity {

    @Column(name = "FIELD_ID", nullable = false, length = 50)
    private String fieldId;

    @Column(name = "LABEL", nullable = false, length = 50)
    private String label;

    @Column(name = "TITLE", nullable = false, length = 200)
    private String title;
}
