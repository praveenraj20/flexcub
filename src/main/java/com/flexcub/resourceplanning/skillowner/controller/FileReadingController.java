package com.flexcub.resourceplanning.skillowner.controller;

import com.flexcub.resourceplanning.skillowner.dto.SkillOwnerFile;
import com.flexcub.resourceplanning.skillowner.service.SkillOwnerFileDataService;
import com.flexcub.resourceplanning.skillpartner.service.SkillPartnerFileDataService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(value = "/v1/file-reading")
@ApiResponses({@ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "400", description = "Bad Request")})
public class FileReadingController {
    Logger logger = LoggerFactory.getLogger(FileReadingController.class);
    @Autowired
    private SkillOwnerFileDataService fileReadingService;
    @Autowired
    private SkillPartnerFileDataService skillPartnerFileDataService;

    /**
     * @param
     * @return Response Entity
     * @throws IOException
     */
    @PostMapping(value = "/uploadExcel", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SkillOwnerFile> uploadExcel(@RequestParam("file") List<MultipartFile> excelDataFileList, @RequestParam("id") String id) {
        logger.info("FileReadingController|| uploadExcel || /upload Excel called");
        return new ResponseEntity<>(fileReadingService.checkFileTypeAndUpload(excelDataFileList, id), HttpStatus.OK);
    }

    /**
     * @param id
     * @return responseEntity
     * @throws IOException
     */
    //TODO : to be added in scheduler
    @GetMapping(path = "/syncSkillOwnerFile/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> setSkillOwnerDataInDb(@PathVariable int id) {
        logger.info("FileReadingController|| setDataInDb || /syncExcel called");
        try {
            return new ResponseEntity<>(fileReadingService.readExcelFile(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sync Failure");
        }
    }

    //TODO : to be added in scheduler
    @GetMapping(path = "/syncSkillPartnerFile/{id}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> setSkillPartnerDataInDb(@PathVariable int id) {
        logger.info("FileReadingController|| setDataInDb || /syncExcel called");
        try {
            return new ResponseEntity<>(skillPartnerFileDataService.readSkillPartnerExcelFile(id), HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Sync Failure");
        }
    }

    @GetMapping(value = "/downloadTemplate", produces = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        logger.info("FileReadingController|| downloadTemplate || /downloadTemplate Excel called");
        return fileReadingService.downloadTemplate();
    }
}