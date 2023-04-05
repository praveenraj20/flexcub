package com.flexcub.resourceplanning.skillpartner.service.impl;

import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.registration.entity.RegistrationEntity;
import com.flexcub.resourceplanning.registration.entity.Roles;
import com.flexcub.resourceplanning.registration.service.RegistrationService;
import com.flexcub.resourceplanning.skillowner.entity.FileDB;
import com.flexcub.resourceplanning.skillowner.repository.ExcelFileRepository;
import com.flexcub.resourceplanning.skillpartner.service.SkillPartnerFileDataService;
import com.flexcub.resourceplanning.skillpartner.service.SkillPartnerService;
import lombok.extern.log4j.Log4j2;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;

import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.*;

@Service
@Log4j2
public class SkillPartnerFileDataImpl implements SkillPartnerFileDataService {

    Logger logger = LoggerFactory.getLogger(SkillPartnerFileDataImpl.class);

    @Autowired
    ExcelFileRepository excelFileRepository;

    @Autowired
    RegistrationService registrationService;

    @Autowired
    SkillPartnerService skillPartnerService;

    /**
     * @param id
     * @return
     */
    @Transactional
    public String readSkillPartnerExcelFile(int id) {
        //Creating map of filetype to determine how to read file
        HashMap<String, String> fileTypeList = new HashMap<>();
        fileTypeList.put(".xls", "application/vnd.ms-excel");
        fileTypeList.put(".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        try {
            logger.info("SkillPartnerFileDataImpl || readExcelFile || Calling repo to check if file exists");
            Optional<FileDB> fileDb = excelFileRepository.findById(id);

            if (fileDb.isPresent() && !(fileDb.get().isSynced())) {
                InputStream excelInputStream = new ByteArrayInputStream(fileDb.get().getData());
                String fileName = fileDb.get().getName();
                if (fileDb.get().getType().equalsIgnoreCase(fileTypeList.get(".xls")) || fileDb.get().getType().equalsIgnoreCase(fileTypeList.get(".xlsx"))) {
                    importExcelFile(excelInputStream, fileName);
                    logger.info("SkillPartnerFileDataImpl || readExcelFile || File synced successfully ");
                    return "File synced successfully";
                } else {
                    logger.info("SkillPartnerFileDataImpl || readExcelFile || Wrong File Format ");
                    throw new ServiceException(WRONG_FILE_FORMAT.getErrorCode(), WRONG_FILE_FORMAT.getErrorDesc());
                }
            } else {
                logger.info("SkillPartnerFileDataImpl || readExcelFile || File not Found ");
                throw new ServiceException(FILE_NOT_FOUND.getErrorCode(), FILE_NOT_FOUND.getErrorDesc());
            }
        } catch (Exception e) {
            logger.info("SkillPartnerFileDataImpl || readExcelFile || File synced failure");
            throw new ServiceException(FILE_NOT_SYNCED.getErrorCode(), FILE_NOT_SYNCED.getErrorDesc());
        }
    }

    /**
     * @param excel
     * @return
     * @throws IOException
     */
    @Override
    public String importExcelFile(InputStream excel, String fileName) throws IOException {
        logger.info("SkillPartnerFileDataImpl || importExcelFile || Method start");
        XSSFWorkbook workbook = new XSSFWorkbook(excel);
        XSSFSheet worksheet = workbook.getSheetAt(1);
        Row row = worksheet.getRow(0);
        ArrayList<String> headerData = new ArrayList<>();

        //Storing header data to verify template
        for (Cell cell : row) {
            headerData.add(String.valueOf(cell));
        }
        String[] stringArray = headerData.toArray(new String[0]);
        String[] expectedHeaders = {"Skill Partner ID", "Business Name", "Address", "Phone", "Email",
                "TaxID, Business License", "Primary Contact - Full Name", "Primary Contact - Email",
                "Primary Contact - Phone"};

        String[] newStringArray = Arrays.stream(stringArray).limit(expectedHeaders.length).toArray(String[]::new);

        if (Arrays.equals(newStringArray, expectedHeaders)) {
            for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                XSSFRow rows = worksheet.getRow(index);
                if (null != rows.getCell(0)) {
                    RegistrationEntity registration = new RegistrationEntity();
                    Roles roles = new Roles();
                    try {
                        registration.setExcelId(fileName + "_" + rows.getCell(0).getNumericCellValue());
                        registration.setBusinessName(rows.getCell(1).getStringCellValue());
                        registration.setAddress(rows.getCell(2).getStringCellValue());

                        rows.getCell(3).setCellType(CellType.STRING);
                        registration.setBusinessPhone(rows.getCell(3).getStringCellValue());

                        registration.setEmailId(rows.getCell(4).getStringCellValue());
                        registration.setTaxIdBusinessLicense(rows.getCell(5).getStringCellValue());
                        registration.setFirstName(rows.getCell(6).getStringCellValue());
                        registration.setContactEmail(rows.getCell(7).getStringCellValue());
                        rows.getCell(8).setCellType(CellType.STRING);
                        registration.setContactPhone(rows.getCell(8).getStringCellValue());

                        roles.setRolesId(2L);
                        registration.setRoles(roles);
                    } catch (Exception e) {
                        e.printStackTrace();
                        //TODO : Add error to log file
                        logger.info("Error in setting object");
                        continue;
                    }
                    try {
                        logger.info("SkillPartnerFileDataImpl || importExcelFile || Saving data of row: {} of name: {}", index, rows.getCell(1).getStringCellValue());
                        registrationService.insertDetails(registration);

                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("SkillPartnerFileDataImpl || importExcelFile || Error in saving data of row: {} of name: {}", index, rows.getCell(1).getStringCellValue());
                        continue;
                    }
                }
            }
        } else {
            throw new ServiceException(WRONG_TEMPLATE.getErrorCode(), WRONG_TEMPLATE.getErrorDesc());
        }
        return "Sync Success";
    }
}
