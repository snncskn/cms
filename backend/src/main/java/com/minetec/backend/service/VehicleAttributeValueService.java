package com.minetec.backend.service;


import com.minetec.backend.dto.form.VehicleAttributeValueCreateUpdateForm;
import com.minetec.backend.dto.info.VehicleAttributeValueInfo;
import com.minetec.backend.entity.VehicleAttribute;
import com.minetec.backend.entity.VehicleAttributeValue;
import com.minetec.backend.repository.VehicleAttributeValueRepository;
import com.minetec.backend.repository.projection.VehicleAttributeValueListItemProjection;
import com.minetec.backend.dto.mapper.VehicleAttributeValueMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class VehicleAttributeValueService
    extends EntityService<VehicleAttributeValue, VehicleAttributeValueRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<VehicleAttributeValueListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * @param form
     * @return
     */
    public VehicleAttributeValueInfo create(@NotNull final VehicleAttribute vehicleAttribute,
                                            @NotNull final VehicleAttributeValueCreateUpdateForm form) {
        var entity = new VehicleAttributeValue();
        if ((form.getAttributeValueUuid() == null) && !(StringUtils.isWhitespace(form.getDesc()))) {
            entity.setValue(form.getDesc());
            entity.setVehicleAttribute(vehicleAttribute);
            return VehicleAttributeValueMapper.toInfo(this.persist(entity));
        } else {
            return this.update(form.getAttributeValueUuid(), form);
        }

    }

    public VehicleAttributeValueInfo update(@NotNull final UUID uuid,
                                            @NotNull final VehicleAttributeValueCreateUpdateForm createUpdateForm) {
        var entity = this.findByUuid(uuid);
        entity.setValue(createUpdateForm.getDesc());
        this.persist(entity);
        return VehicleAttributeValueMapper.toInfo(entity);
    }

    /**
     * @param uuid
     * @return
     */
    public VehicleAttributeValueInfo find(@NotNull final UUID uuid) {
        return VehicleAttributeValueMapper.toInfo(this.findByUuid(uuid));
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
    public Page<VehicleAttributeValueListItemProjection> findBy(final String value, final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }

    /**
     * @param filter
     * @return
     */
    public Specification<VehicleAttributeValueListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter))
            .toPredicate(root, query, cb);
    }

}
