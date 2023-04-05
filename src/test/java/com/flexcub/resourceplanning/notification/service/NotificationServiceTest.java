package com.flexcub.resourceplanning.notification.service;


import com.flexcub.resourceplanning.invoice.entity.*;
import com.flexcub.resourceplanning.invoice.repository.AdminInvoiceRepository;
import com.flexcub.resourceplanning.invoice.repository.SeekerInvoiceRepository;
import com.flexcub.resourceplanning.job.dto.*;
import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.job.entity.RequirementPhase;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.job.repository.JobRepository;
import com.flexcub.resourceplanning.job.repository.RequirementPhaseRepository;
import com.flexcub.resourceplanning.job.repository.SelectionPhaseRepository;
import com.flexcub.resourceplanning.job.service.SelectionPhaseService;
import com.flexcub.resourceplanning.notifications.dto.Notification;
import com.flexcub.resourceplanning.notifications.entity.*;
import com.flexcub.resourceplanning.notifications.repository.*;
import com.flexcub.resourceplanning.notifications.service.impl.NotificationServiceImpl;
import com.flexcub.resourceplanning.registration.entity.RegistrationEntity;
import com.flexcub.resourceplanning.registration.repository.RegistrationRepository;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillLevelEntity;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillYearOfExperience;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillYearOfExperienceRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerRepository;
import com.flexcub.resourceplanning.skillpartner.dto.JobHistory;
import com.flexcub.resourceplanning.skillpartner.entity.SkillPartnerEntity;
import com.flexcub.resourceplanning.skillseeker.entity.*;
import com.flexcub.resourceplanning.skillseeker.repository.*;
import com.flexcub.resourceplanning.skillseeker.service.impl.SkillSeekerServiceImpl;
import liquibase.pro.packaged.M;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = NotificationServiceImpl.class)
class NotificationServiceTest {


    static final String dateType = "MM-dd-yyyy";

    static final String timeType = "hh:mm a";

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(dateType);
    DateTimeFormatter time = DateTimeFormatter.ofPattern(timeType);


    @MockBean
    ModelMapper modelMapper;
    @Autowired
    NotificationServiceImpl notificationService;

    @MockBean
    OwnerNotificationsRepository ownerNotificationsRepository;

    @MockBean
    ContentRepository contentRepository;

    @MockBean
    JobRepository jobRepository;


    @MockBean
    SeekerNotificationsRepository seekerNotificationsRepository;

    @MockBean
    PartnerNotificationsRepository partnerNotificationsRepository;

    @MockBean
    SkillOwnerRepository skillOwnerRepository;

    @MockBean
    SelectionPhaseRepository selectionPhaseRepository;

    @MockBean
    RegistrationRepository registrationRepository;

    @MockBean
    SeekerModuleRepository seekerModuleRepository;

    @MockBean
    OwnerSkillYearOfExperienceRepository ownerSkillYearOfExperienceRepository;

    @MockBean
    SkillSeekerServiceImpl skillSeekerService;

    @Autowired
    SkillSeekerMsaRepository seekerMsaRepository;

    @MockBean
    PoRepository poRepository;

    @MockBean
    ContractStatusRepository contractStatusRepository;

    @MockBean
    StatementOfWorkRepository statementOfWorkRepository;
    @MockBean
    RequirementPhaseRepository requirementPhaseRepository;

    @MockBean
    SkillSeekerRepository skillSeekerRepository;

    @MockBean
    SuperAdminNotificationRepositoy superAdminNotificationRepositoy;

    @MockBean
    SkillSeekerMsaRepository skillSeekerMsaRepository;

    @MockBean
    SeekerInvoiceRepository seekerInvoiceRepository;

    @MockBean
    AdminInvoiceRepository adminInvoiceRepository;

    @MockBean
    SelectionPhaseService selectionPhaseService;

    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();

    List<SkillOwnerEntity> skillOwnerEntities = new ArrayList<>();
    PoEntity poEntity = new PoEntity();

