package com.flexcub.resourceplanning.skillseeker.service;


import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.job.entity.RequirementPhase;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.job.repository.JobRepository;
import com.flexcub.resourceplanning.job.repository.RequirementPhaseRepository;
import com.flexcub.resourceplanning.job.repository.SelectionPhaseRepository;
import com.flexcub.resourceplanning.job.service.impl.SelectionPhaseServiceImpl;
import com.flexcub.resourceplanning.notifications.service.NotificationService;
import com.flexcub.resourceplanning.skillowner.dto.OwnerSkillRoles;
import com.flexcub.resourceplanning.skillowner.entity.FileDB;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillDomainEntity;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillStatusEntity;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillDomainRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerRepository;
import com.flexcub.resourceplanning.skillseeker.dto.PurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.dto.SeekerPurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.dto.SowStatusDto;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWorkGetDetails;
import com.flexcub.resourceplanning.skillseeker.entity.*;
import com.flexcub.resourceplanning.skillseeker.repository.*;
import com.flexcub.resourceplanning.skillseeker.service.impl.PoServiceImpl;
import com.flexcub.resourceplanning.skillseeker.service.impl.StatementOfWorkServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = StatementOfWorkServiceImpl.class)
class StatementOfWorkServiceTest {

    @Autowired
    StatementOfWorkServiceImpl statementOfWorkService;

    @MockBean
    StatementOfWorkRepository statementOfWorkRepository;

    @MockBean
    SelectionPhaseServiceImpl selectionPhaseService;

    @MockBean
    SkillSeekerProjectRepository skillSeekerProjectRepository;

    @MockBean
    SkillOwnerRepository skillOwnerRepository;

    @MockBean
    RequirementPhaseRepository requirementPhaseRepository;

    @MockBean
    SelectionPhaseRepository selectionPhaseRepository;

    @MockBean
    PoServiceImpl poService;

    @MockBean
    OwnerSkillDomainRepository ownerSkillDomainRepository;

    @MockBean
    private NotificationService notificationService;
    @MockBean
    PoRepository poRepository;
    @MockBean
    ContractStatusRepository contractStatusRepository;
    @MockBean
    ModelMapper modelMapper;

    @MockBean
    JobRepository jobRepository;

   @MockBean
    private TemplateRepository templateRepository;

    StatementOfWorkEntity statementOfWorkEntity = new StatementOfWorkEntity();
    List<StatementOfWorkGetDetails> statementOfWorkGetDetailsList = new ArrayList<>();
    StatementOfWorkGetDetails statementOfWorkGetDetails = new StatementOfWorkGetDetails();
    List<StatementOfWorkEntity> statementOfWorkEntities = new ArrayList<>();
    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();
    SkillSeekerProjectEntity skillSeekerProject = new SkillSeekerProjectEntity();
    OwnerSkillDomainEntity ownerSkillDomainEntity = new OwnerSkillDomainEntity();
    SkillSeekerEntity skillSeekerEntity = new SkillSeekerEntity();
    SkillSeekerProjectEntity skillSeekerProjectEntity = new SkillSeekerProjectEntity();
    OwnerSkillRoles ownerSkillRoles = new OwnerSkillRoles();
    PurchaseOrder purchaseOrder = new PurchaseOrder();
    Job job = new Job();
    FileDB fileDB = new FileDB();

    List<MultipartFile> file = new ArrayList<>();

    List<RequirementPhase> requirementPhaseList = new ArrayList<>();
    RequirementPhase requirementPhase = new RequirementPhase();

    SelectionPhase selectionPhase = new SelectionPhase();

    List<SelectionPhase> selectionPhases = new ArrayList<>();

    OwnerSkillStatusEntity ownerSkillStatus = new OwnerSkillStatusEntity();

    SowStatusDto sowStatusDto = new SowStatusDto();
    SeekerPurchaseOrder seekerProductOwner = new SeekerPurchaseOrder();

    List<SeekerPurchaseOrder> seekerProductOwners = new ArrayList<>();

     TemplateTable templateTable = new TemplateTable();

    byte[] bytes = {1, 2, 3};
    Optional<ContractStatus> sowStatus = Optional.of(new ContractStatus());


