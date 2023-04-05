package com.flexcub.resourceplanning.skillseeker.controller;

import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.skillowner.dto.FileResponse;
import com.flexcub.resourceplanning.skillseeker.dto.SowStatusDto;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWork;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWorkGetDetails;
import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;
import com.flexcub.resourceplanning.skillseeker.service.StatementOfWorkSerivce;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.INVALID_SOW_ID;

@RestController
@RequestMapping(value = "/v1/statementOfWorkController")
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "400", description = "Bad Request"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "500", description = "Server Error")})
public class StatementOfWorkController {

    Logger logger = LoggerFactory.getLogger(StatementOfWorkController.class);
    @Autowired
    private StatementOfWorkSerivce statementOfWorkSerivce;
    @Value("${flexcub.downloadURLSOW}")
    private String downloadURLSOW;


    @PostMapping(value = "/uploadSOW", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StatementOfWork> uploadFile(@RequestParam(value = "document") List<MultipartFile> multipartFile,
                                                      @RequestParam("ownerId") int ownerId,
                                                      @RequestParam("skillSeekerId") int seekerId,
                                                      @RequestParam("domainId") int domainId,
                                                      @RequestParam("roles") String role,
                                                      @RequestParam("skillSeekerProjectId") int projectID,
                                                      @RequestParam("jobId") String jobId) throws Exception {
        logger.info("StatementOfWorkController|| uploadFile || upload File called");
        return new ResponseEntity<>(statementOfWorkSerivce.addDocument(multipartFile, ownerId, seekerId, domainId, role, projectID, jobId), HttpStatus.OK);
    }

    @PutMapping(value = "/uploadSOW", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<StatementOfWork> updateFile(@RequestParam(value = "document") List<MultipartFile> multipartFile,
                                                      @RequestParam("ownerId") int ownerId,
                                                      @RequestParam("skillSeekerId") int seekerId,
                                                      @RequestParam("domainId") int domainId,
                                                      @RequestParam("roles") String role,
                                                      @RequestParam("skillSeekerProjectId") int projectID,
                                                      @RequestParam("jobId") String jobId) throws Exception {
        logger.info("StatementOfWorkController|| updateFile || updating The File");
        return new ResponseEntity<>(statementOfWorkSerivce.addDocument(multipartFile, ownerId, seekerId, domainId, role, projectID, jobId), HttpStatus.OK);
    }

    @GetMapping(value = "/getSowDetails")
    public ResponseEntity<List<StatementOfWorkGetDetails>> getSowDetails(@RequestParam int skillSeekerId) {
        logger.info("StatementOfWorkController|| getSowDetails || getSowDetails");
        return new ResponseEntity<>(statementOfWorkSerivce.getSowDetails(skillSeekerId), HttpStatus.OK);
    }

    @GetMapping(value = "/getAllSowDetails", produces = {"application/json"})
    public ResponseEntity<List<StatementOfWorkGetDetails>> getAllSowDetails() {
        logger.info("StatementOfWorkController|| getAllSowDetails || get AllSowDetails");
        return new ResponseEntity<>(statementOfWorkSerivce.getAllSowDetails(), HttpStatus.OK);
    }

    @PutMapping(value = "/updateSowStatus", produces = {"application/json"})
    public ResponseEntity<SowStatusDto> updateSowStatus(@RequestParam int sowId, @RequestParam int sowStatusId) {
        logger.info("StatementOfWorkController|| updateSowStatus || updateSowStatus called");
        return new ResponseEntity<>(statementOfWorkSerivce.updateSowStatus(sowId, sowStatusId), HttpStatus.OK);
    }

    @GetMapping(value = "/downloadOwnerAgreementForSow", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> downloadAgreementSow(@RequestParam int id) {
        Optional<StatementOfWorkEntity> statementOfWorkEntity = Optional.of(this.statementOfWorkSerivce.downloadAgreementSOW(id));
        if (statementOfWorkEntity.isPresent()) {
            String file = String.valueOf(statementOfWorkEntity.get().getName());
            file = file.substring(file.indexOf("."));
            logger.info("StatementOfWorkController|| downloadAgreementSow || downloadAgreementSow called");
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(statementOfWorkEntity.get().getMimeType()))
                    .header("Content-disposition", "attachment; filename=" + statementOfWorkEntity.get().getSkillOwnerEntity().getFirstName() + "_SOW" + file)
                    .body(new ByteArrayResource(statementOfWorkEntity.get().getData()));
        } else {
            throw new ServiceException(INVALID_SOW_ID.getErrorCode(), INVALID_SOW_ID.getErrorDesc());
        }
    }

    @GetMapping(value = "/downloadAgreementForSow", produces = {"application/json"})
    public FileResponse downloadOwnerAgreement(int id) throws FileNotFoundException {
        StatementOfWorkEntity sow = statementOfWorkSerivce.getSow(id);
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().
                fromUriString(downloadURLSOW + id).
                toUriString();
        String file = String.valueOf(sow.getName());
        file = file.substring(file.indexOf("."));
        logger.info("StatementOfWorkController|| downloadAgreementForSow || /downloadAgreementForSow response URL for download");
        return new FileResponse(sow.getSkillOwnerEntity().getFirstName() + "_SOW" + file, fileDownloadUri, sow.getMimeType(), sow.getSize(), HttpStatus.OK);
    }

    @GetMapping(value = "/getSowTemplate", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> getSowTemplate() throws IOException {
        logger.info("StatementOfWorkController|| getSowTemplate || download SOW Template");
        return statementOfWorkSerivce.templateDownload();
    }

}
