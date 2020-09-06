package com.minetec.backend.controller.workshop;

import com.minetec.backend.dto.filter.workshop.RequestListFilterForm;
import com.minetec.backend.dto.filter.workshop.WorkshopFilterForm;
import com.minetec.backend.dto.form.workshop.BreakDownCreateUpdateForm;
import com.minetec.backend.dto.form.workshop.JobCardCreateUpdateForm;
import com.minetec.backend.dto.form.workshop.JobCardHistoryCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardImageCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardItemCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardItemCreateRequestForm;
import com.minetec.backend.dto.form.workshop.JobCardUpdateForm;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.repository.workshop.JobCardRepository;
import com.minetec.backend.service.EntityService;
import com.minetec.backend.service.workshop.JobCardFacade;
import com.minetec.backend.service.workshop.JobCardService;
import com.minetec.backend.util.Response;
import com.minetec.backend.util.RestResponseFactory;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.UUID;

/**
 * @author Sinan
 */
@RestController
@RequestMapping("/api/job-card")
@Secured(value = {"ROLE_ADMIN"})
@RequiredArgsConstructor
@CrossOrigin
public class JobCardController extends EntityService<JobCard, JobCardRepository> {

    private final JobCardService service;
    private final JobCardFacade jobCardFacade;
    private final RestResponseFactory respFactory;

    @GetMapping("/find-job-card/{jobCardUuid}")
    public ResponseEntity findOrderItem(@PathVariable @NotNull final UUID jobCardUuid) {
        return Response.ok(respFactory.success("18b4ed06d054", "find-job-card",
            service.find(jobCardUuid)));
    }

    @PostMapping("/list-all-break-down")
    public ResponseEntity listAllBreakDown(@RequestBody @Valid final WorkshopFilterForm form) {
        Pageable pageable = PageRequest.of(form.getPage(), form.getSize(),
            Sort.by(Sort.Order.asc("jobCardStatus"), Sort.Order.desc("breakDownStartDate")));
        return Response.ok(respFactory.success("23b7ed06d089", "list-all-break-down",
            service.findAllBreakDown(form, pageable)));
    }

    @PostMapping("/create-break-down")
    public ResponseEntity createBreakDown(@RequestBody @Valid final BreakDownCreateUpdateForm form) {
        return Response.ok(respFactory.success("23b7ed06d089", "create-break-down",
            jobCardFacade.createBreakDown(form)));
    }

    @PostMapping("/create-image-break-down")
    public ResponseEntity createImageBreakDown(@RequestBody @Valid final JobCardImageCreateForm form) {
        return Response.ok(respFactory.success("20b9ef06l046", "create-image-break-down",
            service.createImages(form)));
    }

    @PostMapping("/create-job-card")
    public ResponseEntity createJobCard(@RequestBody @Valid final JobCardCreateUpdateForm form) {
        return Response.ok(respFactory.success("83b7ed06d123", "create-job-card",
            service.createJobCard(form)));
    }

    @PostMapping("/create-image-job-card")
    public ResponseEntity createImageJobCard(@RequestBody @Valid final JobCardImageCreateForm form) {
        return Response.ok(respFactory.success("10b5ef06l044", "create-image-job-card",
            service.createImages(form)));
    }

    @GetMapping("/find-job-card-history/{jobCardUuid}")
    public ResponseEntity findJobCardHistory(@PathVariable @NotNull final UUID jobCardUuid, final Pageable pageable) {
        return Response.ok(respFactory.success("58b4ed06d054", "find-job-card-history",
            service.findJobCardHistory(jobCardUuid, pageable)));
    }

    @PostMapping("/create-job-card-history")
    public ResponseEntity createJobCardHistory(@RequestBody @Valid final JobCardHistoryCreateForm form) {
        return Response.ok(respFactory.success("60b5ef06l044", "create-job-card-history",
            service.createJobCardHistory(form)));
    }

    @PostMapping("/create-job-card-item")
    public ResponseEntity createJobCardItem(@RequestBody @Valid final JobCardItemCreateForm form) {
        return Response.ok(respFactory.success("80b5ef06l024", "create-job-card-item",
            service.createJobCardItem(form)));
    }

    @GetMapping("/find-job-card-item/{jobCardUuid}")
    public ResponseEntity findJobCardItem(@PathVariable @NotNull final UUID jobCardUuid, final Pageable pageable) {
        return Response.ok(respFactory.success("95b4ed06d024", "find-job-card-history",
            service.findJobCardItem(jobCardUuid, pageable)));
    }

    @PutMapping("/close-job-card/{jobCardUuid}")
    public ResponseEntity closeJobCard(@PathVariable @NotNull final UUID jobCardUuid) {
        final var ret = service.closeJobCard(jobCardUuid);
        if (ret) {
            jobCardFacade.updateVehicleLastServiceDate(jobCardUuid);
            return Response.ok(respFactory.success("14b3ed06h054", "close-job-card", true));
        } else {
            return Response.ok(respFactory.success("94b4ey04r034", "close-job-card-error", false));
        }
    }

    @PutMapping("/update-job-card/{jobCardUuid}")
    public ResponseEntity updateJobCard(@PathVariable @NotNull final UUID jobCardUuid,
                                        @RequestBody @Valid final JobCardUpdateForm form) {
        final var ret = service.updateJobCard(jobCardUuid, form);
        if (ret) {
            return Response.ok(respFactory.success("24b2hd06d054", "close-job-card", true));
        } else {
            return Response.ok(respFactory.success("34b4by04d034", "close-job-card-error", false));
        }
    }

    @PostMapping("/create-job-card-item-request")
        public ResponseEntity createJobCardItemRequest(@RequestBody @Valid final JobCardItemCreateRequestForm form) {
        return Response.ok(respFactory.success("80b5ef06l024", "create-job-card-item-request",
            service.createJobCardItemRequest(form)));
    }

    @GetMapping("/find-job-card-item-delivered/{jobCardItemUuid}")
    public ResponseEntity findJobCardItemDelivered(@PathVariable @NotNull final UUID jobCardItemUuid,
                                                   final Pageable pageable) {
        return Response.ok(respFactory.success("95b4ed06d024", "find-job-card-item-delivered",
            service.findJobCardItemDelivered(jobCardItemUuid, pageable)));
    }

    @DeleteMapping("/delete-job-card-item")
    public ResponseEntity deleteJobCardItem(@RequestParam @NotNull final UUID jobCardItemUuid) {
        final var ret = service.deleteJobCardItem(jobCardItemUuid);
        if (ret) {
            return Response.ok(respFactory.success("95b4ed06d024", "delete-job-card-item", true));
        } else {
            return Response.ok(respFactory.success("34b4by04d034", "delete-job-card-item-error", false));
        }
    }

    @PostMapping("/list-all-request-list")
    public ResponseEntity listAllRequestList(@RequestBody @Valid final RequestListFilterForm form,
                                             final Pageable pageable) {
        return Response.ok(respFactory.success("23b7ed06d089", "list-all-request-list",
            service.findAllRequestList(form, pageable)));
    }

    @PostMapping(value = "/xlsRequest")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final RequestListFilterForm filterForm) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGeneratorRequest(response.getOutputStream(), filterForm);
    }

    @PostMapping(value = "/xls")
    public void xls(final HttpServletResponse response,
                    @RequestBody @Valid final WorkshopFilterForm filterForm) throws IOException {
        response.setContentType("application/vnd.ms-excel");
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + RandomUtils.nextLong());
        service.xlsGenerator(response.getOutputStream(), filterForm);
    }
}