    Job job = new Job();
    SeekerNotificationsEntity seekerNotificationsEntity = new SeekerNotificationsEntity();
    List<SeekerNotificationsEntity> seekerNotificationsEntities = new ArrayList<>();
    OwnerNotificationsEntity ownerNotificationsEntity = new OwnerNotificationsEntity();
    List<OwnerNotificationsEntity> ownerNotificationsEntities = new ArrayList<>();
    SkillPartnerEntity skillPartnerEntity = new SkillPartnerEntity();
    PartnerNotificationsEntity partnerNotificationsEntity = new PartnerNotificationsEntity();
    List<PartnerNotificationsEntity> partnerNotificationsEntitiesList = new ArrayList<>();
    SkillSeekerEntity skillSeekerEntity = new SkillSeekerEntity();
    SeekerModulesEntity seekerModulesEntity = new SeekerModulesEntity();
    List<SeekerModulesEntity> seekerModulesEntities = new ArrayList<>();
    SubRoles subRoles = new SubRoles();
    SkillSeekerMSAEntity skillSeekerMSAEntity = new SkillSeekerMSAEntity();
    Notification notification = new Notification();
    ContentEntity contentEntity = new ContentEntity();
    RequirementPhase requirementPhase = new RequirementPhase();
    List<RequirementPhase> requirementPhases = new ArrayList<>();
    SuperAdminNotifications superAdminNotifications = new SuperAdminNotifications();
    ContractStatus contractStatus = new ContractStatus();
    SkillSeekerMSAEntity skillSeekerMSA = new SkillSeekerMSAEntity();
    ScheduleInterviewDto scheduleInterviewDto = new ScheduleInterviewDto();
    SelectionPhase selectionPhase = new SelectionPhase();
    List<SelectionPhase> selectionPhases = new ArrayList<>();
    JobHistory jobHistory = new JobHistory();
    List<JobHistory> jobHistories = new ArrayList<>();
    RegistrationEntity registrationEntity = new RegistrationEntity();

    OwnerSkillYearOfExperience ownerSkillYearOfExperience = new OwnerSkillYearOfExperience();
    AcceptRejectDto acceptRejectDto = new AcceptRejectDto();
    NewSlotRequestBySeekerDto newSlotRequestBySeekerDto = new NewSlotRequestBySeekerDto();
    SkillSeekerProjectEntity skillSeekerProject = new SkillSeekerProjectEntity();
    SlotConfirmBySeekerDto slotConfirmBySeekerDto = new SlotConfirmBySeekerDto();
    SlotConfirmByOwnerDto slotConfirmByOwnerDto = new SlotConfirmByOwnerDto();

    InvoiceAdmin invoiceAdmin = new InvoiceAdmin();
    InvoiceStatus invoiceStatus = new InvoiceStatus();

    AdminInvoiceData adminInvoiceData = new AdminInvoiceData();

    List<AdminInvoiceData> adminInvoiceDataList = new ArrayList<>();

    SkillSeekerProjectEntity skillSeekerProjectEntity = new SkillSeekerProjectEntity();

    Invoice invoice = new Invoice();

    InvoiceData invoiceData = new InvoiceData();

    List<InvoiceData> invoiceDataList = new ArrayList<>();

    OwnerSkillLevelEntity ownerSkillLevelEntity = new OwnerSkillLevelEntity();


    StatementOfWorkEntity sowEntity=new StatementOfWorkEntity();

    SkillSeekerProjectEntity seekerProject=new SkillSeekerProjectEntity();



