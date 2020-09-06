package com.minetec.backend.repository;

import com.minetec.backend.entity.FieldAttribute;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FieldAttributeRepository extends BaseRepository<FieldAttribute> {
    Optional<FieldAttribute> findByFieldId(String fieldId);
}
