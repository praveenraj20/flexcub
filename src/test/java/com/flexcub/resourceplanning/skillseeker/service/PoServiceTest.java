package com.flexcub.resourceplanning.skillseeker.service;

import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.job.entity.RequirementPhase;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.job.repository.JobRepository;
import com.flexcub.resourceplanning.job.repository.RequirementPhaseRepository;
import com.flexcub.resourceplanning.job.repository.SelectionPhaseRepository;
import com.flexcub.resourceplanning.job.service.impl.SelectionPhaseServiceImpl;
import com.flexcub.resourceplanning.notifications.service.impl.NotificationServiceImpl;
import com.flexcub.resourceplanning.skillowner.dto.OwnerSkillRoles;
import com.flexcub.resourceplanning.skillowner.entity.*;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillDomainRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerRepository;
import com.flexcub.resourceplanning.skillseeker.dto.PurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.dto.SeekerPurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.entity.*;
import com.flexcub.resourceplanning.skillseeker.repository.*;
import com.flexcub.resourceplanning.skillseeker.service.impl.PoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = PoServiceImpl.class)
class PoServiceTest {

    @Autowired
    PoServiceImpl poService;

 @MockBean
    TemplateRepository templateRepository;

    @MockBean
    PoRepository poRepository;

    @MockBean
    SelectionPhaseServiceImpl selectionPhaseService;

    @MockBean
    RequirementPhaseRepository requirementPhaseRepository;

    @MockBean
    StatementOfWorkRepository statementOfWorkRepository;

    @MockBean
    SelectionPhaseRepository selectionPhaseRepository;

    @MockBean
    JobRepository jobRepository;

    @MockBean
    SkillSeekerProjectRepository skillSeekerProjectRepository;

    @MockBean
    SkillOwnerRepository skillOwnerRepository;

    @MockBean
    OwnerSkillDomainRepository ownerSkillDomainRepository;
    @MockBean
    ContractStatusRepository contractStatusRepository;
    @MockBean
    NotificationServiceImpl notificationService;


    PoEntity poEntity = new PoEntity();

    List<PoEntity> poEntities = new ArrayList<>();

    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();

    SkillSeekerProjectEntity skillSeekerProject = new SkillSeekerProjectEntity();

    OwnerSkillDomainEntity ownerSkillDomainEntity = new OwnerSkillDomainEntity();

    SkillSeekerEntity skillSeekerEntity = new SkillSeekerEntity();

    SkillSeekerProjectEntity skillSeekerProjectEntity = new SkillSeekerProjectEntity();

    OwnerSkillRoles ownerSkillRoles = new OwnerSkillRoles();

    PurchaseOrder purchaseOrder = new PurchaseOrder();

    StatementOfWorkEntity statementOfWorkEntity = new StatementOfWorkEntity();

    Job job = new Job();

    FileDB fileDB = new FileDB();

    List<MultipartFile> file = new ArrayList<>();

    List<RequirementPhase> requirementPhaseList = new ArrayList<>();

    RequirementPhase requirementPhase = new RequirementPhase();

    SelectionPhase selectionPhase = new SelectionPhase();

    List<SelectionPhase> selectionPhases = new ArrayList<>();

    OwnerSkillStatusEntity ownerSkillStatus = new OwnerSkillStatusEntity();

    SeekerPurchaseOrder seekerProductOwner = new SeekerPurchaseOrder();

    List<SeekerPurchaseOrder> seekerProductOwners = new ArrayList<>();

    byte[] bytes = {(byte) 32223};
    ContractStatus poStatus =new ContractStatus();

    OwnerSkillStatusEntity ownerSkillStatusEntity = new OwnerSkillStatusEntity();
    List<SkillOwnerPortfolio> skillOwnerPortfolios = new ArrayList<>();
    TemplateTable templateTable = new TemplateTable();