    @BeforeEach
    public void setup() {

        skillOwnerEntity.setSkillOwnerEntityId(1);
        skillOwnerEntity.setFirstName("kevin");
        skillOwnerEntity.setLastName("G");
        skillOwnerEntity.setSkillPartnerEntity(skillPartnerEntity);
        skillOwnerEntities.add(skillOwnerEntity);


        skillPartnerEntity.setSkillPartnerId(1);
        job.setJobId("FJB-00001");
        job.setJobTitle("Development");
        job.setSkillSeeker(skillSeekerEntity);
        job.setTaxIdBusinessLicense("1234");
        requirementPhase.setRequirementPhaseName("Initial screening");

        seekerNotificationsEntity.setOwnerId(1);
        seekerNotificationsEntity.setSkillOwnerId(scheduleInterviewDto.getSkillOwnerId());
        seekerNotificationsEntity.setJobId("FJB-00001");
        seekerNotificationsEntity.setContentId(1);
        seekerNotificationsEntity.setStage(1);
        seekerNotificationsEntity.setTitle(contentEntity.getTitle());
        seekerNotificationsEntity.setId(contentEntity.getId());
        seekerNotificationsEntity.setContentObj(contentEntity);
        seekerNotificationsEntity.setSkillSeekerEntityId(1);
        seekerNotificationsEntity.setTaxIdBusinessLicense("Abc-123");
        seekerNotificationsEntity.setContent("Hi its a notifiation");
        seekerNotificationsEntity.setDate(new Date(2023-01-24));
        seekerNotificationsEntity.setSkillOwnerId(1);
        seekerNotificationsEntity.setMarkAsRead(false);

        seekerNotificationsEntities.add(seekerNotificationsEntity);

        poEntity.setId(1);
        sowEntity.setId(1);
        skillSeekerMSA.setId(1);
        skillSeekerMSA.setJobId(job);
        skillSeekerMSA.setSkillSeekerId(1);
        skillSeekerMSA.setSkillOwnerEntity(skillOwnerEntity);
        skillSeekerMSA.setSkillSeekerProject(seekerProject);
        seekerProject.setId(1);
        seekerProject.setSkillSeeker(skillSeekerEntity);


        ownerNotificationsEntity.setSkillOwnerEntityId(skillOwnerEntity.getSkillOwnerEntityId());
        ownerNotificationsEntity.setSkillOwnerEntityId(1);
        ownerNotificationsEntity.setJobId("FJB-00001");
        ownerNotificationsEntity.setContentId(1);
        ownerNotificationsEntity.setContentId(contentEntity.getId());
        ownerNotificationsEntity.setTitle(contentEntity.getTitle());
        ownerNotificationsEntity.setStage(1);
        ownerNotificationsEntity.setMarkAsRead(false);
        ownerNotificationsEntity.setId(1);
        LocalDateTime now = LocalDateTime.now();
        ownerNotificationsEntity.setContent("Hi " + skillOwnerEntity.getFirstName() + " " + skillOwnerEntity.getLastName() + ", you are shortlisted for the - " + job.getJobTitle() + ", by the " + job.getSkillSeeker().getSkillSeekerName() + " on " + formatter.format(now) + ", Time : " + time.format(new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalTime()));
        ownerNotificationsEntity.setRequirementPhaseName(requirementPhase.getRequirementPhaseName());
        ownerNotificationsEntity.setDateOfInterview((LocalDate.now()));
        ownerNotificationsEntity.setTimeOfInterview(LocalTime.now());
        ownerNotificationsEntities.add(ownerNotificationsEntity);

        skillPartnerEntity.setSkillPartnerId(1);

        contractStatus.setId(1);
        contractStatus.setStatus("");

        acceptRejectDto.setJobId(job.getJobId());
        acceptRejectDto.setSkillOwnerEntityId(skillOwnerEntity.getSkillOwnerEntityId());
        acceptRejectDto.setAccepted(true);

        partnerNotificationsEntity.setSkillPartnerEntityId(1);
        partnerNotificationsEntity.setContentId(contentEntity.getId());
        partnerNotificationsEntity.setTitle("PO-" + contentEntity.getTitle());
        partnerNotificationsEntity.setContentObj(contentEntity);
        partnerNotificationsEntity.setSkillPartnerEntityId(1);
        partnerNotificationsEntity.setOwnerId(1);
        partnerNotificationsEntity.setJobId("");
        partnerNotificationsEntity.setMarkAsRead(false);
        partnerNotificationsEntity.setDate(new Date());
        partnerNotificationsEntity.setContent("");
        partnerNotificationsEntity.setDateOfInterview(LocalDate.now());
        partnerNotificationsEntitiesList.add(partnerNotificationsEntity);

        skillSeekerMSAEntity.setId(1);
        skillSeekerMSAEntity.setMsaStatus(contractStatus);
        skillSeekerMSAEntity.setSkillOwnerEntity(skillOwnerEntity);
        skillSeekerMSAEntity.setJobId(job);
        skillSeekerMSAEntity.setSkillSeekerId(skillSeekerEntity.getId());
        skillSeekerMSA.setId(1);
        skillSeekerMSA.setMsaStatus(contractStatus);
        skillSeekerEntity.setSkillSeekerName("Worth");

        subRoles.setId(1);

        skillSeekerEntity.setId(1);
        skillSeekerEntity.setTaxIdBusinessLicense("Abc-123");
        skillSeekerEntity.setSubRoles(subRoles);
        skillSeekerEntity.setSkillSeekerName("soundarya");

        skillSeekerProject.setId(1);
        poEntity.setPoStatus(contractStatus);
        poEntity.setSkillOwnerEntity(skillOwnerEntity);
        poEntity.setJobId(job);
        poEntity.setSkillSeekerId(skillSeekerEntity.getId());
        poEntity.setSkillSeekerProject(skillSeekerProject);
        seekerModulesEntity.setId(1);

        seekerModulesEntities.add(seekerModulesEntity);
        contentEntity.setId(1);
        contentEntity.setTitle("");

        notification.setRequirementPhaseName(requirementPhase.getRequirementPhaseName());
        notification.setDateOfInterview(LocalDate.now());
        notification.setTimeOfInterview(LocalTime.now());
        notification.setStage(1);
//        notification.setContent("Hi " + skillOwnerEntity.getFirstName() + " " + skillOwnerEntity.getLastName() + ", You are scheduled for an interview for the role " + job.getJobTitle() + " - " + "Round " + notification.getStage() +
//                " - " + notification.getRequirementPhaseName() + " with " + job.getSkillSeeker().getSkillSeekerName() + " on " + formatter.format(notification.getDateOfInterview()) + " ,Time : " + time.format(notification.getTimeOfInterview()));

        notification.setContentId(1);
        notification.setContent("abc");
        notification.setStage(1);
        notification.setDate(new Date());
        notification.setTitle("");
        notification.setId(1);

        scheduleInterviewDto.setSkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId());
        scheduleInterviewDto.setJobId(job.getJobId());
        scheduleInterviewDto.setStage(1);
        scheduleInterviewDto.setEndTimeOfInterview(LocalTime.now().plusHours(1));
        scheduleInterviewDto.setDateOfInterview(LocalDate.now());
        scheduleInterviewDto.setInterviewedBy("sou");
        scheduleInterviewDto.setTimeOfInterview(LocalTime.now());
        scheduleInterviewDto.setModeOfInterview("online");

