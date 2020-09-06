package com.minetec.backend.service;

import com.minetec.backend.dto.form.VehicleTypeCreateUpdateForm;
import com.minetec.backend.dto.info.VehicleTypeInfo;
import com.minetec.backend.dto.mapper.VehicleTypeAttributeMapper;
import com.minetec.backend.dto.mapper.VehicleTypeMapper;
import com.minetec.backend.entity.VehicleType;
import com.minetec.backend.entity.VehicleTypeAttribute;
import com.minetec.backend.repository.VehicleTypeRepository;
import com.minetec.backend.repository.projection.VehicleTypeListItemProjection;
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

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class VehicleTypeService extends EntityService<VehicleType, VehicleTypeRepository> {

    private final VehicleTypeAttributeService vehicleTypeAttributeService;

    /**
     * @param vehicleTypeCreateUpdateForm
     * @return
     */
    public VehicleTypeInfo create(final VehicleTypeCreateUpdateForm vehicleTypeCreateUpdateForm) {

        var vehicleType = new VehicleType();

        if (vehicleTypeCreateUpdateForm.getUuid() != null) {
            vehicleType = this.findByUuid(vehicleTypeCreateUpdateForm.getUuid());
            var attributesForDelete =
                vehicleTypeAttributeService.getRepository().findByVehicleTypeId(vehicleType.getId());
            attributesForDelete.forEach(itemTypeAttribute ->
                vehicleTypeAttributeService.delete(itemTypeAttribute)
            );
        }

        vehicleType.setName(vehicleTypeCreateUpdateForm.getVehicleTypeDesc());

        var newVehicleType = this.persist(vehicleType);

        typeAttrCreate(vehicleTypeCreateUpdateForm, newVehicleType);

        return VehicleTypeMapper.toInfo(newVehicleType);
    }


    /**
     * @param vehicleTypeCreateUpdateForm
     * @param vehicleType
     */
    private void typeAttrCreate(final VehicleTypeCreateUpdateForm vehicleTypeCreateUpdateForm,
                                final VehicleType vehicleType) {
        var vehicleTypeAttributeCreateUpdateForms =
            vehicleTypeCreateUpdateForm.getVehicleTypeAttributeCreateUpdateForms();
        vehicleTypeAttributeCreateUpdateForms.forEach(itemTypeAttributeCreateUpdateForm -> {
            itemTypeAttributeCreateUpdateForm.setVehicleTypeUuid(vehicleType.getUuid());
            itemTypeAttributeCreateUpdateForm.setVehicleAttributeUuid(itemTypeAttributeCreateUpdateForm.getUuid());
            vehicleTypeAttributeService.create(vehicleType, itemTypeAttributeCreateUpdateForm);
        });
        vehicleType.setVehicleTypeAttributes(
            vehicleTypeAttributeService.getRepository().findByVehicleTypeId(vehicleType.getId()));
        this.persist(vehicleType);
    }


    /**
     * @param uuid
     */
    public void softDelete(final UUID uuid) {
        var vehicleType = this.findByUuid(uuid);
        vehicleType.setActive(false);
        this.persist(vehicleType);
    }

    /**
     * @param uuid
     * @return
     */
    public VehicleTypeInfo find(final UUID uuid) {
        var vehicle = this.findByUuid(uuid);
        var vehicleTypeInfo = VehicleTypeMapper.toInfo(vehicle);
        List<VehicleTypeAttribute> vehicleTypeAttributeList =
            vehicleTypeAttributeService.getRepository().findByVehicleTypeId(vehicle.getId());
        vehicleTypeInfo.setVehicleTypeAttributes(VehicleTypeAttributeMapper.toInfos(vehicleTypeAttributeList));
        return vehicleTypeInfo;
    }

    /**
     * pageable list
     *
     * @param pageable
     * @return<
     */
    public Page<VehicleTypeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }


    /**
     * @param value
     * @param pageable
     * @return
     */
    public Page<VehicleTypeInfo> findBy(final String value, final Pageable pageable) {
        final var repValue = Utils.replace(value, "*", "");
        Page<VehicleType> types = this.getRepository().findAll(this.getFilter(repValue), pageable);
        final List<VehicleTypeInfo> items = types.stream().map(VehicleTypeMapper::toInfo).collect(Collectors.toList());
        return new PageImpl(items, pageable, types.getTotalElements());
    }


    /**
     * @param filter
     * @return
     */
    public Specification<VehicleType> getFilter(@Valid @NotNull final String filter) {
        return (root, query, cb) -> where(
            contains("name", filter))
            .and(equals("isActive", true))
            .toPredicate(root, query, cb);
    }
}
