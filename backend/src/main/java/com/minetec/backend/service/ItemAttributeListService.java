package com.minetec.backend.service;

import com.minetec.backend.dto.form.ItemAttributeListCreateForm;
import com.minetec.backend.dto.form.ItemAttributeListUpdateForm;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.ItemAttributeListInfo;
import com.minetec.backend.entity.Image;
import com.minetec.backend.entity.Item;
import com.minetec.backend.entity.ItemAttributeList;
import com.minetec.backend.repository.ItemAttributeListRepository;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.ItemAttributeListMapper;
import com.minetec.backend.dto.mapper.ItemMapper;
import com.minetec.backend.dto.mapper.ItemTypeAttributeMapper;
import com.minetec.backend.dto.mapper.ItemTypeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ItemAttributeListService extends EntityService<ItemAttributeList, ItemAttributeListRepository> {

    private final ItemTypeService itemTypeService;

    private final ItemTypeAttributeService itemTypeAttributeService;

    private final ItemAttributeService itemAttributeService;

    private final ItemAttributeValueService itemAttributeValueService;

    private final ImageService imageService;

    /**
     * create
     *
     * @param form
     * @return
     */
    public ItemAttributeListInfo create(final Item item, final ItemAttributeListCreateForm form) {

        this.deleteAll(this.getRepository().findByItemId(item.getId()));

        var attributeLists = new ArrayList<ItemAttributeList>();
        form.getVehicleAttributeListDetailCreateForm().forEach(partAttributeListDetailCreateForm -> {
            var itemAttributeList = new ItemAttributeList();
            itemAttributeList.setItem(item);
            itemAttributeList.setItemAttribute(itemAttributeService.findByUuid(
                partAttributeListDetailCreateForm.getVehicleAttributeUuid()));
            itemAttributeList.setItemAttributeValue(itemAttributeValueService.findByUuid(
                partAttributeListDetailCreateForm.getVehicleAttributeValueUuid()));
            var newItemAttributeList = this.persist(itemAttributeList);
            attributeLists.add(newItemAttributeList);
        });

        var itemInfo = ItemMapper.toInfo(item);

        itemInfo.setItemTypeInfo(ItemTypeMapper.toInfo(item.getItemType()));

        itemInfo.setImageInfos(getImageInfos(item));

        var itemAttributeListInfo = ItemAttributeListMapper.toInfo(attributeLists);
        itemAttributeListInfo.setPartInfo(itemInfo);

        return itemAttributeListInfo;
    }

    /**
     * @param item
     * @return
     */
    private ArrayList<ImageInfo> getImageInfos(final Item item) {
        List<Image> images = imageService.getRepository().findByItemId(item.getId());
        var imageUuidList = new ArrayList<ImageInfo>();
        images.forEach(image -> {
            var imageInfo = ImageMapper.toInfo(image);
            imageInfo.setDownloadUrl(imageService.imageUrl(image.getUuid()));
            imageUuidList.add(imageInfo);
        });
        return imageUuidList;
    }

    /**
     * update
     *
     * @param uuid
     * @param partDetailUpdateForm
     * @return
     */
    public ItemAttributeListInfo update(final UUID uuid, final ItemAttributeListUpdateForm partDetailUpdateForm) {
        return new ItemAttributeListInfo();
    }

    /**
     * @param item
     * @return
     */
    public ItemAttributeListInfo find(final Item item) {

        var itemList = this.getRepository().findByItemId(item.getId());

        var listAttrValues = new ArrayList<UUID>();
        itemList.forEach(itemAttributeList ->
            listAttrValues.add(itemAttributeList.getItemAttributeValue().getUuid())
        );

        var itemListInfo = new ItemAttributeListInfo();
        itemListInfo.setPartInfo(ItemMapper.toInfo(item));

        var itemType = itemTypeService.findByUuid(item.getItemType().getUuid());
        itemListInfo.getPartInfo().setItemTypeInfo(ItemTypeMapper.toInfo(itemType));

        var itemTypeAttributes = itemTypeAttributeService.getRepository().findByItemTypeId(itemType.getId());
        itemListInfo.getPartInfo().getItemTypeInfo().setVehicleTypeAttributes(
            ItemTypeAttributeMapper.toInfos(itemTypeAttributes));


        itemListInfo.getPartInfo().getItemTypeInfo().getVehicleTypeAttributes().forEach(itemTypeAttributeInfo -> {
            itemList.forEach(itemAttributeList -> {
                if (itemTypeAttributeInfo.getUuid().equals(itemAttributeList.getItemAttribute().getUuid())) {
                    itemTypeAttributeInfo.setSelectedVehicleAttrUuid(
                        itemAttributeList.getItemAttributeValue().getUuid());
                }
            });

        });

        itemListInfo.getPartInfo().setImageInfos(getImageInfos(item));

        return itemListInfo;
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.deleteByUuid(uuid);
    }

}

