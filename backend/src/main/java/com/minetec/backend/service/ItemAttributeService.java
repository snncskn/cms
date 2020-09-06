package com.minetec.backend.service;

import com.minetec.backend.dto.form.ItemAttributeCreateUpdateForm;
import com.minetec.backend.dto.info.ItemAttributeInfo;
import com.minetec.backend.entity.ItemAttribute;
import com.minetec.backend.repository.ItemAttributeRepository;
import com.minetec.backend.repository.projection.ItemAttributeListItemProjection;
import com.minetec.backend.dto.mapper.ItemAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class ItemAttributeService extends EntityService<ItemAttribute, ItemAttributeRepository> {

    private final ItemAttributeValueService itemAttributeValueService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<ItemAttributeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * create
     *
     * @param partAttributeCreateForm
     * @return
     */
    public ItemAttributeInfo create(final ItemAttributeCreateUpdateForm partAttributeCreateForm) {
        if (partAttributeCreateForm.getUuid() == null) {
            var entity = new ItemAttribute();
            entity.setName(partAttributeCreateForm.getVehicleAttribute().toUpperCase());
            this.persist(entity);
            return ItemAttributeMapper.toInfo(entity);
        } else {
            return update(partAttributeCreateForm.getUuid(), partAttributeCreateForm);
        }
    }

    /**
     * update
     *
     * @param partAttributeCreateForm
     * @return
     */
    public ItemAttributeInfo update(final UUID uuid, final ItemAttributeCreateUpdateForm partAttributeCreateForm) {
        var entity = this.findByUuid(uuid);
        entity.setName(partAttributeCreateForm.getVehicleAttribute());
        this.persist(entity);
        return ItemAttributeMapper.toInfo(entity);
    }

    /**
     * @param uuid
     * @return
     */
    public ItemAttributeInfo find(final UUID uuid) {
        return ItemAttributeMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.findByUuid(uuid).getItemAttributeValues().forEach(itemAttributeValue ->
            itemAttributeValueService.deleteById(itemAttributeValue.getId())
        );
        this.deleteByUuid(uuid);
    }

    /**
     * @param filter
     * @param pageable
     * @return
     */
    public Page<ItemAttributeListItemProjection> findBy(final String filter, final Pageable pageable) {
        Page<ItemAttribute> attributes = this.getRepository().findAll(this.getFilter(filter), pageable);
        final List<ItemAttributeInfo> items =
            attributes.stream().map(ItemAttributeMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, attributes.getTotalElements());
    }

    /**
     * @param filter
     * @return
     */
    public Specification<ItemAttribute> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter))
            .toPredicate(root, query, cb);
    }
}

