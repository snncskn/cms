package com.minetec.backend.service.workshop;

import com.minetec.backend.dto.enums.JobCardItemStatus;
import com.minetec.backend.dto.enums.JobCardStatus;
import com.minetec.backend.dto.enums.ReportType;
import com.minetec.backend.dto.filter.workshop.RequestListFilterForm;
import com.minetec.backend.dto.filter.workshop.WorkshopFilterForm;
import com.minetec.backend.dto.form.workshop.BreakDownCreateUpdateForm;
import com.minetec.backend.dto.form.workshop.JobCardCreateUpdateForm;
import com.minetec.backend.dto.form.workshop.JobCardHistoryCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardImageCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardItemCreateForm;
import com.minetec.backend.dto.form.workshop.JobCardItemCreateRequestForm;
import com.minetec.backend.dto.form.workshop.JobCardUpdateForm;
import com.minetec.backend.dto.info.workshop.BreakDownListInfo;
import com.minetec.backend.dto.info.workshop.JobCardHistoryListInfo;
import com.minetec.backend.dto.info.workshop.JobCardInfo;
import com.minetec.backend.dto.info.workshop.JobCardItemDeliveredListInfo;
import com.minetec.backend.dto.info.workshop.JobCardItemInfo;
import com.minetec.backend.dto.info.workshop.RequestListInfo;
import com.minetec.backend.dto.mapper.RequestListMapper;
import com.minetec.backend.dto.mapper.workshop.BreakDownListMapper;
import com.minetec.backend.dto.mapper.workshop.JobCardHistoryMapper;
import com.minetec.backend.dto.mapper.workshop.JobCardItemMapper;
import com.minetec.backend.dto.mapper.workshop.JobCardMapper;
import com.minetec.backend.entity.StockHistory;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.entity.workshop.JobCard;
import com.minetec.backend.entity.workshop.JobCardHistory;
import com.minetec.backend.entity.workshop.JobCardItem;
import com.minetec.backend.error_handling.exception.ErrorOccurredException;
import com.minetec.backend.repository.workshop.JobCardHistoryRepository;
import com.minetec.backend.repository.workshop.JobCardRepository;
import com.minetec.backend.service.EntityService;
import com.minetec.backend.service.ImageService;
import com.minetec.backend.service.ItemService;
import com.minetec.backend.service.SiteService;
import com.minetec.backend.service.StockHistoryService;
import com.minetec.backend.service.StockService;
import com.minetec.backend.service.UserService;
import com.minetec.backend.util.Utils;
import com.minetec.backend.util.excel_generator.RequestListExcel;
import com.minetec.backend.util.excel_generator.WorkshopListExcel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Sinan
 */
@Service
@RequiredArgsConstructor
public class JobCardService extends EntityService<JobCard, JobCardRepository> {

    private static final int LEFT_PAD_LENGTH = 10;

    private final SiteService siteService;
    private final StockService stockService;
    private final StockHistoryService stockHistoryService;
    private final UserService userService;
    private final ImageService imageService;
    private final JobCardItemService jobCardItemService;
    private final ItemService itemService;
    private final JdbcTemplate jdbcTemplate;
    private final JobCardHistoryRepository jobCardHistoryRepository;

    /**
     * @param uuid
     * @return
     */
    public JobCardInfo find(@NotNull final UUID uuid) {
        final var jobCard = this.findByUuid(uuid);
        JobCardInfo info = JobCardMapper.toJobCardInfo(jobCard);
        if (!JobCardStatus.CLOSED_JOB_CARD.equals(jobCard.getJobCardStatus())) {
            info.setCurrentKmHour(jobCard.getVehicle().getCurrentMachineHours());
        }
        return info;
    }

