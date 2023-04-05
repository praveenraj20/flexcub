package com.flexcub.resourceplanning.skillseeker.controller;

import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.skillseeker.dto.SowStatusDto;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWork;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWorkGetDetails;
import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerEntity;
import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerProjectEntity;
import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;
import com.flexcub.resourceplanning.skillseeker.repository.StatementOfWorkRepository;
import com.flexcub.resourceplanning.skillseeker.service.StatementOfWorkSerivce;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = StatementOfWorkController.class)
 class StatementOfWorkControllerTest {

    @Autowired
    StatementOfWorkController statementOfWorkController;

    @MockBean
    StatementOfWorkSerivce statementOfWorkSerivce;

    @MockBean
    StatementOfWorkRepository statementOfWorkRepository;

    StatementOfWork statementOfWork = new StatementOfWork();

    StatementOfWorkEntity statementOfWorkEntity = new StatementOfWorkEntity();

    SkillSeekerEntity skillSeekerEntity = new SkillSeekerEntity();

    StatementOfWorkGetDetails statementOfWorkGetDetails = new StatementOfWorkGetDetails();

    SowStatusDto sowStatusDto = new SowStatusDto();
    List<StatementOfWorkGetDetails> statementOfWorkGetDetailsList = new ArrayList<>();

    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();

    SkillSeekerProjectEntity skillSeekerProjectEntity = new SkillSeekerProjectEntity();


    @BeforeEach
    void setup() {
        skillSeekerEntity.setId(1);
        statementOfWorkGetDetails.setId(1);

        statementOfWorkEntity.setSkillOwnerEntity(skillOwnerEntity);
        statementOfWorkEntity.setSkillSeekerProject(skillSeekerProjectEntity);
        statementOfWorkEntity.setId(1);
        statementOfWorkEntity.setMimeType("APPLICATION/PDF");
        statementOfWorkEntity.setName("application.pdf");
        byte[] a = {1, 2, 3};
        statementOfWorkEntity.setData(a);

        sowStatusDto.setSowId(1);
        sowStatusDto.setSowStatusId(1);
    }

    @Test
    void uploadFileTest() throws Exception {
        Mockito.when(statementOfWorkSerivce.addDocument(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(statementOfWork);
        assertEquals(200, statementOfWorkController.uploadFile(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()).getStatusCodeValue());
    }

    @Test
    void updateFileTest() throws Exception {
        Mockito.when(statementOfWorkSerivce.addDocument(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString())).thenReturn(statementOfWork);
        assertEquals(200, statementOfWorkController.updateFile(Mockito.anyList(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString()).getStatusCodeValue());
    }

    @Test
    void getSowDetailsTest() {
        Mockito.when(statementOfWorkSerivce.getSowDetails(skillSeekerEntity.getId())).thenReturn(statementOfWorkGetDetailsList);
        assertEquals(200, statementOfWorkController.getSowDetails(skillSeekerEntity.getId()).getStatusCodeValue());
    }

    @Test
    void getAllSowDetailsTest() {
        Mockito.when(statementOfWorkSerivce.getAllSowDetails()).thenReturn(statementOfWorkGetDetailsList);
        assertEquals(200, statementOfWorkController.getAllSowDetails().getStatusCodeValue());
    }

    @Test
    void updateSowStatusTest() {
        Mockito.when(statementOfWorkSerivce.updateSowStatus(Mockito.anyInt(),Mockito.anyInt())).thenReturn(sowStatusDto);
        assertEquals(200, statementOfWorkController.updateSowStatus(Mockito.anyInt(), Mockito.anyInt()).getStatusCodeValue());
    }

    @Test
    void downloadAgreementSowTest() {
        Mockito.when(statementOfWorkSerivce.downloadAgreementSOW(1)).thenReturn(statementOfWorkEntity);
        Mockito.when(statementOfWorkRepository.findById(1)).thenReturn(Optional.of(statementOfWorkEntity));
        assertEquals(200, statementOfWorkController.downloadAgreementSow(1).getStatusCodeValue());
    }

    @Test
    void getSowTemplateTest() throws IOException {
        ResponseEntity<Resource> resourceResponseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(statementOfWorkSerivce.templateDownload()).thenReturn(resourceResponseEntity);
        assertEquals(200, statementOfWorkController.getSowTemplate().getStatusCodeValue());
    }
}
