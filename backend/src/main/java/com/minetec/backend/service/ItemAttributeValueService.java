package com.minetec.backend.service;


import com.minetec.backend.dto.form.ItemAttributeValueCreateUpdateForm;
import com.minetec.backend.dto.info.ItemAttributeValueInfo;
import com.minetec.backend.entity.ItemAttribute;
import com.minetec.backend.entity.ItemAttributeValue;
import com.minetec.backend.repository.ItemAttributeValueRepository;
import com.minetec.backend.repository.projection.ItemAttributeValueListItemProjection;
import com.minetec.backend.dto.mapper.ItemAttributeValueMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class ItemAttributeValueService extends EntityService<ItemAttributeValue, ItemAttributeValueRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<ItemAttributeValueListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param form
     * @return
     */
    public ItemAttributeValueInfo create(@NotNull final ItemAttribute itemAttribute,
                                         @NotNull final ItemAttributeValueCreateUpdateForm form) {
        var entity = new ItemAttributeValue();
        if (form.getAttributeValueUuid() == null) {
            entity.setValue(form.getDesc());
            entity.setItemAttribute(itemAttribute);
            return ItemAttributeValueMapper.toInfo(this.persist(entity));
        } else {
            return this.update(form.getAttributeValueUuid(), form);
        }

    }

    public ItemAttributeValueInfo update(@NotNull final UUID uuid,
                                         @NotNull final ItemAttributeValueCreateUpdateForm form) {
        var entity = this.findByUuid(uuid);
        entity.setValue(form.getDesc());
        this.persist(entity);
        return ItemAttributeValueMapper.toInfo(entity);
    }

    /**
     * @param uuid
     * @return
     */
    public ItemAttributeValueInfo find(@NotNull final UUID uuid) {
        return ItemAttributeValueMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(@NotNull final UUID uuid) {
        this.deleteByUuid(uuid);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<ItemAttributeValueListItemProjection> findBy(@NotNull final String value,
                                                             @NotNull final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }


    /**
     * @param filter
     * @return
     */
    public Specification<ItemAttributeValueListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("value", filter))
            .toPredicate(root, query, cb);
    }
}
