package com.minetec.backend.service;


import com.minetec.backend.dto.form.VehicleTypeAttributeCreateUpdateForm;
import com.minetec.backend.dto.info.VehicleTypeAttributeInfo;
import com.minetec.backend.entity.VehicleType;
import com.minetec.backend.entity.VehicleTypeAttribute;
import com.minetec.backend.repository.VehicleTypeAttributeRepository;
import com.minetec.backend.repository.projection.VehicleTypeAttributeListItemProjection;
import com.minetec.backend.dto.mapper.VehicleTypeAttributeMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleTypeAttributeService
    extends EntityService<VehicleTypeAttribute, VehicleTypeAttributeRepository> {

    private final VehicleAttributeService vehicleAttributeService;

    /**
     * pageable list
     *
     * @param pageable
     * @return
     */
    public Page<VehicleTypeAttributeListItemProjection> list(final Pageable pageable) {
        return getRepository().list(pageable);
    }

    /**
     * save
     *
     * @param vehicleTypeAttributeCreateForm
     * @return
     */
    public VehicleTypeAttributeInfo create(final VehicleType vehicleType,
                                           final VehicleTypeAttributeCreateUpdateForm vehicleTypeAttributeCreateForm) {
        var entity = new VehicleTypeAttribute();
        entity.setVehicleType(vehicleType);
        entity.setVehicleAttribute(
            vehicleAttributeService.findByUuid(vehicleTypeAttributeCreateForm.getVehicleAttributeUuid()));
        this.persist(entity);
        return VehicleTypeAttributeMapper.toInfo(entity);
    }


    /**
     * @param uuid
     * @return
     */
    public VehicleTypeAttributeInfo find(final UUID uuid) {
        return VehicleTypeAttributeMapper.toInfo(this.findByUuid(uuid));
    }

}