    /**
     * JobCardCreateUpdateForm
     *
     * @param form
     * @return
     */
    public BreakDownListInfo createBreakDown(@NotNull final Vehicle vehicle,
                                             @NotNull final BreakDownCreateUpdateForm form) {
        var jobCard = new JobCard();
        jobCard.setVehicle(vehicle);
        jobCard.setOperatorUser(userService.findByUuid(form.getOperatorUuid()));
        jobCard.setJobCardStatus(JobCardStatus.ACTIVE_BREAKDOWN_REPORT);
        jobCard.setReportType(ReportType.valueOf(form.getReportType()));
        jobCard.setCurrentKmHour(jobCard.getVehicle().getCurrentMachineHours());
        jobCard.setReportNumber(newReportNumber(jobCard.getReportType()));
        jobCard.setDescription(form.getDescription());
        jobCard.setBreakDownStartDate(LocalDateTime.now());
        jobCard.setSupervisorUser(this.getCurrentUser());
        var newJobCard = this.persist(jobCard);
        return JobCardMapper.toInfo(newJobCard);
    }

    /**
     * @param form
     * @return
     */
    public BreakDownListInfo createJobCard(@NotNull final JobCardCreateUpdateForm form) {
        var jobCard = this.findByUuid(form.getJobCardUuid());
        jobCard.setJobCardStatus(JobCardStatus.ACTIVE_JOB_CARD);
        jobCard.setBreakDownEndDate(LocalDateTime.now());
        jobCard.setStartDate(LocalDateTime.now());
        jobCard.setOperatorUser(userService.findByUuid(form.getOperatorUuid()));
        var newJobCard = this.persist(jobCard);
        return JobCardMapper.toInfo(newJobCard);
    }

    /**
     * @param reportType
     * @return
     */
    public String newReportNumber(@NotNull final ReportType reportType) {
        var newSeq = "";
        switch (reportType) {
            case BREAKDOWN:
                newSeq = jdbcTemplate.queryForObject("SELECT nextval('BRK_NUMBER_SEQ')", String.class);
                break;
            case SERVICE:
                newSeq = jdbcTemplate.queryForObject("SELECT nextval('SRH_NUMBER_SEQ')", String.class);
                break;
            default:
                break;
        }
        var ret = new StringBuilder(reportType.getType()).append(" ");
        return ret.append(StringUtils.leftPad(newSeq, LEFT_PAD_LENGTH, "0")).toString();
    }

    /**
     * @param form
     * @return
     */
    public boolean createImages(@NotNull final JobCardImageCreateForm form) {
        JobCard jobCard = this.findByUuid(form.getJobCardUuid());
        imageService.updateTo(jobCard, form.getImageInfos());
        return true;
    }

    /**
     * @param form
     * @return
     */
    public Page<BreakDownListInfo> findAllBreakDown(@NotNull @Valid final WorkshopFilterForm form,
                                                    final Pageable pageable) {
        String status = "";
        for (var value : JobCardStatus.values()) {
            if (value.getStatus().equals(form.getStatus())) {
                status = value.name();
                break;
            }
        }

        final var site = siteService.findByUuid(this.getContextDetail().getSiteUuid());
        String siteName = form.getSite();

        if (Utils.isEmpty(siteName)) {
            siteName = site.getDescription();
        }

        Page<JobCard> jobCards = this.getRepository().filter(
            Utils.toLocalDate(form.getBreakDownStartDate()), Utils.toLocalDate(form.getBreakDownEndDate()),
            form.getFleetNumber(), form.getReportNumber(), siteName, pageable);
        final List<BreakDownListInfo> items =
            jobCards.stream().map(BreakDownListMapper::toMap).collect(Collectors.toList());
        items.forEach(item -> {
            item.getImageInfos().forEach(img -> {
                img.setDownloadUrl(imageService.imageUrl(img.getUuid()));
            });
        });
        return new PageImpl<>(items, pageable, jobCards.getTotalElements());
    }


