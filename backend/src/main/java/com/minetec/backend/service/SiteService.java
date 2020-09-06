package com.minetec.backend.service;

import com.minetec.backend.dto.form.SiteCreateUpdateForm;
import com.minetec.backend.dto.info.SiteInfo;
import com.minetec.backend.dto.mapper.SiteMapper;
import com.minetec.backend.dto.mapper.SupplierMapper;
import com.minetec.backend.entity.Site;
import com.minetec.backend.repository.SiteRepository;
import com.minetec.backend.repository.projection.SiteListItemProjection;
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
public class SiteService extends EntityService<Site, SiteRepository> {

    private final SupplierService supplierService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<SiteListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param form
     * @return
     */
    public SiteInfo create(@NotNull final SiteCreateUpdateForm form) {

        var site = new Site();
        if (!Utils.isEmpty(form.getUuid())) {
            site = this.findByUuid(form.getUuid());
        }
        site.setDescription(form.getDescription());
        if (!Utils.isEmpty(form.getSupplierUuid())) {
            final var supp = supplierService.findByUuid(form.getSupplierUuid());
            site.setSupplierId(supp.getId());
        }
        var newSite = this.persist(site);
        var siteInfo = SiteMapper.toInfo(newSite);

        if (!Utils.isEmpty(form.getSupplierUuid())) {
            final var supplier = supplierService.findById(newSite.getSupplierId());
            siteInfo.setSupplierInfo(SupplierMapper.toInfo(supplier));
        }
        return siteInfo;
    }


    /**
     * @param uuid
     * @param form
     * @return
     */
    public SiteInfo update(final UUID uuid, final SiteCreateUpdateForm form) {
        return new SiteInfo();
    }

    /**
     * @param uuid
     * @return
     */
    public SiteInfo find(@NotNull final UUID uuid) {
        final var site = this.findByUuid(uuid);
        final var siteInfo = SiteMapper.toInfo(site);
        if (!Utils.isEmpty(site.getSupplierId())) {
            final var supplier = supplierService.findById(site.getSupplierId());
            siteInfo.setSupplierInfo(SupplierMapper.toInfo(supplier));
        }
        return siteInfo;
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.deleteByUuid(uuid);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<SiteListItemProjection> findBy(final String value, final Pageable pageable) {
        Page<Site> sites = this.getRepository().findAll(this.getFilter(value), pageable);
        final List<SiteInfo> items = sites.stream().map(SiteMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, sites.getTotalElements());
    }


    /**
     * @param filter
     * @return
     */
    public Specification<Site> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("description", filter))
            .toPredicate(root, query, cb);
    }
}
