package com.minetec.backend.service;


import com.minetec.backend.dto.form.ItemTypeAttributeCreateUpdateForm;
import com.minetec.backend.dto.info.ItemTypeAttributeInfo;
import com.minetec.backend.entity.ItemType;
import com.minetec.backend.entity.ItemTypeAttribute;
import com.minetec.backend.repository.ItemTypeAttributeRepository;
import com.minetec.backend.repository.projection.ItemTypeAttributeListItemProjection;
import com.minetec.backend.dto.mapper.ItemTypeAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemTypeAttributeService extends EntityService<ItemTypeAttribute, ItemTypeAttributeRepository> {

    private final ItemAttributeService itemAttributeService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<ItemTypeAttributeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }


    /**
     * save
     *
     * @param itemTypeAttributeCreateForm
     * @return
     */
    public ItemTypeAttributeInfo create(final ItemType itemType,
                                        final ItemTypeAttributeCreateUpdateForm itemTypeAttributeCreateForm) {
        var entity = new ItemTypeAttribute();
        entity.setItemType(itemType);
        entity.setItemAttribute(
            itemAttributeService.findByUuid(itemTypeAttributeCreateForm.getVehicleAttributeUuid()));
        this.persist(entity);
        return ItemTypeAttributeMapper.toInfo(entity);
    }


    /**
     * @param uuid
     * @return
     */
    public ItemTypeAttributeInfo find(final UUID uuid) {
        return ItemTypeAttributeMapper.toInfo(this.findByUuid(uuid));
    }

}
