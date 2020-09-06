package com.minetec.backend.service;

import com.minetec.backend.dto.form.ItemTypeCreateUpdateForm;
import com.minetec.backend.dto.info.ItemTypeInfo;
import com.minetec.backend.dto.mapper.ItemTypeAttributeMapper;
import com.minetec.backend.dto.mapper.ItemTypeMapper;
import com.minetec.backend.entity.ItemType;
import com.minetec.backend.repository.ItemTypeRepository;
import com.minetec.backend.repository.projection.ItemTypeListItemProjection;
import com.minetec.backend.util.Utils;
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
public class ItemTypeService extends EntityService<ItemType, ItemTypeRepository> {

    private final ItemTypeAttributeService itemTypeAttributeService;

    /**
     * @param itemTypeCreateUpdateForm
     * @return
     */
    public ItemTypeInfo create(final ItemTypeCreateUpdateForm itemTypeCreateUpdateForm) {

        var itemType = new ItemType();

        if (itemTypeCreateUpdateForm.getUuid() != null) {
            itemType = this.findByUuid(itemTypeCreateUpdateForm.getUuid());
            var attributesForDelete = itemTypeAttributeService.getRepository().findByItemTypeId(itemType.getId());
            attributesForDelete.forEach(itemTypeAttribute ->
                itemTypeAttributeService.delete(itemTypeAttribute)
            );
        }

        itemType.setName(itemTypeCreateUpdateForm.getVehicleTypeDesc());
        var newItemType = this.persist(itemType);

        itemTypeAttrCreate(itemTypeCreateUpdateForm, newItemType);

        return ItemTypeMapper.toInfo(newItemType);


    }

    /**
     * @param itemTypeCreateUpdateForm
     * @param entity
     */
    private void itemTypeAttrCreate(final ItemTypeCreateUpdateForm itemTypeCreateUpdateForm,
                                    final ItemType entity) {
        itemTypeCreateUpdateForm.getVehicleTypeAttributeCreateUpdateForms().forEach(attributeCreateUpdateForm -> {
            attributeCreateUpdateForm.setVehicleTypeUuid(entity.getUuid());
            attributeCreateUpdateForm.setVehicleAttributeUuid(attributeCreateUpdateForm.getUuid());
            itemTypeAttributeService.create(entity, attributeCreateUpdateForm);
        });
        entity.setItemTypeAttributes(itemTypeAttributeService.getRepository().findByItemTypeId(entity.getId()));
        this.persist(entity);
    }


    /**
     * @param uuid
     */
    public void softDelete(final UUID uuid) {
        var itemType = this.findByUuid(uuid);
        itemType.setActive(false);
        this.persist(itemType);
    }

    /**
     * @param uuid
     * @return
     */
    public ItemTypeInfo find(final UUID uuid) {
        var item = this.findByUuid(uuid);
        var itemTypeInfo = ItemTypeMapper.toInfo(item);
        var itemTypeAttributes = itemTypeAttributeService.getRepository().findByItemTypeId(item.getId());
        itemTypeInfo.setVehicleTypeAttributes(ItemTypeAttributeMapper.toInfos(itemTypeAttributes));
        return itemTypeInfo;
    }

    /**
     * pageable list
     *
     * @param pageable
     * @return<
     */
    public Page<ItemTypeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<ItemTypeListItemProjection> findBy(final String value, final Pageable pageable) {
        final var repValue = Utils.replace(value, "*", "");
        Page<ItemType> types = this.getRepository().findAll(this.getFilter(repValue), pageable);
        final List<ItemTypeInfo> items = types.stream().map(ItemTypeMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, types.getTotalElements());
    }


    /**
     * @param filter
     * @return
     */
    public Specification<ItemType> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }
}

