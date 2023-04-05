package com.flexcub.resourceplanning.skillowner.service.impl;

import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.registration.service.RegistrationService;
import com.flexcub.resourceplanning.skillowner.dto.SkillOwnerFile;
import com.flexcub.resourceplanning.skillowner.entity.*;
import com.flexcub.resourceplanning.skillowner.repository.*;
import com.flexcub.resourceplanning.skillowner.service.ClientService;
import com.flexcub.resourceplanning.skillowner.service.SkillOwnerFileDataService;
import com.flexcub.resourceplanning.skillowner.service.SkillOwnerService;
import com.flexcub.resourceplanning.skillpartner.entity.SkillPartnerEntity;
import com.flexcub.resourceplanning.skillpartner.repository.SkillPartnerRepository;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.flexcub.resourceplanning.utils.FlexcubConstants.*;
import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.*;

@Service
@Component
public class SkillOwnerFileDataServiceImpl implements SkillOwnerFileDataService {

    Logger logger = LoggerFactory.getLogger(SkillOwnerFileDataServiceImpl.class);
    @Autowired
    OwnerSkillTechnologiesRepository ownerSkillTechnologiesRepository;
    @Autowired
    OwnerSkillRolesRepository ownerSkillRolesRepository;
    @Autowired
    ExcelFileRepository excelFileRepository;
    @Autowired
    VisaStatusRepository visaStatusRepository;
    @Autowired
    SkillOwnerService skillOwnerService;
    @Autowired
    OwnerSkillLevelRepository ownerSkillLevelRepository;
    @Autowired
    OwnerSkillSetRepository ownerSkillSetRepository;
    @Autowired
    OwnerSkillStatusRepository ownerSkillStatusRepository;
    @Autowired
    SkillPartnerRepository skillPartnerRepository;
    @Autowired
    RegistrationService registrationService;

    @Autowired
    ClientService clientService;
    SkillOwnerFile skillOwnerFileDto = new SkillOwnerFile();
    @Value("${flexcub.defaultTemplateName}")
    private String defaultTemplateName;
    @Autowired
    private OwnerSkillDomainRepository ownerSkillDomainRepository;