        selectionPhase.setSelectionId(1);
        selectionPhase.setJob(job);
        selectionPhase.setSkillOwnerEntity(skillOwnerEntity);
        selectionPhase.setRequirementPhase(requirementPhases);
        selectionPhase.setAccepted(true);
        selectionPhase.setInterviewAccepted(true);
        selectionPhase.setJoinedOn(LocalDate.now());
        selectionPhase.setRejectedOn(LocalDate.now());
        selectionPhase.getSkillOwnerEntity().getExpYears();
        selectionPhase.setCurrentStage(1);

        jobHistory.setJobId(job.getJobId());
        jobHistories.add(jobHistory);

        newSlotRequestBySeekerDto.setJobId("FJB-00001");
        newSlotRequestBySeekerDto.setSkillOwnerEntityId(skillOwnerEntity.getSkillOwnerEntityId());
        newSlotRequestBySeekerDto.setNewSlotRequested(true);

        registrationEntity.setId(skillSeekerEntity.getId());

        ownerSkillYearOfExperience.setId(1);


        superAdminNotifications.setMsaId(skillSeekerMSA.getId());
        superAdminNotifications.setTitle(contentEntity.getTitle());
        superAdminNotifications.setId(1);
        superAdminNotifications.setContent("MSA");
        superAdminNotifications.setSkillSeekerEntityId(skillSeekerEntity.getId());
        superAdminNotifications.setContentId(1);
        superAdminNotifications.setMarkAsRead(true);
        superAdminNotifications.setContentObj(contentEntity);
        superAdminNotifications.setDate(new Date());
        superAdminNotifications.setMsaStatus(contractStatus.getStatus());
        invoiceStatus.setId(1);
        invoice.setInvoiceStatus(invoiceStatus);
        invoice.setInvoiceData(invoiceDataList);
        invoiceData.setId(1);
        invoiceData.setSkillSeeker(skillSeekerEntity);
        invoiceAdmin.setInvoiceData(adminInvoiceDataList);

