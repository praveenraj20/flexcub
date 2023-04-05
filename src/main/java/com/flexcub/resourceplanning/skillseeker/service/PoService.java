package com.flexcub.resourceplanning.skillseeker.service;

import com.flexcub.resourceplanning.skillseeker.dto.PurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.dto.SeekerPurchaseOrder;
import com.flexcub.resourceplanning.skillseeker.entity.PoEntity;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PoService {

    PurchaseOrder addData(List<MultipartFile> multipartFiles, String role, int domainId, int ownerId, int seekerId, int projectID, String jobId);

//    PurchaseOrder updateStatus(int id, String status);

    PurchaseOrder updateStatus(int poId, int statusId);

    Optional<PoEntity> downloadAgreement(int id) throws IOException;

    List<SeekerPurchaseOrder> getPurchaseOrderDetails(int seekerId);

    List<SeekerPurchaseOrder> getAllPurchaseOrderDetails();

    ResponseEntity<Resource> getPurchaseOrderTemplateDownload() throws IOException;

    @Transactional
    PoEntity getPo(int id) throws FileNotFoundException;
}