    public static <T> List<T> getListFromIterator(Iterator<T> iterator) {
        Iterable<T> iterable = () -> iterator;
        return StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toList());
    }

    /**
     * @return response entity
     */
    @Transactional
    public SkillOwnerFile checkFileTypeAndUpload(List<MultipartFile> excelDataFileList, String skillPartnerId) {
        //Creating map to store file types
        HashMap<String, String> fileTypeList = new HashMap<>();
        fileTypeList.put(CSV, TEXT_CSV);
        fileTypeList.put(XLS, APPLICATION_VND_MS_EXCEL);
        fileTypeList.put(XLSX, XLSX_SPREADSHEET);
        Optional<SkillPartnerEntity> partnerEntity= Optional.ofNullable(skillPartnerRepository.findById(Integer.valueOf(skillPartnerId)).orElseThrow(() -> new ServiceException(PARTNER_ID_NOT_FOUND.getErrorCode(), PARTNER_ID_NOT_FOUND.getErrorDesc())));

        if (!excelDataFileList.isEmpty() &&  partnerEntity.isPresent()) {
            for (MultipartFile excelDataFile : excelDataFileList) {
                if (fileTypeList.containsValue(excelDataFile.getContentType())) {
                    logger.info("SkillOwnerFileDataImpl || checkFileTypeAndUpload || File Type verified !!");
                    try {
                        String uploadResponse = uploadExcelFile(excelDataFile, String.valueOf(skillPartnerId));
                        if (!"File uploaded".equalsIgnoreCase(uploadResponse)) {
                            throw new ServiceException();
                        }
                        logger.info("SkillOwnerFileDataImpl || checkFileTypeAndUpload || Uploaded the file successfully: {} // ->", excelDataFile.getOriginalFilename());
                    } catch (ServiceException e) {
                        throw new ServiceException(WRONG_TEMPLATE.getErrorCode(), WRONG_TEMPLATE.getErrorDesc());
                    } catch (Exception e) {
                        logger.info("SkillOwnerFileDataImpl || checkFileTypeAndUpload || Could not upload the file: {} // ->", excelDataFile.getOriginalFilename());
                        throw new ServiceException(EXPECTATION_FAILED.getErrorCode(), EXPECTATION_FAILED.getErrorDesc());
                    }
                } else {
                    logger.info("SkillOwnerFileDataImpl || checkFileTypeAndUpload || Wrong File Format ");
                    throw new ServiceException(WRONG_FILE_FORMAT.getErrorCode(), WRONG_FILE_FORMAT.getErrorDesc());
                }
            }
            return skillOwnerFileDto;
        } else {
            throw new ServiceException(FILE_NOT_FOUND.getErrorCode(), FILE_NOT_FOUND.getErrorDesc());
        }
    }

    @Transactional
    public String readExcelFile(int id) {
        //Creating map of filetype to determine how to read file
        HashMap<String, String> fileTypeList = new HashMap<>();
        fileTypeList.put(CSV, TEXT_CSV);
        fileTypeList.put(XLS, APPLICATION_VND_MS_EXCEL);
        fileTypeList.put(XLSX, XLSX_SPREADSHEET);
        try {
            logger.info("FileReadingImpl || readExcelFile || Calling repo to check if file exists");
            Optional<FileDB> fileDb = excelFileRepository.findById(id);

            if (fileDb.isPresent() && !(fileDb.get().isSynced())) {
                String skillPartnerId = fileDb.get().getSkillPartnerId();
                InputStream excelInputStream = new ByteArrayInputStream(fileDb.get().getData());
                if (fileDb.get().getType().equalsIgnoreCase(fileTypeList.get(CSV))) {
                    importCsvFile(excelInputStream, skillPartnerId);
                    logger.info("File synced successfully ");
                    return "File synced successfully";
                } else if (fileDb.get().getType().equalsIgnoreCase(fileTypeList.get(XLS)) || fileDb.get().getType().equalsIgnoreCase(fileTypeList.get(XLSX))) {
                    importExcelFile(excelInputStream, skillPartnerId, fileDb.get().getName());
                    logger.info("FileReadingImpl || readExcelFile || File synced successfully ");
                    return "File synced successfully";
                } else {
                    logger.info("FileReadingImpl || readExcelFile || Wrong File Format ");
                    throw new ServiceException(WRONG_FILE_FORMAT.getErrorCode(), WRONG_FILE_FORMAT.getErrorDesc());
                }
            } else {
                logger.info("FileReadingImpl || readExcelFile || File not Found ");
                throw new ServiceException(FILE_NOT_FOUND.getErrorCode(), FILE_NOT_FOUND.getErrorDesc());
            }
        } catch (Exception e) {
            logger.info("FileReadingImpl || readExcelFile || File synced failure");
            throw new ServiceException(EXPECTATION_FAILED.getErrorCode(), EXPECTATION_FAILED.getErrorDesc());
        }
    }

    /**
     * @param file excel
     * @return File Object
     */
    @Transactional
    private String uploadExcelFile(MultipartFile file, String skillPartnerId) throws IOException {
        logger.info("SkillOwnerFileDataImpl || uploadExcelFile || Calling Repository to add file in DataBase");
        HashMap<String, String> fileTypeList = new HashMap<>();
        fileTypeList.put(CSV, TEXT_CSV);
        fileTypeList.put(XLS, APPLICATION_VND_MS_EXCEL);
        fileTypeList.put(XLSX, XLSX_SPREADSHEET);
        boolean isTemplateCorrect = false;
        if (file.getContentType().equalsIgnoreCase(fileTypeList.get(XLS)) || file.getContentType().equalsIgnoreCase(fileTypeList.get(XLSX))) {
            InputStream excelInputStream = new ByteArrayInputStream(file.getBytes());
            XSSFWorkbook workbook = new XSSFWorkbook(excelInputStream);

            if (workbook.getNumberOfSheets() != 0) {
                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                    if (workbook.getSheetName(i).equals("Skill Owner(Candidate)")) {
                        XSSFSheet worksheet = workbook.getSheetAt(0);
                        Row row = worksheet.getRow(0);
                        isTemplateCorrect = checkExcelTemplate(row);
                    }
                    if (workbook.getSheetName(i).equals("Skill Partner(Employer)")) {
                        XSSFSheet worksheet = workbook.getSheetAt(1);
                        Row row = worksheet.getRow(0);
                        isTemplateCorrect = checkSkillPartnerExcelTemplate(row);
                    }
                }

            }
            if (!isTemplateCorrect) {
                XSSFSheet worksheet = workbook.getSheetAt(0);
                Row row = worksheet.getRow(0);
                isTemplateCorrect = checkExcelTemplate(row);
            } else {
                throw new ServiceException(WRONG_TEMPLATE.getErrorCode(), WRONG_TEMPLATE.getErrorDesc());
            }

        } else {
            isTemplateCorrect = checkCsvTemplate(file);
        }
        if (isTemplateCorrect) {
            String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename())) + "_" + LocalDateTime.now();
            FileDB fileDB = new FileDB(fileName, file.getContentType(), file.getBytes(), false, skillPartnerId);
            FileDB fileDB1 = excelFileRepository.save(fileDB);
            skillOwnerFileDto.setId(fileDB1.getId());
            return "File uploaded";
        }
        throw new ServiceException(WRONG_TEMPLATE.getErrorCode(), WRONG_TEMPLATE.getErrorDesc());
    }

    private ResponseEntity<String> importCsvFile(InputStream excel, String skillOwnerId) throws IOException {
        Map<Integer, List<String>> failedMap = new HashMap<>();
        Map<Integer, List<String>> successMap = new HashMap<>();

        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(excel, StandardCharsets.UTF_8)); CSVParser csvParser = new CSVParser(fileReader, CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim())) {

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                if (null != csvRecord) {
                    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();
                    SkillPartnerEntity skillPartnerEntity = new SkillPartnerEntity();
                    try {
                        if (null != skillOwnerId) {
                            skillPartnerEntity.setSkillPartnerId(Integer.parseInt(skillOwnerId));
                        }
                        if (null == skillOwnerId) {
                            skillPartnerEntity.setSkillPartnerId(Integer.parseInt((csvRecord.get("Skill Partner ID"))));
                        }
                        skillOwnerEntity.setSkillPartnerEntity(skillPartnerEntity);
                        skillOwnerEntity.setFirstName(csvRecord.get(FIRST_NAME));
                        skillOwnerEntity.setLastName(csvRecord.get(LAST_NAME));

                        skillOwnerEntity.setDOB(Date.valueOf(csvRecord.get(DOB)));

                        if (csvRecord.get(PRIMARY_EMAIL).isEmpty()) {
                            logger.info("Email not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No email");
                        }
                        skillOwnerEntity.setPrimaryEmail(csvRecord.get(PRIMARY_EMAIL));

                        skillOwnerEntity.setAlternateEmail(csvRecord.get(ALTERNATE_EMAIL));
                        skillOwnerEntity.setPhoneNumber(csvRecord.get(PHONE_NUMBER));

                        if (null != csvRecord.get(ALTERNATE_PHONE)) {
                            skillOwnerEntity.setAlternatePhoneNumber(csvRecord.get(ALTERNATE_PHONE));
                        }

                        if (null != csvRecord.get(STATUS)) {
                            skillOwnerEntity.setOwnerSkillStatusEntity(setSkillStatusForCSV(csvRecord.get(STATUS)));
                        }
                        if (null == csvRecord.get(STATUS)) {
                            OwnerSkillStatusEntity ownerSkillStatusEntity = new OwnerSkillStatusEntity();
                            ownerSkillStatusEntity.setSkillOwnerStatusId(1);
                            skillOwnerEntity.setOwnerSkillStatusEntity(ownerSkillStatusEntity);
                        }

                        OwnerSkillSetEntity ownerSkillSetEntity = new OwnerSkillSetEntity();
                        if (null == csvRecord.get(DOMAIN)) {
                            logger.info("Domain not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No domain found");
                        }
                        //                ownerSkillSetEntity.setOwnerSkillDomainEntity(getDomainFromExcel(csvRecord.get(DOMAIN)));

                        if (null == csvRecord.get(LEVEL)) {
                            logger.info("Level not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No Level found");
                        }
                        //                 ownerSkillSetEntity.setOwnerSkillLevelEntity(getLevelFromExcel(csvRecord.get(LEVEL)));

                        if (null == csvRecord.get(TECHNOLOGY)) {
                            logger.info("Technologies not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No Technologies found");
                        }

                        skillOwnerEntity.setExpYears(Integer.parseInt(csvRecord.get(EXP_YEARS)));
                        skillOwnerEntity.setExpMonths(Integer.parseInt(csvRecord.get(EXP_MONTHS)));
                        ownerSkillSetEntity.setLastUsed(String.valueOf(csvRecord.get(LAST_USED)));

                        if (null != csvRecord.get(ADDRESS)) {
                            skillOwnerEntity.setAddress(csvRecord.get(ADDRESS));
                        }
                        skillOwnerEntity.setCity(csvRecord.get(CITY));
                        skillOwnerEntity.setState(csvRecord.get(STATE));
                        skillOwnerEntity.setVisaStatus(getVisaStatusFromExcel(csvRecord.get(VISA_STATUS)));
                        skillOwnerEntity.setLinkedIn(csvRecord.get(LINKEDIN));

                    } catch (IOException e) {
                        throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
                    }
                    try {
                        logger.info(String.format("FileReadingImpl || importCsvFile || Saving data of name %s", csvRecord.get(FIRST_NAME)));
                        List<SkillOwnerEntity> skillOwnerEntities = new ArrayList<>();
                        skillOwnerEntities.add(skillOwnerEntity);
                        SkillOwnerEntity savedSkillOwnerEntity = skillOwnerService.insertData(skillOwnerEntities).get(0);
//                        ownerSkillSetEntity.setSkillOwnerEntityId(savedSkillOwnerEntity.getSkillOwnerEntityId());
//                        ownerSkillSetRepository.save(ownerSkillSetEntity);
                        List<ClientEntity> clientEntityList = new ArrayList<>();

                        for (int i = 1; i < 6; i++) {
                            ClientEntity clientEntity = new ClientEntity();
                            SkillOwnerEntity ownerEntity = new SkillOwnerEntity();

                            ownerEntity.setSkillOwnerEntityId(savedSkillOwnerEntity.getSkillOwnerEntityId());

                            clientEntity.setSkillOwnerEntityId(savedSkillOwnerEntity.getSkillOwnerEntityId());
                            clientEntity.setEmployerName(csvRecord.get("Client_" + i));
                            Date startDate = Date.valueOf(csvRecord.get("Start_Dt_" + i));
                            Date endDate = Date.valueOf(csvRecord.get("End_Dt_" + i));
                            clientEntity.setStartDate(startDate);
                            clientEntity.setEndDate(endDate);

                            clientEntityList.add(clientEntity);
                        }
                        for (ClientEntity ce : clientEntityList) {
//                            clientService.insertClient(ce);
                        }

                        List<String> successList = successMap.get(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId());
                        if (ObjectUtils.isEmpty(successList)) {
                            successList = new ArrayList<>();
                        }
                        successList.add(skillOwnerEntity.getPrimaryEmail());
                        successMap.put(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId(), successList);
                    } catch (ServiceException serviceException) {
                        List<String> failedEntries = failedMap.get(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId());
                        if (failedEntries != null) {
                            failedEntries.add(skillOwnerEntity.getPrimaryEmail());
                        } else {
                            failedEntries = new ArrayList<>();
                            failedEntries.add(skillOwnerEntity.getPrimaryEmail());
                        }
                        failedMap.put(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId(), failedEntries);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("FileReadingImpl || importCsvFile || Error in saving data of name: {}", csvRecord.get(FIRST_NAME));
                    }
                }
            }
        }
        registrationService.sendMailForFailedOwnerRegistrations(failedMap);
        return ResponseEntity.status(HttpStatus.OK).body("File Parsing Completed");
    }

    private ResponseEntity<String> importExcelFile(InputStream excel, String skillPartnerId, String fileName) throws IOException, InterruptedException {
        Map<Integer, List<String>> failedMap = new HashMap<>();
        Map<Integer, List<String>> successMap = new HashMap<>();

        logger.info("FileReadingImpl || importExcelFile || Method start");
        XSSFWorkbook workbook = new XSSFWorkbook(excel);
        XSSFSheet worksheet = workbook.getSheetAt(0);
        Row row = worksheet.getRow(0);

        if (checkExcelTemplate(row)) {
            for (int index = 1; index < worksheet.getPhysicalNumberOfRows(); index++) {
                LocalTime localTime = LocalTime.now();
                Thread.sleep(5000);
                logger.info("Time  taken to sleep" + LocalTime.now().until(localTime, ChronoUnit.MILLIS));
                XSSFRow rows = worksheet.getRow(index);
                if (null != rows.getCell(0) && !rows.getCell(0).getRawValue().equalsIgnoreCase("")) {
                    SkillOwnerEntity skillOwnerEntity = new SkillOwnerEntity();
                    SkillPartnerEntity skillPartnerEntity = new SkillPartnerEntity();
                    OwnerSkillSetEntity ownerSkillSetEntity = new OwnerSkillSetEntity();
                    try {
                        if (!skillPartnerId.equalsIgnoreCase("")) {
                            skillPartnerEntity.setSkillPartnerId(Integer.parseInt(skillPartnerId));
                        }
                        if (skillPartnerId.equalsIgnoreCase("")) {
                            int id = skillPartnerRepository.findByExcelId(fileName + "_" + rows.getCell(0).getNumericCellValue()).getSkillPartnerId();
                            skillPartnerEntity.setSkillPartnerId(id);
                        }
                        skillOwnerEntity.setSkillPartnerEntity(skillPartnerEntity);

                        skillOwnerEntity.setFirstName(rows.getCell(1).getStringCellValue());
                        skillOwnerEntity.setLastName(rows.getCell(2).getStringCellValue());


                        DataFormatter dataFormatter = new DataFormatter();
                        String cellValue = dataFormatter.formatCellValue(rows.getCell(3));
                        SimpleDateFormat date1 = new SimpleDateFormat("dd/MM/yyyy");
                        java.util.Date date = date1.parse(cellValue);
                        java.sql.Date sqlDate = new java.sql.Date(date.getTime());

                        if (sqlDate.equals(null)) {
                            logger.info("Dob is not Found for particular", row);

                        }
                        if (null != date) {
                            skillOwnerEntity.setDOB(sqlDate);
                        }

                        if (rows.getCell(4).getStringCellValue().isEmpty()) {
                            logger.info("Email not found for {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No email");
                        }
                        skillOwnerEntity.setPrimaryEmail(rows.getCell(4).getStringCellValue());
                        if (null != rows.getCell(5)) {
                            skillOwnerEntity.setAlternateEmail(rows.getCell(5).getStringCellValue());
                        }
                        rows.getCell(6).setCellType(CellType.STRING);
                        skillOwnerEntity.setPhoneNumber(rows.getCell(6).getStringCellValue());
                        if (null != rows.getCell(7)) {
                            rows.getCell(7).setCellType(CellType.STRING);
                            skillOwnerEntity.setAlternatePhoneNumber(rows.getCell(7).getStringCellValue());
                        }
                        skillOwnerEntity.setOwnerSkillStatusEntity(setSkillStatus(rows.getCell(8)));
                        if (null == rows.getCell(9)) {
                            logger.info("Domain not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No domain found");
                        }
//                        ownerSkillSetEntity.setOwnerSkillDomainEntity(getDomainFromExcel(rows.getCell(9).getStringCellValue()));

                        if (null == rows.getCell(10)) {
                            logger.info("Level not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No Level found");
                        }
//                        ownerSkillSetEntity.setOwnerSkillLevelEntity(getLevelFromExcel(rows.getCell(10).getStringCellValue()));

                        if (null == rows.getCell(11)) {
                            logger.info("Technologies not found for: {}", skillOwnerEntity.getFirstName());
                            throw new IOException("No Technologies found");
                        }

//                        List<OwnerSkillTechnologiesEntity> techFromExcel = getTechFromExcel(rows.getCell(11).getStringCellValue());
//                        for (int i = 0; i <= techFromExcel.size() - 1; i++) {
//                            OwnerSkillTechnologiesEntity ownerSkillTechnologiesEntity = techFromExcel.get(i);
//                            ownerSkillSetEntity.setOwnerSkillTechnologiesEntity(ownerSkillTechnologiesEntity);
//                        }
//                        ownerSkillSetEntity.setOwnerSkillRolesEntity(getRoleFromExcel(rows.getCell(12).getStringCellValue()));
//                        skillOwnerEntity.setExpYears((int) rows.getCell(13).getNumericCellValue());
//                        skillOwnerEntity.setExpMonths((int) rows.getCell(14).getNumericCellValue());
//                        rows.getCell(15).setCellType(CellType.STRING);

                        String lastUsed = dataFormatter.formatCellValue(rows.getCell(15));
                        ownerSkillSetEntity.setLastUsed(lastUsed);
                        ownerSkillSetRepository.save(ownerSkillSetEntity);

                        if (null != rows.getCell(16)) {
                            skillOwnerEntity.setAddress(rows.getCell(16).getStringCellValue());
                        }
                        skillOwnerEntity.setCity(rows.getCell(17).getStringCellValue());
                        skillOwnerEntity.setState(rows.getCell(18).getStringCellValue());
//                        skillOwnerEntity.setVisaStatus(getVisaStatusFromExcel(rows.getCell(19).getStringCellValue()));
                        skillOwnerEntity.setLinkedIn(rows.getCell(20).getStringCellValue());

                    } catch (Exception e) {
//                        e.printStackTrace();
                        logger.info("row is skipped " + index);
                        logger.info("Error in setting object");
                        continue;
                    }
                    try {
                        logger.info(String.format("FileReadingImpl || importExcelFile || Saving data of row %d", index, rows.getCell(1).getStringCellValue()));

                        List<ClientEntity> clientEntityList = new ArrayList<>();

//                        for (int i = 21; i < 36; i = i + 3) {
//                            ClientEntity clientEntity = new ClientEntity();
//                            SkillOwnerEntity ownerEntity = new SkillOwnerEntity();
//
//                            if (null != rows.getCell(i).getStringCellValue() || !(rows.getCell(i).getStringCellValue().equalsIgnoreCase(""))) {
//                                clientEntity.setEmployerName(rows.getCell(i).getStringCellValue());
//                                clientEntity.setSkillOwnerEntityId(ownerEntity.getSkillOwnerEntityId());
//                                /*Date startDate = new Date(rows.getCell(i + 1).getDateCellValue().getTime());
//                                Date endDate = new Date(rows.getCell(i + 2).getDateCellValue().getTime());
//                                clientEntity.setStartDate(startDate);
//                                clientEntity.setEndDate(endDate);*/
//
//                                clientEntityList.add(clientEntity);
//                            }
//                        }
//                        if (!clientEntityList.isEmpty()) {
//                            for (ClientEntity ce : clientEntityList) {
//                                clientService.insertClient(ce);
//                            }
//                        }
                        List<SkillOwnerEntity> skillOwnerEntities = new ArrayList<>();
                        skillOwnerEntities.add(skillOwnerEntity);
                        SkillOwnerEntity savedSkillOwnerEntity = skillOwnerService.insertData(skillOwnerEntities).get(0);
                        ownerSkillSetEntity.setSkillOwnerEntityId(savedSkillOwnerEntity.getSkillOwnerEntityId());


                        List<String> successList = successMap.get(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId());
                        if (ObjectUtils.isEmpty(successList)) {
                            successList = new ArrayList<>();
                        }
                        successList.add(skillOwnerEntity.getPrimaryEmail());
                        successMap.put(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId(), successList);
                    } catch (ServiceException serviceException) {
                        List<String> failedEntries = failedMap.get(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId());
                        if (failedEntries != null) {
                            failedEntries.add(skillOwnerEntity.getPrimaryEmail());
                        } else {
                            failedEntries = new ArrayList<>();
                            failedEntries.add(skillOwnerEntity.getPrimaryEmail());
                        }
                        failedMap.put(skillOwnerEntity.getSkillPartnerEntity().getSkillPartnerId(), failedEntries);
                    } catch (Exception e) {
                        e.printStackTrace();
                        logger.error("FileReadingImpl || importExcelFile || Error in saving data at row no {} of name {}", index, rows.getCell(1).getStringCellValue());
                    }
                } else {
                    break;
                }
            }
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("wrong Template");
        }

        registrationService.sendMailForFailedOwnerRegistrations(failedMap);
        return ResponseEntity.status(HttpStatus.OK).body("File Parsing Completed");
    }

    @Transactional
    public ResponseEntity<Resource> downloadTemplate() throws IOException {
        Optional<FileDB> fileDb = excelFileRepository.findByName(defaultTemplateName);
        ByteArrayResource resource = null;
        if (fileDb.isPresent()) {
            resource = new ByteArrayResource(fileDb.get().getData());
        } else {
            throw new IOException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename = Sample_template.xlsx");

        return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM).body(resource);

    }

    private boolean checkExcelTemplate(Row row) {
        ArrayList<String> headerData = new ArrayList<>();

        //Storing header data to verify template
        for (Cell cell : row) {
            headerData.add(String.valueOf(cell));
        }
        String[] stringArray = headerData.toArray(new String[0]);
        String[] expectedHeaders = {"Skill Partner ID", FIRST_NAME, LAST_NAME, DOB, PRIMARY_EMAIL, ALTERNATE_EMAIL, PHONE_NUMBER, ALTERNATE_PHONE,"Hourly Rate", STATUS, DOMAIN, LEVEL, TECHNOLOGY, ROLES, EXP_YEARS, EXP_MONTHS, "Last Used (mmyyyy)", ADDRESS, CITY, STATE, VISA_STATUS, LINKEDIN, "Client_1", "Start_Dt_1", "End_Dt_1", "Client_2", "Start_Dt_2", "End_Dt_2", "Client_3", "Start_Dt_3", "End_Dt_3", "Client_4", "Start_Dt_4", "End_Dt_4", "Client_5", "Start_Dt_5", "End_Dt_5"};
        String[] newStringArray = Arrays.stream(stringArray).limit(expectedHeaders.length).toArray(String[]::new);
        return Arrays.equals(newStringArray, expectedHeaders);
    }

    private boolean checkSkillPartnerExcelTemplate(Row row) {
        ArrayList<String> headerData = new ArrayList<>();
        //Storing header data to verify template
        for (Cell cell : row) {
            headerData.add(String.valueOf(cell));
        }
        String[] stringArray = headerData.toArray(new String[0]);
        String[] expectedHeaders = {"Skill Partner ID", "Business Name", "Address", "Phone", "Email", "TaxID, Business License", "Primary Contact - Full Name", "Primary Contact - Email", "Primary Contact - Phone"};
        String[] newStringArray = Arrays.stream(stringArray).limit(expectedHeaders.length).toArray(String[]::new);
        return Arrays.equals(newStringArray, expectedHeaders);
    }

    private boolean checkCsvTemplate(MultipartFile file) throws IOException {
        String[] expectedHeaders = {"Skill Partner ID", FIRST_NAME, LAST_NAME, DOB, PRIMARY_EMAIL, ALTERNATE_EMAIL, PHONE_NUMBER, ALTERNATE_PHONE,"Hourly Rate", STATUS, DOMAIN, LEVEL, TECHNOLOGY, ROLES, EXP_YEARS, EXP_MONTHS, "Last Used (mmyyyy)", ADDRESS, CITY, STATE, VISA_STATUS, LINKEDIN, "Client_1", "Start_Dt_1", "End_Dt_1", "Client_2", "Start_Dt_2", "End_Dt_2", "Client_3", "Start_Dt_3", "End_Dt_3", "Client_4", "Start_Dt_4", "End_Dt_4", "Client_5", "Start_Dt_5", "End_Dt_5"};
        InputStream excelInputStream = new ByteArrayInputStream(file.getBytes());
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(excelInputStream));
        CSVParser csvFileParser = new CSVParser(fileReader, CSVFormat.EXCEL);
        List<CSVRecord> csvRecords = csvFileParser.getRecords();

        List<String> list = getListFromIterator(csvRecords.get(0).iterator());
        String[] listArray = list.toArray(new String[list.size()]);
        return Arrays.equals(expectedHeaders, listArray);
    }

    public OwnerSkillDomainEntity getDomainFromExcel(String domains) {
//        List<String> domainList = List.of(domains.split(","));
//        OwnerSkillDomainEntity domainEntity = null;

//        List<OwnerSkillDomainEntity> ownerSkillDomainEntities = new ArrayList<>();
//        domainList.forEach(domain -> {
//            OwnerSkillDomainEntity ownerSkillDomainEntity = new OwnerSkillDomainEntity();
//            if (null != ownerSkillDomainRepository.findByDomainValuesIgnoreCase(domain)) {
//                ownerSkillDomainEntity = ownerSkillDomainRepository.findByDomainValuesIgnoreCase(domain);
//            } else {
//                ownerSkillDomainEntity.setDomainValues(domain);
//                domainEntity = ownerSkillDomainRepository.save(ownerSkillDomainEntity);
//            }
//            ownerSkillDomainEntities.add(ownerSkillDomainEntity);
//
//        });
//        return  ;


        OwnerSkillDomainEntity ownerSkillDomainEntity = new OwnerSkillDomainEntity();
        if (null != ownerSkillDomainRepository.findByDomainValuesIgnoreCase(domains)) {
            ownerSkillDomainEntity = ownerSkillDomainRepository.findByDomainValuesIgnoreCase(domains);
        } else {
            ownerSkillDomainEntity.setDomainValues(domains);
            ownerSkillDomainEntity = ownerSkillDomainRepository.save(ownerSkillDomainEntity);
        }

        return ownerSkillDomainEntity;
    }

    public List<OwnerSkillTechnologiesEntity> getTechFromExcel(String techs) {
        List<String> techList = List.of(techs.split(","));
        List<OwnerSkillTechnologiesEntity> ownerSkillTechEntities = new ArrayList<>();
        techList.forEach(tech -> {
            OwnerSkillTechnologiesEntity ownerSkillTechnologiesEntity = new OwnerSkillTechnologiesEntity();
            if (null != ownerSkillTechnologiesRepository.findByTechnologyValuesIgnoreCase(tech)) {
                ownerSkillTechnologiesEntity = ownerSkillTechnologiesRepository.findByTechnologyValuesIgnoreCase(tech);
            } else {
                ownerSkillTechnologiesEntity.setTechnologyValues(tech);
                ownerSkillTechnologiesEntity = ownerSkillTechnologiesRepository.save(ownerSkillTechnologiesEntity);
            }
            ownerSkillTechEntities.add(ownerSkillTechnologiesEntity);
        });
        return ownerSkillTechEntities;
    }

    public OwnerSkillRolesEntity getRoleFromExcel(String roles) {
//        List<String> roleList = List.of(roles.split(","));
//        List<OwnerSkillRolesEntity> ownerSkillRoleEntities = new ArrayList<>();
//        roleList.forEach(role -> {
//            OwnerSkillRolesEntity ownerSkillRolesEntity = new OwnerSkillRolesEntity();
//            if (null != ownerSkillRolesRepository.findByRolesDescriptionIgnoreCase(role)) {
//                ownerSkillRolesEntity = (ownerSkillRolesRepository.findByRolesDescriptionIgnoreCase(role));
//            } else {
//                ownerSkillRolesEntity.setRolesDescription(role);
//                ownerSkillRolesEntity = (ownerSkillRolesRepository.save(ownerSkillRolesEntity));
//            }
//            ownerSkillRoleEntities.add(ownerSkillRolesEntity);
//        });
//        return ownerSkillRoleEntities;

        OwnerSkillRolesEntity ownerSkillRolesEntity = new OwnerSkillRolesEntity();
        if (null != ownerSkillRolesRepository.findByRolesDescriptionIgnoreCase(roles)) {
            ownerSkillRolesEntity = ownerSkillRolesRepository.findByRolesDescriptionIgnoreCase(roles);
        } else {
            ownerSkillRolesEntity.setRolesDescription(roles);
            ownerSkillRolesEntity = ownerSkillRolesRepository.save(ownerSkillRolesEntity);
        }

        return ownerSkillRolesEntity;
    }

    private VisaEntity getVisaStatusFromExcel(String visaStatus) {
        VisaEntity status = new VisaEntity();
        if (null != visaStatusRepository.findByVisaStatusIgnoreCase(visaStatus)) {
            status = visaStatusRepository.findByVisaStatusIgnoreCase(visaStatus);
        } else {
            status.setVisaStatus(visaStatus);
            status = visaStatusRepository.save(status);
        }
        return status;
    }

    private OwnerSkillLevelEntity getLevelFromExcel(String level) {
        OwnerSkillLevelEntity ownerSkillLevelEntity = new OwnerSkillLevelEntity();
        if (null != ownerSkillLevelRepository.findBySkillLevelDescriptionIgnoreCase(level)) {
            ownerSkillLevelEntity = ownerSkillLevelRepository.findBySkillLevelDescriptionIgnoreCase(level);
        } else {
            ownerSkillLevelEntity.setSkillLevelDescription(level);
            ownerSkillLevelEntity = ownerSkillLevelRepository.save(ownerSkillLevelEntity);
        }
        return ownerSkillLevelEntity;
    }

    private OwnerSkillStatusEntity setSkillStatus(XSSFCell skillStatusCell) {
        OwnerSkillStatusEntity ownerSkillStatusEntityNew = new OwnerSkillStatusEntity();
        if (null != skillStatusCell) {
            Optional<OwnerSkillStatusEntity> ownerSkillStatusEntity = ownerSkillStatusRepository.findByStatusDescriptionIgnoreCase(skillStatusCell.getStringCellValue());
            if (ownerSkillStatusEntity.isPresent()) {
                return ownerSkillStatusEntity.get();
            }
        }
        ownerSkillStatusEntityNew.setSkillOwnerStatusId(1);
        return ownerSkillStatusEntityNew;
    }

    private OwnerSkillStatusEntity setSkillStatusForCSV(String skillStatus) {
        OwnerSkillStatusEntity ownerSkillStatusEntityNew = new OwnerSkillStatusEntity();
        if (null != skillStatus) {
            Optional<OwnerSkillStatusEntity> ownerSkillStatusEntity = ownerSkillStatusRepository.findByStatusDescriptionIgnoreCase(skillStatus);
            if (ownerSkillStatusEntity.isPresent()) {
                return ownerSkillStatusEntity.get();
            }
        }
        ownerSkillStatusEntityNew.setSkillOwnerStatusId(1);
        return ownerSkillStatusEntityNew;
    }
}