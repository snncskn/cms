package com.minetec.backend.service;

import com.minetec.backend.dto.info.FieldAttributeInfo;
import com.minetec.backend.error_handling.exception.ItemNotFoundException;
import com.minetec.backend.repository.FieldAttributeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Service
@RequiredArgsConstructor
public class FieldAttributeService {
    private final FieldAttributeRepository fieldAttributeRepository;

    public FieldAttributeInfo findByField(@NotNull @NotEmpty final String fieldId) {
        final var ret = fieldAttributeRepository.findByFieldId(fieldId).orElseThrow(() ->
            new ItemNotFoundException("entity-00005", "fieldId not found.."));
        var fai = new FieldAttributeInfo();
        fai.setLabel(ret.getLabel());
        fai.setTitle(ret.getTitle());
        return fai;
    }
}
