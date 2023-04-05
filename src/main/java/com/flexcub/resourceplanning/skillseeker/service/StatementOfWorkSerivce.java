package com.flexcub.resourceplanning.skillseeker.service;

import com.flexcub.resourceplanning.skillseeker.dto.SowStatusDto;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWork;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWorkGetDetails;
import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface StatementOfWorkSerivce {

    StatementOfWork addDocument(List<MultipartFile> multipartFile, int ownerId, int seekerId, int domainId, String role, int projectID, String jobId);

    List<StatementOfWorkGetDetails> getSowDetails(int skillSeekerId);

    List<StatementOfWorkGetDetails> getAllSowDetails();

    SowStatusDto updateSowStatus(int id, int sowStatusId);

    StatementOfWorkEntity downloadAgreementSOW(int id);

    ResponseEntity<Resource> templateDownload() throws IOException;

    @Transactional
    StatementOfWorkEntity getSow(int id);
}