    /**
     * @param form
     * @return
     */
    public Page<BreakDownListInfo> findAllBreakDownByVehicleUuid(@NotNull @Valid final WorkshopFilterForm form) {

        Pageable pageable = PageRequest.of(form.getPage(), form.getSize(),
            Sort.by(Sort.Order.asc("jobCardStatus")));

        if (Utils.isEmpty(form.getFilter())) {
            form.setFilter("");
        }

        Page<JobCard> jobCards = this.getRepository().filterByVehicleUuid(
            Utils.toLocalDate(form.getBreakDownStartDate()), Utils.toLocalDate(form.getBreakDownEndDate()),
            form.getVehicleUuid(), pageable);

        final List<BreakDownListInfo> items =
            jobCards.stream().map(BreakDownListMapper::toMap).collect(Collectors.toList());

        return new PageImpl<>(items, pageable, jobCards.getTotalElements());
    }


    /**
     * @param jobCardUuid
     * @param pageable
     * @return
     */
    public Page<JobCardHistoryListInfo> findJobCardHistory(@NotNull final UUID jobCardUuid,
                                                           final Pageable pageable) {
        Page<JobCardHistory> jobCardHistories =
            jobCardHistoryRepository.findJobCardHistoryByJobCardUuid(jobCardUuid, pageable);
        final List<JobCardHistoryListInfo> items =
            jobCardHistories.stream().map(JobCardHistoryMapper::toInfo).collect(Collectors.toList());
        return new PageImpl<>(items, pageable, jobCardHistories.getTotalElements());


    }

    /**
     * @param form
     * @return
     */
    public boolean createJobCardHistory(@NotNull final JobCardHistoryCreateForm form) {
        final var jobCard = this.findByUuid(form.getJobCardUuid());
        if (jobCardClosed(jobCard)) {
            throw new ErrorOccurredException("0", "This job is closed...");
        }
        var jch = new JobCardHistory();
        jch.setJobCard(jobCard);
        jch.setDescription(form.getDescription());
        jobCardHistoryRepository.save(jch);

        this.updateJobCard(jobCard);

        return true;
    }

    /**
     * @param form
     * @return
     */
    public boolean createJobCardItem(@NotNull final JobCardItemCreateForm form) {
        final var jobCard = this.findByUuid(form.getJobCardUuid());

        if (jobCardClosed(jobCard)) {
            throw new ErrorOccurredException("0", "This job is closed...");
        }

        /**
         * TODO : this method have to change, so have to add a edit method.
         */
        var jobCardItem = new JobCardItem();
        for (var jci : jobCard.getJobCardItems()) {
            if (jci.getItem().getUuid().equals(form.getItemUuid())) {
                jobCardItem = jci;
            }
        }
        jobCardItem.setJobCard(jobCard);
        jobCardItem.setItem(itemService.findByUuid(form.getItemUuid()));
        jobCardItem.setQuantity(jobCardItem.getQuantity().add(form.getQuantity()));
        jobCardItem.setJobCardItemStatus(JobCardItemStatus.REQUESTED);
        jobCardItem.setDescription(form.getDescription());
        jobCardItem.setRequestDate(LocalDateTime.now());
        jobCardItem.setRequestUser(this.getCurrentUser());

        jobCardItem.setApproveQuantity(stockService.getRepository().sumQuantity(
            jobCardItem.getJobCard().getVehicle().getSite().getId(), jobCardItem.getItem().getId()));

        if (Utils.isEmpty(jobCardItem.getApproveQuantity())) {
            jobCardItem.setApproveQuantity(BigDecimal.ZERO);
        }
        jobCardItemService.persist(jobCardItem);

        this.updateJobCard(jobCard);

        return true;
    }

    /**
     * @param jobCard
     * @return
     */
    private boolean jobCardClosed(@NotNull final JobCard jobCard) {
        return JobCardStatus.CLOSED_JOB_CARD.equals(jobCard.getJobCardStatus());
    }