        invoiceAdmin.setInvoiceStatus(invoiceStatus);
        invoiceAdmin.setInvoiceStatus(invoiceStatus);
        slotConfirmBySeekerDto.setJobId("FJB-00001");
        slotConfirmBySeekerDto.setSkillOwnerEntityId(1);
        slotConfirmBySeekerDto.setSlotConfirmed(true);

        slotConfirmByOwnerDto.setJobId("FJB-00001");
        slotConfirmByOwnerDto.setSkillOwnerEntityId(1);

        invoiceAdmin.setInvoiceStatus(invoiceStatus);
        adminInvoiceData.setSkillSeeker(skillSeekerEntity);

    }

    @Test
    void getSeekerLastFiveNotificationTest() {
        Mockito.when(skillSeekerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillSeekerEntity));
        Mockito.when(seekerNotificationsRepository.findLastFiveNotificationByTaxId(Mockito.anyString())).thenReturn(seekerNotificationsEntities);
        Mockito.when(skillSeekerService.getAccessDetails(Mockito.anyInt())).thenReturn(seekerModulesEntities);
        assertEquals(1, notificationService.getSeekerLastFiveNotification(skillSeekerEntity.getId()).size());
    }


    @Test
    void getSeekerNotificationByOwnerTest() {
        Mockito.when(seekerNotificationsRepository.findByOwnerIdAndJobId(Mockito.anyInt(), Mockito.anyString())).thenReturn(Optional.of(seekerNotificationsEntities));
        assertEquals(1, notificationService.getSeekerNotificationByOwner(skillOwnerEntity.getSkillOwnerEntityId(), job.getJobId()).size());
    }

    @Test
    void getLastFiveNotificationOfPartnerTest() {
        Mockito.when(partnerNotificationsRepository.findLastFiveNotification(Mockito.anyInt())).thenReturn(partnerNotificationsEntitiesList);
        assertEquals(1, notificationService.getLastFiveNotificationOfPartner(skillPartnerEntity.getSkillPartnerId()).size());
    }

    @Test
    void getNotificationForParticularOwnerTest() {
        Mockito.when(partnerNotificationsRepository.findByOwnerIdAndJobId(Mockito.anyInt(), Mockito.anyString())).thenReturn(Optional.ofNullable(partnerNotificationsEntitiesList));
        assertEquals(1, notificationService.getNotificationForParticularOwner(skillOwnerEntity.getSkillOwnerEntityId(), job.getJobId()).size());
    }


