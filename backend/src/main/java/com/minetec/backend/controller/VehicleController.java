package com.minetec.backend.controller;

import com.minetec.backend.dto.filter.VehicleFilterForm;
import com.minetec.backend.dto.filter.VehicleUsedItemFilterForm;
import com.minetec.backend.dto.filter.workshop.WorkshopFilterForm;
import com.minetec.backend.dto.form.VehicleAttributeListCreateForm;
import com.minetec.backend.dto.form.VehicleCreateUpdateForm;
import com.minetec.backend.dto.form.VehicleImageCreateForm;
import com.minetec.backend.service.VehicleService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.Utils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.UUID;

/**
 * @author Sinan
 */
@RestController
@RequestMapping("/api/vehicle")
@Secured(value = {"ROLE_ADMIN", "ROLE_ENGINEERING_PLANNER"})
public class VehicleController extends BaseController<VehicleService, VehicleCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final VehicleCreateUpdateForm form) {
        final var info = service.create(form);
        if (!Utils.isEmpty(info)) {
            return Response.ok(respFactory.success("52b7ed06f074", "vehicle-created", info));
        } else {
            return Response.ok(respFactory.error("12b3ed05f042", "vehicle-created-error", info));
        }
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("69b7ed06g094", "vehicle-deleted"));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("08b7ed06d064", "vehicle-fetch", service.find(uuid)));
    }

    @Override
    public ResponseEntity update(@PathVariable final UUID uuid,
                                 @RequestBody @Valid final VehicleCreateUpdateForm form) {
        return Response.ok(respFactory.success("24b7ed06s054", "vehicle-updated",
            service.update(uuid, form)));
    }

    @Override
    public ResponseEntity list(
            @PageableDefault(sort = "fleetNo", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("43b7ed06a044", "vehicle-listed",
            service.findByCriteria(null, pageable)));
    }

    @PostMapping("/createVehicleList")
    public ResponseEntity createVehicleList(@RequestBody @Valid final VehicleAttributeListCreateForm form) {
        return Response.ok(respFactory.success("78b7ed06l094", "vehicle-create-list",
            service.createVehicleAttributes(form)));
    }

    @PostMapping("/createImages")
    public ResponseEntity createImages(@RequestBody @Valid final VehicleImageCreateForm form) {
        return Response.ok(respFactory.success("10b5ef06l044", "vehicle-create-images",
            service.createImages(form)));
    }

    @GetMapping("/findVehicleList/{uuid}")
    public ResponseEntity findVehicleList(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("22b7ed06c014", "vehicle-find-list",
            service.findVehicleDetail(uuid)));
    }

    @Override
    public ResponseEntity filter(final String filter, final Pageable pageable) {
        return null;
    }

    @PostMapping("/find-all-breakdown")
    public ResponseEntity findAllBreakdown(@RequestBody @Valid final WorkshopFilterForm form) {
        return Response.ok(respFactory.success("18b4ed06d054", "find-all-breakdown",
            service.findAllBreakDownByVehicleUuid(form)));
    }

    @PostMapping("/find-all-used")
    public ResponseEntity findAllUsed(@RequestBody @Valid final WorkshopFilterForm form) {
        return Response.ok(respFactory.success("18b4ed06d054", "find-all-used",
            service.findAllBreakDownByVehicleUuid(form)));
    }

    @PostMapping("/filter")
    public ResponseEntity filter(@RequestBody @Valid final VehicleFilterForm filterForm, final Pageable pageable) {
        final var list = service.findByCriteria(filterForm, pageable);
        return Response.ok(respFactory.success("72b7ev67v029", "vehicle-filtered", list));
    }

    /**
     * when xls methods call, fe should be different browser page
     *
     * @param response
     * @param filterForm
     * @throws Exception
     */
    @PostMapping(value = "/xlsExcel")
    public void xlsExcel(final HttpServletResponse response,
                         @RequestBody @Valid final VehicleFilterForm filterForm,
                         final Pageable pageable) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGenerator(response.getOutputStream(), filterForm);
    }

    @PostMapping("/find-all-used-item")
    public ResponseEntity findAllUsedItem(@RequestBody @Valid final VehicleUsedItemFilterForm form) {
        return Response.ok(respFactory.success("95b4ed06d020", "find-all-used-item",
            service.findUsedItemByVehicleUuid(form)));
    }

}