    /**
     * @param jobCardUuid
     * @param pageable
     * @return
     */
    public Page<JobCardItemInfo> findJobCardItem(@NotNull final UUID jobCardUuid, final Pageable pageable) {
        Page<JobCardItem> jobCardItems = jobCardItemService.getRepository().findByJobCardUuid(jobCardUuid, pageable);

        jobCardItems.forEach(info -> {
            var sumDeliveredQuantity = stockHistoryService.getRepository().sumOutputQuantity(
                info.getJobCard().getVehicle().getSite().getId(), info.getId());

            info.setDeliveredQuantity(sumDeliveredQuantity);

            var sumAvailableQuantity = stockService.getRepository().sumQuantity(
                info.getJobCard().getVehicle().getSite().getId(), info.getItem().getId());

            info.setAvailableQuantity(sumAvailableQuantity);
        });

        final List<JobCardItemInfo> infos =
            jobCardItems.stream().map(JobCardItemMapper::toInfo).collect(Collectors.toList());

        return new PageImpl<>(infos, pageable, jobCardItems.getTotalElements());
    }


    /**
     * @param jobCardItemUuid
     * @return
     */
    public boolean deleteJobCardItem(@NotNull final UUID jobCardItemUuid) {
        final var jobCardItem = jobCardItemService.findByUuid(jobCardItemUuid);
        var stockList = stockHistoryService.getRepository().
            findByJobCardItem(jobCardItem.getJobCard().getVehicle().getSite().getId(), jobCardItem.getId());
        if (Utils.isEmpty(stockList)) {
            this.updateJobCard(jobCardItem.getJobCard());
            jobCardItemService.delete(jobCardItem);
            return true;
        }
        return false;
    }

    /**
     * @param jobCardItemUuid
     * @param pageable
     * @return
     */
    public Page<JobCardItemDeliveredListInfo> findJobCardItemDelivered(@NotNull final UUID jobCardItemUuid,
                                                                       final Pageable pageable) {
        final var jobCardItem = jobCardItemService.findByUuid(jobCardItemUuid);
        Page<StockHistory> stockHistories = stockHistoryService.getRepository().findByJobCardItem(
            jobCardItem.getJobCard().getVehicle().getSite().getId(), jobCardItem.getId(), pageable);
        final List<JobCardItemDeliveredListInfo> items =
            stockHistories.stream().map(JobCardItemMapper::toDeliveredInfo).collect(Collectors.toList());
        return new PageImpl<>(items, pageable, stockHistories.getTotalElements());
    }

    /**
     * close job card state
     *
     * @param jobCardUuid
     * @return
     */
    public boolean closeJobCard(@NotNull final UUID jobCardUuid) {

        var jobCard = this.findByUuid(jobCardUuid);

        if (!(jobCard.isRiskAssessment() && jobCard.isLockOutProcedure() &&
            jobCard.isMachineGrease() && jobCard.isOilLevel() && jobCard.isWheelNuts())) {
            return false;
        }

        for (JobCardItem cardItem : jobCard.getJobCardItems()) {
            if (!JobCardItemStatus.DELIVERED.equals(cardItem.getJobCardItemStatus())) {
                return false;
            }
        }

        jobCard.setJobCardStatus(JobCardStatus.CLOSED_JOB_CARD);
        jobCard.setEndDate(LocalDateTime.now());
        jobCard.setCurrentKmHour(jobCard.getVehicle().getCurrentMachineHours());
        this.persist(jobCard);



        return true;
    }

    /**
     * @param jobCardUuid
     * @param form
     * @return
     */
    public boolean updateJobCard(@NotNull final UUID jobCardUuid, @NotNull @Valid final JobCardUpdateForm form) {

        final var jobCard = this.findByUuid(form.getJobCardUuid());

        if (jobCardClosed(jobCard)) {
            throw new ErrorOccurredException("0", "This job is closed...");
        }

        jobCard.setRiskAssessment(form.isRiskAssessment());
        jobCard.setLockOutProcedure(form.isLockOutProcedure());
        jobCard.setWheelNuts(form.isWheelNuts());
        jobCard.setOilLevel(form.isOilLevel());
        jobCard.setMachineGrease(form.isMachineGrease());
        jobCard.setMechanicUser(userService.findByUuid(form.getMechanicUserUuid()));
        jobCard.setForemanUser(userService.findByUuid(form.getForemanUserUuid()));
        jobCard.setOperatorUser(userService.findByUuid(form.getOperatorUserUuid()));
        this.persist(jobCard);
        return true;
    }


