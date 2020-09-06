package com.minetec.backend.service;

import com.minetec.backend.dto.form.VehicleAttributeListCreateForm;
import com.minetec.backend.dto.form.VehicleAttributeListUpdateForm;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.VehicleAttributeListInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.SiteMapper;
import com.minetec.backend.dto.mapper.VehicleAttributeListMapper;
import com.minetec.backend.dto.mapper.VehicleMapper;
import com.minetec.backend.dto.mapper.VehicleTypeAttributeMapper;
import com.minetec.backend.dto.mapper.VehicleTypeMapper;
import com.minetec.backend.entity.Image;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.entity.VehicleAttributeList;
import com.minetec.backend.repository.VehicleAttributeListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VehicleAttributeListService
    extends EntityService<VehicleAttributeList, VehicleAttributeListRepository> {

    private final VehicleTypeService vehicleTypeService;

    private final VehicleTypeAttributeService vehicleTypeAttributeService;

    private final VehicleAttributeService vehicleAttributeService;

    private final VehicleAttributeValueService vehicleAttributeValueService;

    private final ImageService imageService;

    private final SiteService siteService;

    /**
     * create
     *
     * @param vehicleDetailCreateForm
     * @return
     */
    public VehicleAttributeListInfo create(final Vehicle vehicle,
                                           final VehicleAttributeListCreateForm vehicleDetailCreateForm) {

        this.deleteAll(this.getRepository().findByVehicleId(vehicle.getId()));

        var vehicleAttributeLists = new ArrayList<VehicleAttributeList>();

        vehicleDetailCreateForm.getVehicleAttributeListDetailCreateForm().forEach(detailCreateForm -> {
            var vehicleAttributeList = new VehicleAttributeList();
            vehicleAttributeList.setVehicle(vehicle);
            vehicleAttributeList.setVehicleAttribute(
                vehicleAttributeService.findByUuid(detailCreateForm.getVehicleAttributeUuid()));
            vehicleAttributeList.setVehicleAttributeValue(
                    vehicleAttributeValueService.findByUuid(detailCreateForm.getVehicleAttributeValueUuid()));
            var newVehicleAttributeList = this.persist(vehicleAttributeList);
            vehicleAttributeLists.add(newVehicleAttributeList);
        });

        var vehicleInfo = VehicleMapper.toInfo(vehicle);
        vehicleInfo.setVehicleType(VehicleTypeMapper.toInfo(vehicle.getVehicleType()));

        vehicleInfo.setImageInfos(getImageInfos(vehicle));

        var vehicleAttributeListInfo = VehicleAttributeListMapper.toInfo(vehicleAttributeLists);
        vehicleAttributeListInfo.setVehicleInfo(vehicleInfo);

        return vehicleAttributeListInfo;
    }

    /**
     * @param vehicle
     * @return
     */
    private ArrayList<ImageInfo> getImageInfos(final Vehicle vehicle) {
        List<Image> images = imageService.getRepository().findByVehicleId(vehicle.getId());
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
     * @param vehicleDetailUpdateForm
     * @return
     */
    public VehicleAttributeListInfo update(final UUID uuid,
                                           final VehicleAttributeListUpdateForm vehicleDetailUpdateForm) {
        return new VehicleAttributeListInfo();
    }

    /**
     * @param vehicle
     * @return
     */
    public VehicleAttributeListInfo find(final Vehicle vehicle) {

        var vehicleList = this.getRepository().findByVehicleId(vehicle.getId());

        var listAttrValues = new ArrayList<UUID>();
        vehicleList.forEach(vehicleAttributeList -> {
            listAttrValues.add(vehicleAttributeList.getVehicleAttributeValue().getUuid());
        });

        var vehicleListInfo = new VehicleAttributeListInfo();
        vehicleListInfo.setVehicleInfo(VehicleMapper.toInfo(vehicle));

        var vehicleType = vehicleTypeService.findByUuid(vehicle.getVehicleType().getUuid());
        vehicleListInfo.getVehicleInfo().setVehicleType(VehicleTypeMapper.toInfo(vehicleType));

        var vehicleTypeAttributes =
            vehicleTypeAttributeService.getRepository().findByVehicleTypeId(vehicleType.getId());

        vehicleListInfo.getVehicleInfo().getVehicleType().
            setVehicleTypeAttributes(VehicleTypeAttributeMapper.toInfos(vehicleTypeAttributes));

        var vehicleTypeAttributeInfos =
            vehicleListInfo.getVehicleInfo().getVehicleType().getVehicleTypeAttributes();

        vehicleTypeAttributeInfos.forEach(vehicleTypeAttributeInfo -> {
            vehicleList.forEach(itemAttributeList -> {
                if (vehicleTypeAttributeInfo.getUuid().equals(itemAttributeList.getVehicleAttribute().getUuid())) {
                    vehicleTypeAttributeInfo.setSelectedVehicleAttrUuid(
                        itemAttributeList.getVehicleAttributeValue().getUuid());
                }
            });

        });

        vehicleListInfo.getVehicleInfo().setImageInfos(getImageInfos(vehicle));
        vehicleListInfo.getVehicleInfo().setSiteInfo(
            SiteMapper.toInfo(siteService.findByUuid(vehicle.getSite().getUuid())));

        return vehicleListInfo;
    }

    /**
     * @param uuid
     */
    public void delete(final UUID uuid) {
        this.deleteByUuid(uuid);
    }

}