    @BeforeEach
    void setup() {
        statementOfWorkEntity.setSkillSeekerProject(skillSeekerProject);
        statementOfWorkEntity.setSkillOwnerEntity(skillOwnerEntity);
        statementOfWorkEntity.setMimeType("APPLICATION/PDF");
        statementOfWorkEntity.setName("application.pdf");
        statementOfWorkEntity.setSize(244);
        statementOfWorkEntity.setId(1);
        statementOfWorkEntity.setSowStatus(sowStatus.get());
        statementOfWorkEntity.setId(1);
        statementOfWorkEntity.setDateOfRelease(new Date(2022 - 12 - 23));
        statementOfWorkEntity.setSowCreated(true);
        statementOfWorkEntity.setOwnerSkillDomainEntity(ownerSkillDomainEntity);
        statementOfWorkEntity.setSkillOwnerEntity(skillOwnerEntity);
        statementOfWorkEntity.setJobId(job);
        statementOfWorkEntity.setData(bytes);
        statementOfWorkEntity.setSkillSeekerId(1);

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
        purchaseOrder.setId(1);

        ownerSkillDomainEntity.setDomainId(1);
        skillSeekerEntity.setId(1);
        skillSeekerProjectEntity.setId(1);
        ownerSkillRoles.setRolesDescription("Developer");

        requirementPhase.setJobId(job.getJobId());
        requirementPhaseList.add(requirementPhase);

        selectionPhase.setJob(job);
        selectionPhases.add(selectionPhase);
        ownerSkillStatus.setSkillOwnerStatusId(4);
        ownerSkillStatus.setStatusDescription("InHiring");

        seekerProductOwner.setId(statementOfWorkEntity.getId());
        seekerProductOwner.setSkillSeekerProjectName("Scala And Kafka");

        statementOfWorkEntities.add(statementOfWorkEntity);
        seekerProductOwners.add(seekerProductOwner);

        statementOfWorkGetDetails.setId(1);
        statementOfWorkGetDetails.setJobId("FJB-00001");
        statementOfWorkGetDetails.setDepartment("it");
        statementOfWorkGetDetails.setRole("developer");
        statementOfWorkGetDetails.setOwnerId(1);
        statementOfWorkGetDetails.setStatus("sow in process");
        statementOfWorkGetDetails.setPhone("1234567899");
        statementOfWorkGetDetails.setEmail("abcd@qbrainx.com");
        statementOfWorkGetDetails.setProject("abc");
        statementOfWorkGetDetails.setSkillOwnerName("abc");
        statementOfWorkGetDetailsList.add(statementOfWorkGetDetails);


        sowStatusDto.setSowStatusId(1);
        sowStatusDto.setSowId(1);
        templateTable.setId(1l);
        templateTable.setSize(4l);
        templateTable.setTemplateName("vendor");
        templateTable.setTemplateType("vendor/pdf");
        templateTable.setData(new byte[3]);
    }

    @Test
    void addDocumentTest() {
        Mockito.when(statementOfWorkRepository.findByAllField(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString())).thenReturn(Optional.ofNullable(statementOfWorkEntity));
        Mockito.when(skillOwnerRepository.findBySkillOwnerEntityId(Mockito.anyInt())).thenReturn(skillOwnerEntity);
        Mockito.when(statementOfWorkRepository.save(statementOfWorkEntity)).thenReturn(statementOfWorkEntity);
        Mockito.when(statementOfWorkRepository.save(statementOfWorkEntity)).thenReturn(statementOfWorkEntity);
        Mockito.when(jobRepository.findByJobJobId(job.getJobId())).thenReturn(job);
        Mockito.when(requirementPhaseRepository.findBySkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId())).thenReturn(Optional.of(requirementPhaseList));
        Mockito.when(selectionPhaseRepository.findBySkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId())).thenReturn(Optional.of(selectionPhases));
        assertEquals(statementOfWorkEntity.getId(), statementOfWorkService.addDocument(file, skillOwnerEntity.getSkillOwnerEntityId(), skillSeekerEntity.getId(), ownerSkillDomainEntity.getDomainId(), ownerSkillRoles.getRolesDescription(), skillSeekerProjectEntity.getId(), job.getJobId()).getId());
    }

    @Test
    void getSowDetailsTest() {
        Mockito.when(statementOfWorkRepository.findBySkillSeekerId(skillSeekerEntity.getId())).thenReturn(Optional.ofNullable(statementOfWorkEntities));
        assertEquals(1, statementOfWorkService.getSowDetails(skillSeekerEntity.getId()).size());

    }

    @Test
    void getAllSowDetailsTest() {
        Mockito.when(statementOfWorkRepository.findAll()).thenReturn(statementOfWorkEntities);
        assertEquals(1, statementOfWorkService.getAllSowDetails().size());
        assertEquals(statementOfWorkGetDetails.getId(), statementOfWorkService.getAllSowDetails().size());
    }

    @Test
    void updateStatusTest() {
        Mockito.when(statementOfWorkRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(statementOfWorkEntity));
        Mockito.when(contractStatusRepository.findById(Mockito.anyInt())).thenReturn(sowStatus);
        assertEquals(1, statementOfWorkService.updateSowStatus(1, 1).getSowId());

    }

    @Test
    void downloadAgreementSOWTest() {
        Mockito.when(statementOfWorkRepository.findById(statementOfWorkEntity.getId())).thenReturn(Optional.ofNullable(statementOfWorkEntity));
        assertEquals(statementOfWorkEntity.getJobId(), statementOfWorkService.downloadAgreementSOW(statementOfWorkEntity.getId()).getJobId());
    }

    @Test
    void templateDownloadTest() {
        Mockito.when(templateRepository.findBySOWFile()).thenReturn(Optional.of(templateTable));
        assertEquals(200, statementOfWorkService.templateDownload().getStatusCodeValue());

    }
}