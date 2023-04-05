package com.flexcub.resourceplanning.skillseeker.service.impl;

import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.job.repository.JobRepository;
import com.flexcub.resourceplanning.job.repository.RequirementPhaseRepository;
import com.flexcub.resourceplanning.job.repository.SelectionPhaseRepository;
import com.flexcub.resourceplanning.job.service.impl.SelectionPhaseServiceImpl;
import com.flexcub.resourceplanning.notifications.dto.Notification;
import com.flexcub.resourceplanning.notifications.service.NotificationService;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillStatusEntity;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillDomainRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerRepository;
import com.flexcub.resourceplanning.skillseeker.dto.SowStatusDto;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWork;
import com.flexcub.resourceplanning.skillseeker.dto.StatementOfWorkGetDetails;
import com.flexcub.resourceplanning.skillseeker.entity.ContractStatus;
import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;
import com.flexcub.resourceplanning.skillseeker.entity.TemplateTable;
import com.flexcub.resourceplanning.skillseeker.repository.*;
import com.flexcub.resourceplanning.skillseeker.service.StatementOfWorkSerivce;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

import static com.flexcub.resourceplanning.utils.FlexcubConstants.*;
import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.*;

@Service
@Log4j2
public class StatementOfWorkServiceImpl implements StatementOfWorkSerivce {

    Logger logger = LoggerFactory.getLogger(StatementOfWorkServiceImpl.class);
    @Autowired
    PoServiceImpl poService;
    @Autowired
    PoRepository poRepository;
    @Autowired
    private StatementOfWorkRepository statementOfWorkRepository;
    @Autowired
    private ContractStatusRepository sowStatusRepository;
    @Autowired
    private SelectionPhaseServiceImpl selectionPhaseService;
    @Autowired
    private SkillSeekerProjectRepository skillSeekerProjectRepository;
    @Autowired
    private SkillOwnerRepository skillOwnerRepository;
    @Autowired
    private RequirementPhaseRepository requirementPhaseRepository;
    @Autowired
    private SelectionPhaseRepository selectionPhaseRepository;

    @Autowired
    TemplateRepository templateRepository;
    @Autowired
    private OwnerSkillDomainRepository ownerSkillDomainRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private NotificationService notificationService;

    @Override
    @Transactional
    public StatementOfWork addDocument(List<MultipartFile> multipartFiles, int ownerId, int seekerId, int domainId, String role, int projectID, String jobId) {
        Optional<StatementOfWorkEntity> statementOfWorkEntity = statementOfWorkRepository.findByAllField(ownerId, seekerId, domainId, projectID, jobId);
        Optional<SkillOwnerEntity> skillOwner = Optional.ofNullable(skillOwnerRepository.findBySkillOwnerEntityId(ownerId));
        HashMap<String, String> fileTypeList = new HashMap<>();
        StatementOfWork statementOfWorkDto = new StatementOfWork();
        fileTypeList.put(PDF, APPLICATION_VND_PDF);
        fileTypeList.put(DOC, TEXT_DOC);
        fileTypeList.put(DOCX, TEXT_DOCX);
        if (!multipartFiles.isEmpty() && skillOwner.isPresent()) {
            for (MultipartFile multipartFile : multipartFiles) {
                if (fileTypeList.containsValue(multipartFile.getContentType())) {
                    logger.info("StatementOfWorkServiceImpl || checkFileTypeAndUpload || File Type verified !!");
                    try {
                        StatementOfWorkEntity statementOfWork = null;
                        if (!statementOfWorkEntity.isPresent()) {
                            statementOfWork = statementOfWorkRepository.save(create(multipartFile, ownerId, seekerId, domainId, role, projectID, jobId));
                            Notification notification = new Notification();
                            notificationService.sowStatusNotification(statementOfWork, notification);

                            Job byJobJobId = jobRepository.findByJobJobId(jobId);
                            if (byJobJobId.getNumberOfPositions() > 0 && !poRepository.findByOwnerId(ownerId).isPresent()) {
                                byJobJobId.setNumberOfPositions(byJobJobId.getNumberOfPositions() - 1);
                                jobRepository.saveAndFlush(byJobJobId);
                                logger.info("StatementOfWorkServiceImpl || checkNumbersOfPositions ||checksNumbersOfPositions and reduces -1 ");
                                poService.removeCandidates(ownerId, jobId);
                            }
                        } else {
                            statementOfWorkEntity.get().setMimeType(multipartFile.getContentType());
                            statementOfWorkEntity.get().setSize(multipartFile.getSize());
                            statementOfWorkEntity.get().setData(multipartFile.getBytes());
                            statementOfWorkEntity.get().setName(multipartFile.getOriginalFilename());
                            statementOfWork = statementOfWorkRepository.save(statementOfWorkEntity.get());

                        }
                        OwnerSkillStatusEntity ownerSkillStatus = new OwnerSkillStatusEntity();
                        ownerSkillStatus.setSkillOwnerStatusId(4);
                        skillOwner.get().setOwnerSkillStatusEntity(ownerSkillStatus);
                        skillOwnerRepository.save(skillOwner.get());

                        statementOfWorkDto.setId(statementOfWork.getId());

                        statementOfWorkDto.setStatus(statementOfWork.getSowStatus().getStatus());

                        logger.info("StatementOfWorkServiceImpl || checkFileTypeAndUpload || Uploaded the file successfully: {} // ->", multipartFile.getOriginalFilename());
                    } catch (NullPointerException e) {
                        throw new ServiceException(INVALID_REQUEST.getErrorCode(), INVALID_REQUEST.getErrorDesc());
                    } catch (Exception e) {
                        logger.info("StatementOfWorkServiceImpl || checkFileTypeAndUpload || Could not upload the file: {} // ->", multipartFile.getOriginalFilename());
                        throw new ServiceException(EXPECTATION_FAILED.getErrorCode(), EXPECTATION_FAILED.getErrorDesc());
                    }
                } else {
                    logger.info("StatementOfWorkServiceImpl || checkFileTypeAndUpload || Wrong File Format ");
                    throw new ServiceException(WRONG_FILE_FORMAT.getErrorCode(), WRONG_FILE_FORMAT.getErrorDesc());
                }
            }
            return statementOfWorkDto;
        } else {
            throw new ServiceException(INVALID_REQUEST.getErrorCode(), INVALID_REQUEST.getErrorDesc());
        }
    }