//    @Test
//    void getJobHistoryInPartnerTest() {
//
//        Mockito.when(selectionPhaseRepository.findBySkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId())).thenReturn(Optional.ofNullable(selectionPhases));
//        Mockito.when(registrationRepository.findById(skillSeekerEntity.getId())).thenReturn(Optional.of(registrationEntity));
//        Mockito.when(ownerSkillYearOfExperienceRepository.findByExperience(Mockito.anyString())).thenReturn(ownerSkillYearOfExperience);
//        assertEquals(1, notificationService.getJobHistoryInPartner(skillOwnerEntity.getSkillOwnerEntityId()).size());
//    }




    @Test
    void getOwnerDetailsInPartnerTest() {
        Mockito.when(skillOwnerRepository.findBySkillPartnerId(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntities));
        Mockito.when(requirementPhaseRepository.findBySkillOwnerId(Mockito.anyInt())).thenReturn(Optional.ofNullable(requirementPhases));
        Mockito.when(selectionPhaseRepository.findBySkillOwnerId(Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhases));
        assertEquals(skillOwnerEntity.getSkillOwnerEntityId(), notificationService.getOwnerDetailsInPartner(skillOwnerEntity.getSkillOwnerEntityId()).size());

    }

    @Test
    void getLastFiveNotificationOfOwnerTest() {
        Mockito.when(ownerNotificationsRepository.findLastFiveNotification(skillOwnerEntity.getSkillOwnerEntityId())).thenReturn(ownerNotificationsEntities);

        assertEquals(1, notificationService.getLastFiveNotificationOfOwner(skillOwnerEntity.getSkillOwnerEntityId()).size());

    }

    @Test
    void markAsReadPartnerTest() {
        Mockito.when(partnerNotificationsRepository.findById(Mockito.anyInt())).thenReturn(partnerNotificationsEntity);
        assertTrue(notificationService.markAsReadPartner(1));
    }

    @Test
    void markAsReadOwnerTest() {
        Mockito.when(ownerNotificationsRepository.findById(1)).thenReturn(ownerNotificationsEntity);
        assertTrue(notificationService.markAsReadOwner(1));

    }

    @Test
    void markAsReadSeekerTest() {
        Mockito.when(seekerNotificationsRepository.findById(Mockito.anyInt())).thenReturn(seekerNotificationsEntity);
        assertTrue(notificationService.markAsReadSeeker(1));
    }

    @Test
    void reinitiateNotificationTest() {
        Mockito.when(contentRepository.findByReinitiation()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(selectionPhase.getSkillOwnerEntity().getSkillOwnerEntityId())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity.getId(), notificationService.reinitiateNotification(selectionPhase).getId());

    }

    @Test
    void reinitiateNotificationToPartnerTest() {
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(selectionPhase.getSkillOwnerEntity().getSkillOwnerEntityId())).thenReturn(skillOwnerEntity);
        Mockito.when(contentRepository.findByReinitiation()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.reinitiateNotification(selectionPhase).getId());
    }

    @Test
    void rejectedNotificationTest() {
        Mockito.when(contentRepository.findByRejected()).thenReturn(contentEntity);
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.of(selectionPhase));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity, notificationService.rejectedNotification(selectionPhase));
    }

    @Test
    void qualifiedNotificationTest() {
        Mockito.when(contentRepository.findByQualified()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(selectionPhase.getSkillOwnerEntity().getSkillOwnerEntityId())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(selectionPhase.getJob().getJobId())).thenReturn(Optional.of(job));
        assertEquals(contentEntity.getTitle(), notificationService.qualifiedNotification(selectionPhase, requirementPhase).getTitle());
    }

    @Test
    void qualifiedNotificationToPartnerTest() {
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(selectionPhase.getSkillOwnerEntity().getSkillOwnerEntityId())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(selectionPhase.getJob().getJobId())).thenReturn(Optional.of(job));
        Mockito.when(contentRepository.findByQualified()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.qualifiedNotificationToPartner(selectionPhase, requirementPhase).getId());
    }

    @Test
    void shortlistBySeekerNotificationToPartnerTest() {
        Mockito.when(contentRepository.findByShortlist()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(selectionPhase.getJob().getJobId())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(selectionPhase.getSkillOwnerEntity().getSkillOwnerEntityId())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity, notificationService.shortlistBySeekerNotificationToPartner(selectionPhase));
    }

    @Test
    void shortlistBySeekerNotificationTest() {
        Mockito.when(contentRepository.findByShortlist()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntity));
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity, notificationService.shortlistBySeekerNotification(selectionPhase));
    }


    @Test
    void newSlotBySeekerNotificationTest() {
        Mockito.when(contentRepository.findByNewSlot()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(job.getJobId())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntity));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        assertEquals(contentEntity.getTitle(), notificationService.newSlotBySeekerNotification(job.getJobId(), skillOwnerEntity.getSkillOwnerEntityId(), newSlotRequestBySeekerDto).getTitle());
    }

    @Test
    void newSlotBySeekerNotificationTest1() {
        Mockito.when(contentRepository.findByCommonSlot()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(skillOwnerEntity));
        assertEquals(contentEntity.getTitle(), notificationService.newSlotBySeekerNotification(1).getTitle());

    }

    @Test
    void acceptBySkillOwnerNotificationToPartnerTest() {
        Mockito.when(contentRepository.findByAccept()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable(job));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(),Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        assertEquals(contentEntity.getId(), notificationService.acceptBySkillOwnerNotificationToPartner(acceptRejectDto).getId());
    }

    @Test
    void acceptInterviewBySkillOwnerNotificationTest() {
        Mockito.when(contentRepository.findByAccept()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(job.getJobId())).thenReturn(Optional.ofNullable(job));
        assertEquals(contentEntity.getId(), notificationService.acceptInterviewBySkillOwnerNotification(job.getJobId(), skillOwnerEntity.getSkillOwnerEntityId()).getId());
    }

    @Test
    void acceptBySkillOwnerNotificationTest() {
        Mockito.when(contentRepository.findByAccept()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable((job)));
        Mockito.when(contentRepository.findByAccept()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable(job));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(),Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        assertEquals(contentEntity.getId(), notificationService.acceptBySkillOwnerNotification(acceptRejectDto).getId());
    }


    @Test
    void slotConfirmedBySeekerNotificationTest() {
        Mockito.when(contentRepository.findByConfirmBySeeker()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(slotConfirmBySeekerDto.getJobId())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntity));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        assertEquals(contentEntity.getTitle(), notificationService.slotConfirmedBySeekerNotification(slotConfirmBySeekerDto).getTitle());
    }

    @Test
    void SlotBySkillOwnerNotificationTest() {
        Mockito.when(contentRepository.findByOwnerSlotId()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(slotConfirmBySeekerDto.getJobId())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        assertEquals(contentEntity.getId(), notificationService.slotBySkillOwnerNotification(slotConfirmByOwnerDto).getId());
    }

    @Test
    void scheduleInterviewNotificationToPartnerTest() {
        Mockito.when(contentRepository.findByScheduleInterview()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(scheduleInterviewDto.getJobId())).thenReturn(Optional.ofNullable(job));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(scheduleInterviewDto.getSkillOwnerId())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity, notificationService.scheduleInterviewNotificationToPartner(scheduleInterviewDto, notification));
    }

    @Test
    void scheduleInterviewNotificationTest() {
        Mockito.when(contentRepository.findByScheduleInterview()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntity));
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.of(job));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.of(selectionPhase));
        Mockito.when(requirementPhaseRepository.findByJobIdSkillOwnerIdAndStage(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(requirementPhase));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity, notificationService.scheduleInterviewNotification(scheduleInterviewDto, notification));
    }

    @Test
    void getPartnerNotificationTest() {
        Mockito.when(partnerNotificationsRepository.findBySkillPartnerEntityId(Mockito.anyInt())).thenReturn(Optional.ofNullable(partnerNotificationsEntitiesList));
        assertEquals(1, notificationService.getPartnerNotification(1).size());
    }

    @Test
    void getSeekerNotificationTest() {
        Mockito.when(skillSeekerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillSeekerEntity));
        Mockito.when(seekerNotificationsRepository.findByTaxIdBusinessLicense(Mockito.anyString())).thenReturn(Optional.of(seekerNotificationsEntities));
        Mockito.when(skillSeekerService.getAccessDetails(skillSeekerEntity.getId())).thenReturn(seekerModulesEntities);
        assertEquals(1, notificationService.getSeekerNotification(1).size());
    }

    @Test
    void getOwnerNotificationTest() {
        Mockito.when(ownerNotificationsRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(Optional.ofNullable(ownerNotificationsEntities));
        assertEquals(1, notificationService.getOwnerNotification(1).size());

    }


    @Test
    void MsaNotificationTest() {
        Mockito.when(skillSeekerRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(skillSeekerEntity));
        Mockito.when(skillOwnerRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(skillOwnerEntity));
        Mockito.when(contentRepository.findByInitiated()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.msaStatusNotification(skillSeekerMSAEntity,notification).getId());
    }


    @Test
    void updateSeekerInvoiceStatusNotificationTest() {
        Mockito.when((contentRepository.findByInvoiceSubmitted())).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoiceApproval()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoiceReject()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoicePending()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoicePaid()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.seekerInvoiceStatusNotification(invoiceAdmin, notification).getId());
    }

    @Test
    void updatePartnerInvoiceStatusNotificationTest() {
        Mockito.when(contentRepository.findByInvoiceSubmitted()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.partnerInvoiceStatusNotification(invoice, notification).getId());
    }

    @Test
    void partnerInvoiceStatusNotificationTest() {
        Mockito.when((contentRepository.findByInvoiceSubmitted())).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoiceApproval()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoiceReject()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoicePending()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByInvoicePaid()).thenReturn(contentEntity);

        assertEquals(contentEntity.getId(), notificationService.partnerInvoiceStatusNotification(invoice, notification).getId());

    }

    @Test
    void poStatusNotificationTest() {
        Mockito.when(contentRepository.findByInitiated()).thenReturn(contentEntity);
        Mockito.when(contentRepository.findByReleased()).thenReturn(contentEntity);
        assertEquals(contentEntity.getId(), notificationService.poStatusNotification(poEntity, notification).getId());
    }

