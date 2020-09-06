package com.minetec.backend.entity.workshop;

import com.minetec.backend.dto.enums.JobCardStatus;
import com.minetec.backend.dto.enums.ReportType;
import com.minetec.backend.entity.AbstractEntity;
import com.minetec.backend.entity.Image;
import com.minetec.backend.entity.User;
import com.minetec.backend.entity.Vehicle;
import com.minetec.backend.util.converter.LocalDateTimeConverter;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "JOB_CARD",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"UUID"}, name = "UX_JOB_CARD__UUID")},
    indexes = {
        @Index(columnList = "VEHICLE_ID", name = "IX_JOB_CARD__VEHICLE_ID"),
        @Index(columnList = "OPERATOR_USER_ID", name = "IX_JOB_CARD__OPERATOR_USER_ID"),
        @Index(columnList = "SUPERVISOR_USER_ID", name = "IX_JOB_CARD__SUPERVISOR_USER_ID"),
        @Index(columnList = "MECHANIC_USER_ID", name = "IX_JOB_CARD__MECHANIC_USER_ID"),
        @Index(columnList = "FOREMAN_USER_ID", name = "IX_JOB_CARD__FOREMAN_USER_ID"),
    })
@Data
@ToString(exclude = {"vehicle", "jobCardStatus",
    "reportType", "operatorUser", "supervisorUser", "mechanicUser", "foremanUser", "jobCardItems", "jobCardHistories"})
@EqualsAndHashCode(callSuper = true, exclude = {"vehicle", "jobCardStatus", "reportType",
    "operatorUser", "supervisorUser", "mechanicUser", "foremanUser", "jobCardItems", "jobCardHistories"})
public class JobCard extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "VEHICLE_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_JOB_CARD__VEHICLE_ID"))
    private Vehicle vehicle;

    @Column(name = "REPORT_NUMBER")
    private String reportNumber;

    @Column(name = "STATUS")
    @Enumerated(EnumType.STRING)
    private JobCardStatus jobCardStatus;

    @Column(name = "ACTIVE", nullable = false)
    private boolean isActive = true;

    @Column(name = "RISK_ASSESSMENT")
    private boolean riskAssessment = false;

    @Column(name = "LOCK_OUT_PROCEDURE")
    private boolean lockOutProcedure = false;

    @Column(name = "WHEEL_NUTS")
    private boolean wheelNuts = false;

    @Column(name = "OIL_LEVEL")
    private boolean oilLevel = false;

    @Column(name = "MACHINE_GREASE")
    private boolean machineGrease = false;

    @Column(name = "START_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime startDate;

    @Column(name = "END_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime endDate;

    @Column(name = "BREAKDOWN_START_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime breakDownStartDate;

    @Column(name = "BREAKDOWN_END_DATE")
    @Convert(converter = LocalDateTimeConverter.class)
    private LocalDateTime breakDownEndDate;

    @Column(name = "DESCRIPTION", nullable = false, length = 2000)
    private String description;

    @Column(name = "CURRENT_KM_HOUR", nullable = false, length = 50)
    private String currentKmHour;

    @Column(name = "REPORT_TYPE")
    @Enumerated(EnumType.STRING)
    private ReportType reportType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OPERATOR_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD__OPERATOR_USER_ID"))
    private User operatorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SUPERVISOR_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD__SUPERVISOR_USER_ID"))
    private User supervisorUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MECHANIC_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD__MECHANIC_USER_ID"))
    private User mechanicUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "FOREMAN_USER_ID", foreignKey = @ForeignKey(name = "FK_JOB_CARD__FOREMAN_USER_ID"))
    private User foremanUser;

    @OneToMany(mappedBy = "jobCard", fetch = FetchType.LAZY)
    private List<Image> images = new ArrayList<>();

    @OneToMany(mappedBy = "jobCard", fetch = FetchType.LAZY)
    private List<JobCardItem> jobCardItems = new ArrayList<>();

    @OneToMany(mappedBy = "jobCard", fetch = FetchType.LAZY)
    private List<JobCardHistory> jobCardHistories = new ArrayList<>();
}