    @Transactional
    private StatementOfWorkEntity create(MultipartFile multipartFile, int ownerId, int seekerId, int domainId, String role, int projectID, String jobId) throws IOException {
        Optional<StatementOfWorkEntity> statementOfWork = statementOfWorkRepository.findByOwnerId(ownerId);
        if (!statementOfWork.isPresent()) {
            StatementOfWorkEntity statementOfWorkEntity = new StatementOfWorkEntity();
            Optional<Job> job = jobRepository.findByJobId(jobId);
            if (job.isPresent()) {
                statementOfWorkEntity.setJobId(job.get());
            }
            statementOfWorkEntity.setSkillSeekerId(seekerId);
            if (projectID == 0) {
                statementOfWorkEntity.setSkillSeekerProject(null);
            } else {
                statementOfWorkEntity.setSkillSeekerProject(skillSeekerProjectRepository.getById(projectID));
            }
            if (domainId == 0) {
                statementOfWorkEntity.setOwnerSkillDomainEntity(null);
            } else {
                statementOfWorkEntity.setOwnerSkillDomainEntity(ownerSkillDomainRepository.getById(domainId));
            }

//          statementOfWorkEntity.setDateSigned(date);
            statementOfWorkEntity.setSkillOwnerEntity(skillOwnerRepository.getById(ownerId));
//          statementOfWorkEntity.setOwnerSkillDomainEntity(ownerSkillDomainRepository.getById(domainId));
            statementOfWorkEntity.setMimeType(multipartFile.getContentType());
            statementOfWorkEntity.setSize(multipartFile.getSize());
            statementOfWorkEntity.setData(multipartFile.getBytes());
            statementOfWorkEntity.setRoles(role);
            statementOfWorkEntity.setSowStatus(sowStatusRepository.findById(1).get());
            Date date = new Date();
            statementOfWorkEntity.setDateOfRelease(date);
            statementOfWorkEntity.setName(multipartFile.getOriginalFilename());
            return statementOfWorkEntity;
        } else {
            throw new ServiceException(SOW_ALREADY_ADDED.getErrorCode(), SOW_ALREADY_ADDED.getErrorDesc());
        }
    }

