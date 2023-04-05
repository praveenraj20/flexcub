package com.flexcub.resourceplanning.skillowner.controller;

import com.flexcub.resourceplanning.skillowner.dto.SkillOwnerFile;
import com.flexcub.resourceplanning.skillowner.service.SkillOwnerFileDataService;
import com.flexcub.resourceplanning.skillpartner.service.SkillPartnerFileDataService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = FileReadingController.class)
class FileReadingControllerTest {

    List<MultipartFile> file;
    ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
    @MockBean
    private SkillOwnerFileDataService fileReadingService;
    @MockBean
    private SkillPartnerFileDataService skillPartnerFileDataService;
    @Autowired
    private FileReadingController fileReadingController;

    SkillOwnerFile skillOwnerFile=new SkillOwnerFile();

    @Test
    void uploadExcelTest() {
        Mockito.when(fileReadingService.checkFileTypeAndUpload(Mockito.any(), Mockito.anyString())).thenReturn(skillOwnerFile);
        assertEquals(200, fileReadingController.uploadExcel(file, "1").getStatusCodeValue());
    }


    @Test
    void setSkillOwnerDataInDbTest() {
        Mockito.when(fileReadingService.readExcelFile(1)).thenReturn("File synced successfully");
        assertEquals(200, fileReadingController.setSkillOwnerDataInDb(1).getStatusCodeValue());

    }

    @Test
    void setSkillPartnerDataInDbTest() {
        Mockito.when(skillPartnerFileDataService.readSkillPartnerExcelFile(1)).thenReturn("File synced successfully");
        assertThat(fileReadingController.setSkillPartnerDataInDb(1).getStatusCodeValue()).isEqualTo(200);
    }

    @Test
    void downloadTemplateTest() throws IOException {
        ResponseEntity<Resource> resourceResponseEntity = new ResponseEntity<>(HttpStatus.OK);
        Mockito.when(fileReadingService.downloadTemplate()).thenReturn(resourceResponseEntity);
        assertThat(fileReadingController.downloadTemplate().getStatusCodeValue()).isEqualTo(200);
    }
}