    @BeforeEach
    void setup() {

        poStatus.setId(1);
        poStatus.setStatus("Active");

        ownerSkillStatusEntity.setSkillOwnerStatusId(1);
        skillOwnerEntity.setSkillOwnerEntityId(1);
        skillOwnerEntity.setAddress("Salem");
        skillOwnerEntity.setCity("Salem");
        skillOwnerEntity.setDOB(new Date(1998 - 01 - 23));
        skillOwnerEntity.setFirstName("Ajith");
        skillOwnerEntity.setLastName("Kumar");
        skillOwnerEntity.setPhoneNumber("9087654321");
        skillOwnerEntity.setLinkedIn("linkdn");
        skillOwnerEntity.setPrimaryEmail("ajithashok2530@gmail.com");
        skillOwnerEntity.setRateCard(67);
        skillOwnerEntity.setState("Tamilnadu");
        skillOwnerEntity.setAccountStatus(true);
        skillOwnerEntity.setPortfolioUrl(skillOwnerPortfolios);
        skillOwnerEntity.setOwnerSkillStatusEntity(ownerSkillStatusEntity);

        skillSeekerProject.setId(1);
        ownerSkillDomainEntity.setDomainId(1);

        poEntity.setSkillSeekerProject(skillSeekerProject);
        poEntity.setSkillOwnerEntity(skillOwnerEntity);
        poEntity.setMimeType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        poEntity.setName("PO__Qbrainx_Inc__Draft.docx");
        poEntity.setSize(101523);
        poEntity.setId(1);
        poEntity.setSkillSeekerId(1);
        poEntity.setOwnerSkillDomainEntity(ownerSkillDomainEntity);
        poEntity.setJobId(job);
        poEntity.setData(bytes);
        poEntity.setPoStatus(poStatus);
        poEntity.setRole("developer");
        poEntity.setDateOfRelease(new Date(2022 - 12 - 23));
        poEntity.setExpiryDate(new Date(2023 - 12 - 30));


        purchaseOrder.setId(poEntity.getId());
        purchaseOrder.setStatus(poEntity.getPoStatus().getStatus());


        skillOwnerEntity.setSkillOwnerEntityId(1);


        job.setJobId("FJB-00001");
        job.setNumberOfPositions(5);


        fileDB.setId(1);
        fileDB.setName("application.pdf");
        fileDB.setType("application/pdf");
        fileDB.setData(new byte[1]);
        fileDB.setSynced(false);

        MultipartFile multipartFile = new MockMultipartFile(fileDB.getName(), fileDB.getName(), "application/pdf", fileDB.getName().getBytes());
        file.add(multipartFile);

        ownerSkillDomainEntity.setDomainId(1);
        skillSeekerEntity.setId(1);
        skillSeekerProjectEntity.setId(1);
        ownerSkillRoles.setRolesDescription("Developer");

        requirementPhase.setSkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId());
        requirementPhase.setJobId(job.getJobId());
        requirementPhaseList.add(requirementPhase);

        selectionPhase.setSkillOwnerEntity(skillOwnerEntity);
        selectionPhase.setJob(job);
        selectionPhases.add(selectionPhase);
        ownerSkillStatus.setSkillOwnerStatusId(4);
        ownerSkillStatus.setStatusDescription("InHiring");

        seekerProductOwner.setId(poEntity.getId());
        seekerProductOwner.setSkillSeekerProjectName("Scala And Kafka");

        poEntities.add(poEntity);
        seekerProductOwners.add(seekerProductOwner);

        templateTable.setId(1l);
        templateTable.setSize(4l);
        templateTable.setTemplateName("vendor.pdf");
        templateTable.setTemplateType("vendor/pdf");
        templateTable.setData(new byte[3]);

    }

    @Test
    void addDataTest() {
        when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        when(jobRepository.findByJobId(Mockito.anyString())).thenReturn(Optional.ofNullable(job));
        when(contractStatusRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(poStatus));
        when(poRepository.save(Mockito.any())).thenReturn(poEntity);
        when(jobRepository.findByJobJobId(Mockito.anyString())).thenReturn(job);
        when(poRepository.save(Mockito.any())).thenReturn(poEntity);
        when(requirementPhaseRepository.findBySkillOwnerId(Mockito.anyInt())).thenReturn(Optional.ofNullable(requirementPhaseList));
        when(selectionPhaseRepository.findBySkillOwnerId(Mockito.anyInt())).thenReturn(Optional.ofNullable(selectionPhases));
        assertEquals(poEntity.getId(), poService.addData(file, ownerSkillRoles.rolesDescription, ownerSkillDomainEntity.getDomainId(), skillOwnerEntity.getSkillOwnerEntityId(), skillSeekerEntity.getId(), skillSeekerProject.getId(), job.getJobId()).getId());
    }
    @Test
    void updateStatusTest() {
        when(poRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(poEntity));
        when(contractStatusRepository.findById(Mockito.anyInt())).thenReturn(Optional.ofNullable(poStatus));
        assertEquals(purchaseOrder.getId(), poService.updateStatus(1, 1).getId());

    }

    @Test
    void downloadAgreementTest() {
        when(poRepository.findById(poEntity.getId())).thenReturn(Optional.of(poEntity));
        assertEquals(poEntity.getId(), poService.downloadAgreement(poEntity.getId()).get().getId());
    }

    @Test
    void getPurchaseOrderDetailsTest() {
        when(poRepository.findBySkillSeekerId(skillSeekerEntity.getId())).thenReturn(Optional.of(poEntities));
        assertEquals(1, poService.getPurchaseOrderDetails(poEntity.getId()).size());
    }

    @Test
    void getAllPurchaseOrderDetailsTest() {
        when(poRepository.findAll()).thenReturn(poEntities);
        assertEquals(1, poService.getAllPurchaseOrderDetails().size());
    }

    @Test
    void getPurchaseOrderTemplateDownloadTest() {
        when(templateRepository.findByPOFile()).thenReturn(Optional.of(templateTable));
        assertEquals(200, poService.getPurchaseOrderTemplateDownload().getStatusCodeValue());
    }

}
