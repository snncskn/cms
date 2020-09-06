package com.minetec.backend.service;

import com.minetec.backend.dto.filter.ItemFilterForm;
import com.minetec.backend.dto.filter.ItemOrderFilterForm;
import com.minetec.backend.dto.form.ItemAttributeListCreateForm;
import com.minetec.backend.dto.form.ItemCreateUpdateForm;
import com.minetec.backend.dto.form.ItemImageCreateForm;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.ItemAttributeListInfo;
import com.minetec.backend.dto.info.ItemInfo;
import com.minetec.backend.dto.info.ItemOrderInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.ItemMapper;
import com.minetec.backend.dto.mapper.ItemTypeMapper;
import com.minetec.backend.entity.Item;
import com.minetec.backend.repository.ItemRepository;
import com.minetec.backend.repository.projection.ItemListItemProjection;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.ItemListExcel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.minetec.backend.entity.AbstractEntity.CURRENCY_SCALE;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class ItemService extends EntityService<Item, ItemRepository> {

    private static final int BARCODE_LENGTH = 10;
    private final StockService stockService;
    private final SiteService siteService;
    private final ImageService imageService;
    private final JdbcTemplate jdbcTemplate;
    private final ItemTypeService itemTypeService;
    private final ItemAttributeListService itemAttributeListService;
    private final OrderItemService orderItemService;

    public Page<ItemListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    public ItemInfo create(final ItemCreateUpdateForm form) {

        var entity = new Item();
        if (form.getUuid() != null) {
            entity = this.findByUuid(form.getUuid());
        }

        if (Utils.isEmpty(form.getBarcode())) {
            entity.setBarcode(newBarcode());
        } else {
            entity.setBarcode(form.getBarcode());
        }

        entity.setItemType(itemTypeService.findByUuid(form.getPartTypeUuid()));
        entity.setStorePartNumber(form.getStorePartNumber());
        entity.setItemDescription(form.getItemDescription());
        entity.setMinStockQuantity(form.getMinStockQuantity());
        entity.setUnit(form.getUnit());
        this.persist(entity);
        return ItemMapper.toInfo(entity);
    }

    public ItemInfo update(final UUID uuid, final ItemCreateUpdateForm form) {
        return new ItemInfo();
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }

    /**
     * @param uuid
     * @return
     */
    public ItemInfo find(final UUID uuid) {

        final var item = this.findByUuid(uuid);
        final var itemInfo = ItemMapper.toInfo(item);
        final var imageList = new ArrayList<ImageInfo>();

        itemInfo.setItemTypeInfo(ItemTypeMapper.toInfo(itemTypeService.findByUuid(item.getItemType().getUuid())));

        item.getImages().forEach(image -> {
            var imageInfo = ImageMapper.toInfo(image);
            imageInfo.setDownloadUrl(imageService.imageUrl(image.getUuid()));
            imageList.add(imageInfo);
        });

        itemInfo.setImageInfos(imageList);
        return itemInfo;
    }

    /**
     * @param form
     * @return
     */
    public ItemAttributeListInfo create(final ItemAttributeListCreateForm form) {
        var item = this.findByUuid(form.getVehicleUuid());
        return itemAttributeListService.create(item, form);
    }


    /**
     * @param itemUuid
     * @return
     */
    public ItemAttributeListInfo findItemDetail(final UUID itemUuid) {
        return itemAttributeListService.find(this.findByUuid(itemUuid));
    }

    /**
     * @return
     */
    public String newBarcode() {
        var newSeq = jdbcTemplate.queryForObject("SELECT nextval('BARCODE_NUMBER_SEQ')", String.class);
        return StringUtils.leftPad(newSeq, BARCODE_LENGTH, "0");
    }

    /**
     * @param filterForm
     * @param pageable
     * @return
     */
    public Page<ItemInfo> findBy(@NotNull final ItemFilterForm filterForm, final Pageable pageable) {
        Page<Item> items;

        if (Utils.isEmpty(filterForm.getFilter())) {
            items = this.getRepository().filter(filterForm.getStorePartNumber(), filterForm.getItemDescription(),
                filterForm.getBarcode(), pageable);
        } else {
            final var repValue = Utils.replace(filterForm.getFilter(), "*", "");
            items = this.getRepository().allFilter(repValue, pageable);
        }

        final List<ItemInfo> itemInfos = items.stream().map(ItemMapper::toMap).collect(Collectors.toList());

        final var site = siteService.findByUuid(getContextDetail().getSiteUuid());
        itemInfos.forEach(itemInfo -> {
            itemInfo.setCurrentQuantity(stockService.findCurrentQuantity(site.getId(), itemInfo.getItemId()));
            itemInfo.setPrice(orderItemService.findMaxTotalByItemId(site.getId(), itemInfo.getItemId()));
            if (Utils.isEmpty(itemInfo.getCurrentQuantity())) {
                itemInfo.setCurrentQuantity(BigDecimal.ZERO);
            }
            if (Utils.isEmpty(itemInfo.getMinStockQuantity())) {
                itemInfo.setMinStockQuantity(BigDecimal.ZERO);
            }
            if (itemInfo.getMinStockQuantity().setScale(CURRENCY_SCALE)
                .compareTo(itemInfo.getCurrentQuantity().setScale(CURRENCY_SCALE)) > 0) {
                itemInfo.setStockLevelWarning(true);
            }

        });
        return new PageImpl<>(itemInfos, pageable, items.getTotalElements());
    }


    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<ItemInfo> findBy(@NotNull final String value, final Pageable pageable) {
        final var repValue = Utils.replace(value, "*", "");
        Page<Item> items = this.getRepository().findAll(this.getFilter(repValue), pageable);
        final List<ItemInfo> itemInfos = items.stream().map(ItemMapper::toMap).collect(Collectors.toList());
        return new PageImpl(itemInfos, pageable, items.getTotalElements());
    }

    /**
     * @param filter
     * @return
     */
    private Specification<Item> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("storePartNumber", filter)
                .or(contains("itemDescription", filter))
                .or(contains("unit", filter))
                .or(contains("barcode", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param itemImageCreateForm
     * @return
     */
    public boolean createImages(final ItemImageCreateForm itemImageCreateForm) {
        Item item = this.findByUuid(itemImageCreateForm.getItemUuid());
        imageService.updateTo(item, itemImageCreateForm.getImageInfos());
        return true;
    }

    /**
     * @param itemOrderFilterForm
     * @return
     */
    public Page<ItemOrderInfo> findAllItemOrder(final ItemOrderFilterForm itemOrderFilterForm) {
        return orderItemService.findAllItemOrder(itemOrderFilterForm);
    }


    /**
     * @param response
     * @param filterForm
     */
    public void xlsGenerator(final ServletOutputStream response,
                             @NotNull @Valid final ItemFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        var infos = this.findBy(filterForm, pageable);
        var itemListExcel = new ItemListExcel();
        itemListExcel.generator(response, infos);
    }

    /**
     * @param form
     * @return
     */
    public Page<ItemInfo> filter(@NotNull final ItemFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize());

        return this.findBy(form, pageable);


    }
}