    @Override
    @Transactional
    public List<StatementOfWorkGetDetails> getSowDetails(int skillSeekerId) {
        try {
            Optional<List<StatementOfWorkEntity>> sow = statementOfWorkRepository.findBySkillSeekerId(skillSeekerId);
            if (!sow.get().isEmpty()) {
                List<StatementOfWorkGetDetails> statementOfWorkGetDetailsList = new ArrayList<>();
                for (StatementOfWorkEntity statementOfWorkEntity : sow.get()) {
                    StatementOfWorkGetDetails statementOfWorkGetDetails = new StatementOfWorkGetDetails();
                    statementOfWorkGetDetails.setId(statementOfWorkEntity.getId());
                    statementOfWorkGetDetails.setSkillOwnerName(statementOfWorkEntity.getSkillOwnerEntity().getFirstName());
                    statementOfWorkGetDetails.setRole(statementOfWorkEntity.getRoles());
                    if (null == statementOfWorkEntity.getSkillSeekerProject()) {
                        statementOfWorkGetDetails.setProject("Default");
                    } else {
                        statementOfWorkGetDetails.setProject(statementOfWorkEntity.getSkillSeekerProject().getTitle());
                    }
                    if (null == statementOfWorkEntity.getOwnerSkillDomainEntity()) {
                        statementOfWorkGetDetails.setDepartment("Default");
                    } else {
                        statementOfWorkGetDetails.setDepartment(statementOfWorkEntity.getOwnerSkillDomainEntity().getDomainValues());
                    }
//                  statementOfWorkGetDetails.setDepartment(statementOfWorkEntity.getOwnerSkillDomainEntity().getDomainValues());
                    statementOfWorkGetDetails.setEmail(statementOfWorkEntity.getSkillOwnerEntity().getPrimaryEmail());
                    statementOfWorkGetDetails.setStatus(statementOfWorkEntity.getSowStatus().getStatus());
                    statementOfWorkGetDetails.setPhone(statementOfWorkEntity.getSkillOwnerEntity().getPhoneNumber());
                    statementOfWorkGetDetails.setOwnerId(statementOfWorkEntity.getSkillOwnerEntity().getSkillOwnerEntityId());
                    statementOfWorkGetDetails.setJobId(statementOfWorkEntity.getJobId().getJobId());
                    statementOfWorkGetDetailsList.add(statementOfWorkGetDetails);
                }
                logger.info("StatementOfWorkServiceImpl || getSowDetails || getting StatementOfWork Details");
                return statementOfWorkGetDetailsList;
            } else {
                throw new ServiceException();
            }
        } catch (ServiceException e) {
            throw new ServiceException(INVALID_SEEKER_ID.getErrorCode(), INVALID_SEEKER_ID.getErrorDesc());
        } catch (Exception e) {
            throw new ServiceException(INVALID_REQUEST.getErrorCode(), INVALID_REQUEST.getErrorDesc());
        }
    }

    @Override
    @Transactional
    public List<StatementOfWorkGetDetails> getAllSowDetails() {
        try {
            Optional<List<StatementOfWorkEntity>> sow = Optional.of(statementOfWorkRepository.findAll());
            if (!sow.get().isEmpty()) {
                List<StatementOfWorkGetDetails> statementOfWorkGetDetailsList = new ArrayList<>();
                for (StatementOfWorkEntity statementOfWorkEntity : sow.get()) {
                    if (null != statementOfWorkEntity.getSkillOwnerEntity()) {
                        StatementOfWorkGetDetails statementOfWorkGetDetails = new StatementOfWorkGetDetails();
                        statementOfWorkGetDetails.setId(statementOfWorkEntity.getId());
                        statementOfWorkGetDetails.setSkillOwnerName(statementOfWorkEntity.getSkillOwnerEntity().getFirstName());
                        statementOfWorkGetDetails.setRole(statementOfWorkEntity.getRoles());
                        if (null == statementOfWorkEntity.getSkillSeekerProject()) {
                            statementOfWorkGetDetails.setProject("Default");
                        } else {
                            statementOfWorkGetDetails.setProject(statementOfWorkEntity.getSkillSeekerProject().getTitle());
                        }
                        if (null == statementOfWorkEntity.getOwnerSkillDomainEntity()) {
                            statementOfWorkGetDetails.setDepartment("Default");
                        } else {
                            statementOfWorkGetDetails.setDepartment(statementOfWorkEntity.getOwnerSkillDomainEntity().getDomainValues());
                        }
//                      statementOfWorkGetDetails.setDepartment(statementOfWorkEntity.getOwnerSkillDomainEntity().getDomainValues());
                        statementOfWorkGetDetails.setEmail(statementOfWorkEntity.getSkillOwnerEntity().getPrimaryEmail());
                        statementOfWorkGetDetails.setStatus(statementOfWorkEntity.getSowStatus().getStatus());
                        statementOfWorkGetDetails.setPhone(statementOfWorkEntity.getSkillOwnerEntity().getPhoneNumber());
                        statementOfWorkGetDetails.setOwnerId(statementOfWorkEntity.getSkillOwnerEntity().getSkillOwnerEntityId());
                        statementOfWorkGetDetails.setJobId(statementOfWorkEntity.getJobId().getJobId());
                        statementOfWorkGetDetailsList.add(statementOfWorkGetDetails);
                    }
                }
                logger.info("StatementOfWorkServiceImpl || getAllSowDetails || getting All StatementOfWork Details");
                return statementOfWorkGetDetailsList;
            } else {
                throw new ServiceException();
            }
        } catch (ServiceException e) {
            throw new ServiceException(DATA_NOT_FOUNDED.getErrorCode(), DATA_NOT_FOUNDED.getErrorDesc());
        } catch (Exception e) {
            throw new ServiceException(INVALID_REQUEST.getErrorCode(), INVALID_REQUEST.getErrorDesc());
        }
    }

