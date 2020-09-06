package com.minetec.backend.service;

import com.minetec.backend.dto.form.VehicleAttributeCreateUpdateForm;
import com.minetec.backend.dto.info.VehicleAttributeInfo;
import com.minetec.backend.entity.VehicleAttribute;
import com.minetec.backend.repository.VehicleAttributeRepository;
import com.minetec.backend.repository.projection.VehicleAttributeListItemProjection;
import com.minetec.backend.dto.mapper.VehicleAttributeMapper;
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
public class VehicleAttributeService extends EntityService<VehicleAttribute, VehicleAttributeRepository> {

    private final VehicleAttributeValueService vehicleAttributeValueService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<VehicleAttributeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * create
     *
     * @param vehicleAttributeCreateForm
     * @return
     */
    public VehicleAttributeInfo create(final VehicleAttributeCreateUpdateForm vehicleAttributeCreateForm) {
        if (vehicleAttributeCreateForm.getUuid() == null) {
            var entity = new VehicleAttribute();
            entity.setName(vehicleAttributeCreateForm.getVehicleAttribute().toUpperCase());
            this.persist(entity);
            return VehicleAttributeMapper.toInfo(entity);
        } else {
            return update(vehicleAttributeCreateForm.getUuid(), vehicleAttributeCreateForm);
        }
    }

    /**
     * update
     *
     * @param vehicleAttributeCreateForm
     * @return
     */
    public VehicleAttributeInfo update(final UUID uuid,
                                       final VehicleAttributeCreateUpdateForm vehicleAttributeCreateForm) {
        var entity = this.findByUuid(uuid);
        entity.setName(vehicleAttributeCreateForm.getVehicleAttribute());
        this.persist(entity);
        return VehicleAttributeMapper.toInfo(entity);
    }

    /**
     * @param uuid
     * @return
     */
    public VehicleAttributeInfo find(final UUID uuid) {
        return VehicleAttributeMapper.toInfo(this.findByUuid(uuid));
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.findByUuid(uuid).getVehicleAttributeValues().forEach(vehicleAttributeValue ->
            vehicleAttributeValueService.deleteById(vehicleAttributeValue.getId())
        );
        this.deleteByUuid(uuid);
    }


    /**
     * @param filter
     * @param pageable
     * @return
     */
    public Page<VehicleAttributeListItemProjection> findBy(final String filter, final Pageable pageable) {
        Page<VehicleAttribute> attributes = this.getRepository().findAll(this.getFilter(filter), pageable);
        final List<VehicleAttributeInfo> items =
            attributes.stream().map(VehicleAttributeMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, attributes.getTotalElements());
    }

    /**
     * @param filter
     * @return
     */
    public Specification<VehicleAttribute> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter))
            .toPredicate(root, query, cb);
    }
}
