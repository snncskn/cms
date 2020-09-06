package com.minetec.backend.service;

import com.minetec.backend.dto.form.PropertyCreateUpdateForm;
import com.minetec.backend.dto.info.PropertyInfo;
import com.minetec.backend.entity.Property;
import com.minetec.backend.repository.PropertyRepository;
import com.minetec.backend.repository.projection.PropertyListItemProjection;
import com.minetec.backend.dto.mapper.PropertyMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

import static org.springframework.data.jpa.domain.Specification.where;

@Service
public class PropertyService extends EntityService<Property, PropertyRepository> {

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<PropertyListItemProjection> list(@NotNull final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param form
     * @return
     */
    public PropertyInfo create(@NotNull final PropertyCreateUpdateForm form) {
        var property = new Property();
        property.setGroupName(form.getGroupName());
        property.setKeyLabel(form.getKeyLabel());
        property.setKeyValue(form.getKeyValue());
        this.persist(property);
        return PropertyMapper.toInfo(property);
    }


    /**
     * @param uuid
     * @param form
     * @return
     */
    public PropertyInfo update(@NotNull final UUID uuid, @NotNull final PropertyCreateUpdateForm form) {
        var property = this.findByUuid(uuid);
        property.setGroupName(form.getGroupName());
        property.setKeyLabel(form.getKeyLabel());
        property.setKeyValue(form.getKeyValue());
        this.persist(property);
        return PropertyMapper.toInfo(property);
    }

    /**
     * @param uuid
     * @return
     */
    public PropertyInfo find(@NotNull final UUID uuid) {
        return PropertyMapper.toInfo(this.findByUuid(uuid));
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
    public Page<PropertyListItemProjection> findBy(@NotNull final String value, @NotNull final Pageable pageable) {
        return this.getRepository().findAll(this.getFilter(value), pageable);
    }

    /**
     * @param filter
     * @return
     */
    public Specification<PropertyListItemProjection> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("groupName", filter)
                .or(contains("keyLabel", filter))
                .or(contains("keyValue", filter)))
            .toPredicate(root, query, cb);
    }

    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<PropertyListItemProjection> findByGroupName(@NotNull final String value,
                                                            @NotNull final Pageable pageable) {
        return this.getRepository().findByGroupName(value, pageable);
    }
}