    /**
     * @param id
     * @param sowStatusId
     * @return dto
     */
    @Override
    public SowStatusDto updateSowStatus(int id, int sowStatusId) {
        Optional<StatementOfWorkEntity> statementOfWorkEntity = statementOfWorkRepository.findById(id);
        Optional<ContractStatus> sowStatus = sowStatusRepository.findById(sowStatusId);
        SowStatusDto sowStatusDto = new SowStatusDto();
        if (statementOfWorkEntity.isPresent() && sowStatus.isPresent()) {
            statementOfWorkEntity.get().setSowStatus(sowStatus.get());
            statementOfWorkRepository.save(statementOfWorkEntity.get());
            sowStatusDto.setSowId(statementOfWorkEntity.get().getId());
            sowStatusDto.setSowStatusId(sowStatus.get().getId());
            if (sowStatus.get().getId() == 1) {
                Notification notification = new Notification();
                notificationService.sowStatusNotification(statementOfWorkEntity.get(), notification);
            }
            if (sowStatus.get().getId() == 6) {
                Notification notification = new Notification();
                notificationService.sowStatusNotification(statementOfWorkEntity.get(), notification);
            }
        } else {
            throw new ServiceException(MSA_ID_NOT_FOUND.getErrorCode(), MSA_ID_NOT_FOUND.getErrorDesc());

        }
        return sowStatusDto;
    }


    @Override
    @Transactional
    public StatementOfWorkEntity downloadAgreementSOW(int id) {
        Optional<StatementOfWorkEntity> sow = statementOfWorkRepository.findById(id);
        if (sow.isPresent()) {
            logger.info("StatementOfWorkImpl || downloadAgreementSOW || DownloadAgreement for StatementOfWork");
            return sow.get();
        } else {
            throw new ServiceException(INVALID_SOW_ID.getErrorCode(), INVALID_SOW_ID.getErrorDesc());

        }
    }

    @Override
    @Transactional
    public ResponseEntity<Resource> templateDownload() {
        Optional<TemplateTable> downloadTemplate = templateRepository.findBySOWFile();

        ByteArrayResource resource = null;
        if (downloadTemplate.isPresent()) {
            resource = new ByteArrayResource(downloadTemplate.get().getData());
        } else {
            throw new ServiceException(FILE_NOT_FOUND.getErrorCode(), FILE_NOT_FOUND.getErrorDesc());
        }
        logger.info("StatementOfWorkImpl || templateDownload || StatementOfWorkTemplateDownload");
        return ResponseEntity.ok().header("Content-disposition", "attachment; filename=" + "Sow_Template").contentType(MediaType.valueOf(downloadTemplate.get().getTemplateType())).body(resource);
    }

    @Override
    @Transactional
    public StatementOfWorkEntity getSow(int id) {
        try {
            Optional<StatementOfWorkEntity> sow = statementOfWorkRepository.findById(id);
            if (sow.get().getMimeType().equals("application/pdf") || sow.get().getMimeType().equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document") || sow.get().getMimeType().equals("application/msword")) {
                return sow.get();
            }
        } catch (ServiceException e) {
            throw new ServiceException(INVALID_ID.getErrorDesc());
        }
        return null;
    }

}