//    @Test
//    void getContractNotificationsInSeekerTest(){
//        Mockito.when(poRepository.findByOwnerId(1)).thenReturn(Optional.of(poEntity));
//        Mockito.when(statementOfWorkRepository.findByOwnerId(1)).thenReturn(Optional.of(sowEntity));
//        Mockito.when(seekerMsaRepository.findByOwnerId(1)).thenReturn(skillSeekerMSA);
//        Mockito.when(contentRepository.findByPO()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findByMSA()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findBySow()).thenReturn(contentEntity);
//        assertEquals(contentEntity,notificationService.getContractNotificationsInSeeker(1));
//
//    }

//    @Test
//    void getContractNotificationsInPartnerTest(){
//        Mockito.when(skillOwnerRepository.findById(1)).thenReturn(Optional.of(skillOwnerEntity));
//        Mockito.when(poRepository.findByOwnerId(Mockito.anyInt())).thenReturn(Optional.of(poEntity));
//        Mockito.when(statementOfWorkRepository.findByOwnerId(Mockito.anyInt())).thenReturn(Optional.of(sowEntity));
//        Mockito.when(seekerMsaRepository.findByOwnerId(Mockito.anyInt())).thenReturn(skillSeekerMSA);
//        Mockito.when(contentRepository.findByPO()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findByMSA()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findBySow()).thenReturn(contentEntity);
//        assertEquals(contentEntity,notificationService.getContractNotificationsInPartner(1));
//
//
//    }

