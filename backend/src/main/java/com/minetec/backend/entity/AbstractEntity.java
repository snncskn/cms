package com.minetec.backend.entity;

import lombok.AccessLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@MappedSuperclass
@Setter(AccessLevel.NONE)
@EntityListeners(AuditingEntityListener.class)
@EqualsAndHashCode(exclude = {"createdBy", "modifiedBy", "createdAt", "modifiedAt"})
public abstract class AbstractEntity implements Persistable<Long>, Serializable {

    public static final int CURRENCY_SCALE = 4;

    @Id
    @Column(name = "ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "UUID", columnDefinition = "uuid", nullable = false)
    private UUID uuid = UUID.randomUUID();

    @CreatedBy
    @Column(name = "CREATED_BY", nullable = false, updatable = false)
    private Long createdBy;

    @CreatedDate
    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedBy
    @Column(name = "MODIFIED_BY")
    private Long modifiedBy;

    @LastModifiedDate
    @Column(name = "MODIFIED_AT")
    @Setter
    private LocalDateTime modifiedAt;

    public boolean isNew() {
        return null == getId();
    }

    @Override
    public Long getId() {
        return id;
    }
}
