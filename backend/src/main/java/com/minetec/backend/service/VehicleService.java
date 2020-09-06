package com.minetec.backend.service;

import com.minetec.backend.dto.filter.VehicleFilterForm;
import com.minetec.backend.dto.filter.VehicleUsedItemFilterForm;
import com.minetec.backend.dto.filter.workshop.WorkshopFilterForm;
import com.minetec.backend.dto.form.VehicleAttributeListCreateForm;
import com.minetec.backend.dto.form.VehicleCreateUpdateForm;
import com.minetec.backend.dto.form.VehicleImageCreateForm;
import com.minetec.backend.dto.info.ImageInfo;
import com.minetec.backend.dto.info.VehicleAttributeListInfo;
import com.minetec.backend.dto.info.VehicleInfo;
import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import com.minetec.backend.dto.info.workshop.VehicleUsedItemInfo;
import com.minetec.backend.dto.mapper.ImageMapper;
import com.minetec.backend.dto.mapper.SiteMapper;
import com.minetec.backend.dto.mapper.VehicleMapper;
import com.minetec.backend.dto.mapper.VehicleTypeMapper;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.repository.VehicleRepository;
import com.minetec.backend.service.workshop.JobCardService;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.VehicleListExcel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.minetec.backend.util.Utils.toInteger;
import static com.minetec.backend.util.Utils.toLong;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class VehicleService extends EntityService<Vehicle, VehicleRepository> {

    private static final int KM_CONST = 1000;
    private static final int HOUR_CONST = 50;
    private final SiteService siteService;
    private final ImageService imageService;
    private final JobCardService jobCardService;
    private final VehicleTypeService vehicleTypeService;
    private final StockHistoryService stockHistoryService;
    private final VehicleAttributeListService vehicleAttributeListService;


    /**
     * @param pageable
     * @return
     */
    public Page<VehicleInfo> list(final Pageable pageable) {
        final var site = siteService.findByUuid(getContextDetail().getSiteUuid());
        Page<Vehicle> vehicles = getRepository().list(site.getId(), pageable);
        final List<VehicleInfo> items = vehicles.stream().map(VehicleMapper::toMap).collect(Collectors.toList());

        items.forEach(vehicleInfo -> {
            Long cnt = jobCardService.getRepository().findByJobCardStatusAndActiveAndVehicleUuid(vehicleInfo.getUuid());
            vehicleInfo.setHasTask(cnt.intValue() > 0);
        });

        return new PageImpl<>(items, pageable, vehicles.getTotalElements());
    }

    /**
     * @param vehicleCreateForm
     * @return
     */
    public VehicleInfo create(final VehicleCreateUpdateForm vehicleCreateForm) {

        var vehicle = new Vehicle();

        if (vehicleCreateForm.getUuid() != null) {

            vehicle = this.findByUuid(vehicleCreateForm.getUuid());

            if (toInteger(vehicleCreateForm.getCurrentMachineHours()) < toInteger(vehicle.getCurrentMachineHours())) {
                return null;
            }

        }

        vehicle.setSite(siteService.findByUuid(vehicleCreateForm.getSiteUuid()));
        vehicle.setVehicleType(vehicleTypeService.findByUuid(vehicleCreateForm.getVehicleTypeUuid()));
        vehicle.setFleetNo(vehicleCreateForm.getFleetNo());
        vehicle.setRegistrationNo(vehicleCreateForm.getRegistrationNo());
        vehicle.setUnit(vehicleCreateForm.getUnit());
        vehicle.setSerialNo(vehicleCreateForm.getSerialNo());
        vehicle.setVinNo(vehicleCreateForm.getVinNo());
        vehicle.setUsable(vehicleCreateForm.isUsable());

        vehicle.setCurrentMachineHours(vehicleCreateForm.getCurrentMachineHours());
        vehicle.setServiceIntervalHours(vehicleCreateForm.getServiceIntervalHours());

        this.calcRemainingHoursKm(vehicle);

        var newVehicle = this.persist(vehicle);
        return VehicleMapper.toInfo(newVehicle);

    }

    /**
     * @param vehicle
     */
    private void calcRemainingHoursKm(final Vehicle vehicle) {

        if (vehicle.isNew()) {
            vehicle.setRemainingHours(vehicle.getServiceIntervalHours());
            vehicle.setLastServiceDate(LocalDateTime.now());
            vehicle.setLastServiceHours(vehicle.getCurrentMachineHours());
            return;
        }

        if (toInteger(vehicle.getLastServiceHours()) == 0) {

            if (toLong(vehicle.getServiceIntervalHours()) > toLong(vehicle.getCurrentMachineHours())) {
                long difference = toLong(vehicle.getServiceIntervalHours()) - toLong(vehicle.getCurrentMachineHours());
                if (difference != 0) {
                    vehicle.setRemainingHours(String.valueOf(difference));
                }
            } else {
                long difference = toLong(vehicle.getCurrentMachineHours()) - toLong(vehicle.getServiceIntervalHours());
                if (difference != 0) {
                    vehicle.setRemainingHours("-" + difference);
                }
            }

        } else {

            long difference = toLong(vehicle.getCurrentMachineHours()) - toLong(vehicle.getLastServiceHours());
            long remainingHours = toLong(vehicle.getServiceIntervalHours()) - difference;
            vehicle.setRemainingHours(String.valueOf(remainingHours));

        }
    }

    /**
     * @param vehicleImageCreateForm
     * @return
     */
    public boolean createImages(final VehicleImageCreateForm vehicleImageCreateForm) {
        Vehicle vehicle = this.findByUuid(vehicleImageCreateForm.getVehicleUuid());
        imageService.updateTo(vehicle, vehicleImageCreateForm.getImageInfos());
        return true;
    }

    /**
     * @param uuid
     * @param vehicleUpdateForm
     * @return
     */
    public VehicleInfo update(final UUID uuid, final VehicleCreateUpdateForm vehicleUpdateForm) {
        return new VehicleInfo();
    }

    /**
     * @param uuid
     * @return
     */
    public VehicleInfo find(final UUID uuid) {

        final var vehicle = this.findByUuid(uuid);
        final var vehicleInfo = VehicleMapper.toInfo(vehicle);
        final var imageList = new ArrayList<ImageInfo>();

        vehicleInfo.setSiteInfo(SiteMapper.toInfo(siteService.findByUuid(vehicle.getSite().getUuid())));
        vehicleInfo.setVehicleType(
            VehicleTypeMapper.toInfo(vehicleTypeService.findByUuid(vehicle.getVehicleType().getUuid())));

        vehicle.getImages().forEach(image -> {
            var imageInfo = ImageMapper.toInfo(image);
            imageInfo.setDownloadUrl(imageService.imageUrl(image.getUuid()));
            imageList.add(imageInfo);
        });

        vehicleInfo.setImageInfos(imageList);

        return vehicleInfo;
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
     * @param form
     * @return
     */
    public VehicleAttributeListInfo createVehicleAttributes(final VehicleAttributeListCreateForm form) {
        var vehicle = this.findByUuid(form.getVehicleUuid());
        return vehicleAttributeListService.create(vehicle, form);
    }


    /**
     * @param vehicleUuid
     * @return
     */
    public VehicleAttributeListInfo findVehicleDetail(final UUID vehicleUuid) {
        return vehicleAttributeListService.find(this.findByUuid(vehicleUuid));
    }




    /**
     * @param form
     * @return
     */
    public Page<BreakDownListInfo> findAllBreakDownByVehicleUuid(@NotNull @Valid final WorkshopFilterForm form) {
        return jobCardService.findAllBreakDownByVehicleUuid(form);
    }

    /**
     * @param form
     * @return
     */
    public Page<BreakDownListInfo> findAllUsedByVehicleUuid(@NotNull @Valid final WorkshopFilterForm form) {
        return jobCardService.findAllBreakDownByVehicleUuid(form);
    }

    /**
     *
     * @throws IOException
     */
    public void xlsGenerator(@NotNull final ServletOutputStream servletOutputStream,
                             @NotNull final VehicleFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        Page<VehicleInfo> vehicleInfos = this.findByCriteria(filterForm, pageable);
        var vehicleListExcel = new VehicleListExcel();
        vehicleListExcel.generator(servletOutputStream, vehicleInfos);
    }

    /**
     * @param filterForm
     * @param pageable
     * @return
     */
    public Page<VehicleInfo> findByCriteria(final VehicleFilterForm filterForm, final Pageable pageable) {

        Page<Vehicle> vehicles;

        final var site = siteService.findByUuid(getContextDetail().getSiteUuid());

        if (Utils.isEmpty(filterForm)) {
            vehicles = getRepository().list(site.getId(), pageable);
        } else {
            vehicles = this.getRepository().filter(filterForm.isUsable(), site.getId(),
                filterForm.getFleetNo(), filterForm.getRegistrationNo(), filterForm.getSerialNo(),
                filterForm.getVinNo(), filterForm.getCurrentMachineHours(), filterForm.getServiceIntervalHours(),
                filterForm.getLastServiceHours(), filterForm.getVehicleTypeFilterForm().getName(), pageable);
        }

        List<VehicleInfo> items = vehicles.stream().map(VehicleMapper::toMap).collect(Collectors.toList());

        items.forEach(vehicleInfo -> {
            Long cnt = jobCardService.getRepository().findByJobCardStatusAndActiveAndVehicleUuid(vehicleInfo.getUuid());
            vehicleInfo.setHasTask(cnt.intValue() > 0);

            if ("KM".equals(vehicleInfo.getUnit())) {
                var ret = vehicleInfo.getRemainingHours() <= KM_CONST ? "WARNING" : "";
                vehicleInfo.setServiceWarning(ret);
            } else if ("HOUR".equals(vehicleInfo.getUnit())) {
                var ret = vehicleInfo.getRemainingHours() <= HOUR_CONST ? "WARNING" : "";
                vehicleInfo.setServiceWarning(ret);
            }
            if (vehicleInfo.getRemainingHours() < 0) {
                vehicleInfo.setServiceWarning("ERROR");
            }

        });

        Collections.sort(items);
        return new PageImpl<>(items, pageable, vehicles.getTotalElements());
    }

    /**
     * @param vehicleUuid
     * @throws IOException
     */
    public void updateLastServiceDate(@NotNull final UUID vehicleUuid) {
        var vehicle = this.findByUuid(vehicleUuid);
        vehicle.setLastServiceDate(LocalDateTime.now());
        vehicle.setRemainingHours(vehicle.getServiceIntervalHours());
        vehicle.setLastServiceHours(vehicle.getCurrentMachineHours());
        this.persist(vehicle);
    }

    /**
     * @param form
     * @return
     */
    public Page<VehicleUsedItemInfo> findUsedItemByVehicleUuid(@NotNull @Valid final VehicleUsedItemFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize());
        final var vehicle = this.findByUuid(form.getVehicleUuid());
        Page<StockHistory> stockHistories = stockHistoryService.getRepository().
            findByVehicleId(vehicle.getId(), Utils.toLocalDate(form.getStartDate()),
                Utils.toLocalDate(form.getEndDate()), pageable);
        final List<VehicleUsedItemInfo> infos = stockHistories.stream().
            map(VehicleMapper::toMapUsedItem).collect(Collectors.toList());
        return new PageImpl<>(infos, pageable, stockHistories.getTotalElements());
    }


}
