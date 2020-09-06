package com.minetec.backend.controller;

import com.minetec.backend.dto.filter.SupplierFilterForm;
import com.minetec.backend.dto.filter.VehicleFilterForm;
import com.minetec.backend.dto.form.ContactCreateUpdateForm;
import com.minetec.backend.dto.form.SupplierCreateUpdateForm;
import com.minetec.backend.dto.form.SupplierImageCreateForm;
import com.minetec.backend.service.SupplierService;
import com.minetec.backend.util.Response;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

/**
 * @author Sandhya Mengani
 */
@CrossOrigin
@RestController
@RequestMapping("/api/suppliers")
@Secured(value = {"ROLE_ADMIN", "ROLE_PROCUREMENT_MANAGER", "ROLE_SITE_BUYER", "ROLE_SITE_MANAGER"})
public class SupplierController extends BaseController<SupplierService, SupplierCreateUpdateForm> {

    @Override
    public ResponseEntity create(@RequestBody @Valid final SupplierCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "supplier-save",
            service.create(form)));
    }

    @Override
    public ResponseEntity update(@RequestParam final UUID uuid,
                         @RequestBody @Valid final SupplierCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "supplier-save",
            service.update(uuid, form)));
    }

    @Override
    public ResponseEntity fetch(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("58b7ed06d024", "supplier-list",
            service.find(uuid)));
    }

    @Override
    public ResponseEntity delete(@RequestParam final UUID uuid) {
        service.softDelete(uuid);
        return Response.ok(respFactory.success("58b7ed06d024", "supplier-delete"));
    }

    @Override
    public ResponseEntity list(@PageableDefault(sort = "id", direction = Sort.Direction.ASC) final Pageable pageable) {
        return Response.ok(respFactory.success("58b7ed06d024", "supplier-list",
            service.list(pageable)));
    }

    @PostMapping(value = {"/create-contact"})
    public ResponseEntity createSupplierContact(@RequestBody @Valid final ContactCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "create-supplier-contact",
            service.create(form)));
    }

    @PutMapping(value = {"/update-contact"})
    public ResponseEntity updateSupplierContact(@RequestParam final UUID uuid,
                                 @RequestBody @Valid final ContactCreateUpdateForm form) {
        return Response.ok(respFactory.success("58b7ed06d024", "update-supplier-contact",
            service.update(uuid, form)));
    }

    @GetMapping("/list-contacts/{uuid}")
    public ResponseEntity findSupplierContact(@PathVariable final UUID uuid) {
        return Response.ok(respFactory.success("58b7ed06d024",
            "findSupplierContact", service.findSupplierContact(uuid)));
    }

    @DeleteMapping("delete-supplier-contact")
    public ResponseEntity deleteSupplierContact(@RequestParam @NotNull final UUID contactUuid) {
        service.softDeleteContact(contactUuid);
        return Response.ok(respFactory.success("25b7ef03d028", "delete-supplier-contact"));
    }

    @Override
    public ResponseEntity filter(@PathVariable final String filter, final Pageable pageable) {
        final var list = service.findBy(filter, pageable);
        return Response.ok(respFactory.success("28b7ed07e084", "supplier-filtered", list));
    }

    @PostMapping("/create-images")
    public ResponseEntity createImages(@RequestBody @Valid final SupplierImageCreateForm form) {
        return Response.ok(respFactory.success("10b5ef06l044", "vehicle-create-images",
            service.createImages(form)));
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
                         @RequestBody @Valid final SupplierFilterForm filterForm,
                         final Pageable pageable) throws Exception {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGenerator(response.getOutputStream(), filterForm);
    }

}
