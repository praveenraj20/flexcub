package com.flexcub.resourceplanning.skillowner.service;

import com.flexcub.resourceplanning.skillowner.dto.SkillOwnerFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface SkillOwnerFileDataService {
    SkillOwnerFile checkFileTypeAndUpload(List<MultipartFile> excelDataFileList, String id);

    ResponseEntity<Resource> downloadTemplate() throws IOException;

    String readExcelFile(int id);

}