//    @Test
//    void getContractNotificationsInOwnerTest(){
//        Mockito.when(skillOwnerRepository.findById(1)).thenReturn(Optional.of(skillOwnerEntity));
//        Mockito.when(poRepository.findByOwnerId(1)).thenReturn(Optional.of(poEntity));
//        Mockito.when(statementOfWorkRepository.findByOwnerId(1)).thenReturn(Optional.of(sowEntity));
//        Mockito.when(seekerMsaRepository.findByOwnerId(1)).thenReturn(skillSeekerMSA);
//        Mockito.when(contentRepository.findByPO()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findByMSA()).thenReturn(contentEntity);
//        Mockito.when(contentRepository.findBySow()).thenReturn(contentEntity);
//        assertEquals(contentEntity.getId(),notificationService.getContractNotificationsInOwner(1).getId());
//    }

    @Test
    void autoScheduleInterviewNotification() {
        Mockito.when(contentRepository.findByAutoScheduleInterview()).thenReturn(contentEntity);
        Mockito.when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable(job));
        Mockito.when(selectionPhaseRepository.findByJobIdAndSkillOwnerId(Mockito.anyString(), Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhase));
        Mockito.when(requirementPhaseRepository.findByJobIdSkillOwnerIdAndStage(Mockito.anyString(),Mockito.anyInt(),Mockito.anyInt())).thenReturn(Optional.of(requirementPhase));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(contentRepository.findByScheduleInterview()).thenReturn(contentEntity);
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        assertEquals(contentEntity.getId(), notificationService.autoScheduleInterviewNotification(scheduleInterviewDto, notification).getId());
        
    }

}