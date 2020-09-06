package com.minetec.backend.service;

import com.minetec.backend.dto.filter.SupplierFilterForm;
import com.minetec.backend.dto.filter.VehicleFilterForm;
import com.minetec.backend.dto.form.ContactCreateUpdateForm;
import com.minetec.backend.dto.form.SupplierCreateUpdateForm;
import com.minetec.backend.dto.form.SupplierImageCreateForm;
import com.minetec.backend.dto.info.ContactInfo;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.SupplierInfo;
import com.minetec.backend.dto.info.VehicleInfo;
import com.minetec.backend.dto.mapper.ContactMapper;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.SupplierMapper;
import com.minetec.backend.entity.Supplier;
import com.minetec.backend.repository.SupplierRepository;
import com.minetec.backend.repository.projection.SupplierListItemProjection;
import com.minetec.backend.util.excel_generator.SupplierListExcel;
import com.minetec.backend.util.excel_generator.VehicleListExcel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.minetec.backend.dto.mapper.SupplierMapper.toInfo;
import static org.springframework.data.jpa.domain.Specification.where;

@Service
@RequiredArgsConstructor
public class SupplierService extends EntityService<Supplier, SupplierRepository> {

    private final ImageService imageService;
    private final ContactService contactService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<SupplierListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param form
     * @return
     */
    public SupplierInfo create(final SupplierCreateUpdateForm form) {

        var supplier = new Supplier();
        if (form.getUuid() == null) {
            supplier.setName(form.getName());
            supplier.setAddress(form.getAddress());
            supplier.setRegisterNumber(form.getRegisterNumber());
            supplier.setTaxNumber(form.getTaxNumber());
            this.persist(supplier);
        }
        return toInfo(supplier);
    }


    /**
     * @param uuid
     * @param form
     * @return
     */
    public SupplierInfo update(final UUID uuid, final SupplierCreateUpdateForm form) {
        var supplier = this.findByUuid(uuid);
        supplier.setName(form.getName());
        supplier.setAddress(form.getAddress());
        supplier.setRegisterNumber(form.getRegisterNumber());
        supplier.setTaxNumber(form.getTaxNumber());
        this.persist(supplier);
        return toInfo(supplier);
    }

    /**
     * @param uuid
     * @return
     */
    public SupplierInfo find(final UUID uuid) {

        final var supplier = this.findByUuid(uuid);
        final var imageList = new ArrayList<ImageInfo>();

        supplier.getImages().forEach(image -> {
            var imageInfo = ImageMapper.toInfo(image);
            imageInfo.setDownloadUrl(imageService.imageUrl(image.getUuid()));
            imageList.add(imageInfo);
        });

        final var supplierInfo = SupplierMapper.toInfo(supplier);
        supplierInfo.setImageInfos(imageList);
        return supplierInfo;
    }

    /**
     * @param uuid
     */
    public void softDelete(final UUID uuid) {
        var entity = this.findByUuid(uuid);
        entity.setActive(false);
        this.persist(entity);
    }

    /**
     * @param contactCreateUpdateForm
     * @return
     */
    public ContactInfo create(final ContactCreateUpdateForm contactCreateUpdateForm) {
        var supplier = this.findByUuid(contactCreateUpdateForm.getSupplierUuid());
        return contactService.create(supplier, contactCreateUpdateForm);
    }

    public ContactInfo update(final UUID uuid, final ContactCreateUpdateForm contactCreateUpdateForm) {
        return contactService.update(uuid, contactCreateUpdateForm);
    }

    /**
     * @param orderItemUuid
     */
    public void softDeleteContact(@NotNull final UUID orderItemUuid) {
        contactService.softDelete(orderItemUuid);
    }

    public ContactInfo findSupplierContact(final UUID uuid) {
        return ContactMapper.toInfo(contactService.findByUuid(uuid));
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<SupplierListItemProjection> findBy(final String value, final Pageable pageable) {
        Page<Supplier> suppliers = this.getRepository().findAll(this.getFilter(value), pageable);
        final List<SupplierInfo> items = suppliers.stream().map(SupplierMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, suppliers.getTotalElements());
    }

    /**
     * @param filter
     * @return
     */
    private Specification<Supplier> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter)
                .or(contains("address", filter))
                .or(contains("taxNumber", filter))
                .or(contains("registerNumber", filter)))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }

    /**
     * @param supplierImageCreateForm
     * @return
     */
    public boolean createImages(final SupplierImageCreateForm supplierImageCreateForm) {
        var supplier = this.findByUuid(supplierImageCreateForm.getSupplierUuid());
        imageService.updateTo(supplier, supplierImageCreateForm.getImageInfos());
        return true;
    }

    /**g
     *
     * @throws IOException
     */
    public void xlsGenerator(@NotNull final ServletOutputStream servletOutputStream,
                             @NotNull final SupplierFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        Page<SupplierListItemProjection> supplierInfos = this.findBy(filterForm.getValue(), pageable);
        var supplierListExcel = new SupplierListExcel();
        supplierListExcel.generator(servletOutputStream, supplierInfos);
    }
}
