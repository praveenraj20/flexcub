package com.flexcub.resourceplanning.skillseeker.service;

import com.flexcub.resourceplanning.registration.entity.RegistrationEntity;
import com.flexcub.resourceplanning.skillseeker.dto.*;
import com.flexcub.resourceplanning.skillseeker.entity.SeekerModulesEntity;
import com.flexcub.resourceplanning.skillseeker.entity.SubRoles;
import com.flexcub.resourceplanning.skillseeker.entity.TemplateTable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

public interface SkillSeekerService {

    SkillSeeker addClientDetails(SkillSeeker skillSeeker) throws MessagingException;

    SkillSeeker updateClientDetails(SkillSeeker skillSeeker);

    void deleteData(int id);

    SkillSeeker getSeekerData(int id);

    SkillSeeker updateData(SkillSeeker skillSeekerInfo);

    void addEntryToSkillSeeker(RegistrationEntity registration);

    ResponseEntity<Resource> downloadTemplate() throws IOException;

    SkillSeeker addSeekerSubRoles(int skillSeeker, int role);

    List<SkillSeeker> getSkillSeeker(String taxId);

    List<SeekerAccess> addSubRole(SubRole subRole);

    List<SeekerModulesEntity> getModules();

    List<SubRoles> getRoles();

    List<SeekerModulesEntity> getAccessDetails(int seekerId);

    List<SeekerRoleListing> getAccessByTaxId(String taxId);

    List<Contracts> getContractDetails(int seekerId);

    List<ContractDetails> getListsOfContractDetails(int ownerId);

    OnBoarding onBoardingSkillOwner(OnBoarding skillOwnerDto);

    ProjectTaskDetails getProjectTaskDetailsBySeeker(int id);

    List<Contracts> getAllContractDetails();

    List<ContractDetails> getListsOfContractDetailsInPartner(int partnerId);

    List<TemplateTable> uploadAgreementTemplate(List<MultipartFile> multipartFiles) throws IOException;
}