    /**
     * @param form
     * @return
     */
    public Page<RequestListInfo> findAllRequestList(@NotNull @Valid final RequestListFilterForm form,
                                                    final Pageable pageable) {

        Page<JobCardItem> items = jobCardItemService.getRepository().filter(
            Utils.toLocalDate(form.getRequestStartDate()), Utils.toLocalDate(form.getRequestEndDate()),
            getContextDetail().getSiteUuid(), form.getRequestUser(), form.getJobCardNumber(), form.getFleetNumber(),
            form.getStockCode(), form.getItemDescription(), pageable);

        items.forEach(item -> {

            BigDecimal sumOutputQuantity = stockHistoryService.getRepository().sumOutputQuantity(
                item.getJobCard().getVehicle().getSite().getId(), item.getId());
            if (!Utils.isEmpty(sumOutputQuantity)) {
                item.setDeliveredQuantity(sumOutputQuantity);
            }

            BigDecimal sumAvailableQuantity = stockService.getRepository().sumQuantity(
                item.getJobCard().getVehicle().getSite().getId(), item.getItem().getId());
            if (!Utils.isEmpty(sumAvailableQuantity)) {
                item.setAvailableQuantity(sumAvailableQuantity);
            }

        });

        final List<RequestListInfo> requestListInfos =
            items.stream().map(RequestListMapper::toMap).collect(Collectors.toList());


        return new PageImpl<>(requestListInfos, pageable, items.getTotalElements());
    }

    /**
     * @param form
     * @return
     */
    public boolean createJobCardItemRequest(@NotNull final JobCardItemCreateRequestForm form) {
        var jobCardItem = jobCardItemService.findByUuid(form.getJobCardItemUuid());
        jobCardItem.setReceivedUser(userService.findByUuid(form.getReceivedUserUuid()));
        jobCardItem.setJobCardItemStatus(JobCardItemStatus.DELIVERED);
        jobCardItem.setDeliveredUser(this.getCurrentUser());
        jobCardItem.setDeliveredDate(LocalDateTime.now());

        final var sumDelivered = form.getDeliverQuantity().add(form.getDeliveredQuantity());
        if (!Utils.equals(sumDelivered, form.getRequestedQuantity())) {
            jobCardItem.setJobCardItemStatus(JobCardItemStatus.PARTIAL);
        }
        final var receivedUser = userService.findByUuid(form.getReceivedUserUuid());
        jobCardItemService.persist(jobCardItem);
        stockService.createJobCard(jobCardItem, form.getDeliverQuantity());
        stockHistoryService.createJobCardItem(jobCardItem, form.getDeliverQuantity(), receivedUser);

        this.updateJobCard(jobCardItem.getJobCard());

        return true;
    }


    /**
     * for workshop
     * @param response
     * @param filterForm
     * @return
     */
    public void xlsGenerator(final ServletOutputStream response,
                             @NotNull @Valid final WorkshopFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        Page<BreakDownListInfo> breakDownList = this.findAllBreakDown(filterForm, pageable);
        var excel = new WorkshopListExcel();
        excel.generator(response, breakDownList);
    }

    /**
     * for request
     *
     * @param response
     * @param filterForm
     * @return
     */
    public void xlsGeneratorRequest(final ServletOutputStream response,
                                    @NotNull @Valid final RequestListFilterForm filterForm) throws IOException {
        Pageable pageable = PageRequest.of(filterForm.getPage(), filterForm.getSize());
        Page<RequestListInfo> requestListInfos = this.findAllRequestList(filterForm, pageable);
        var excel = new RequestListExcel();
        excel.generator(response, requestListInfos);
    }

    /**
     * @param jobCard
     */
    private void updateJobCard(@NotNull final JobCard jobCard) {
        jobCard.setModifiedAt(LocalDateTime.now());
        this.persist(jobCard);
    }
}
