package com.flexcub.resourceplanning.skillseeker.service.impl;

import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerEntity;
import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerProjectEntity;
import com.flexcub.resourceplanning.skillseeker.repository.SkillSeekerProjectRepository;
import com.flexcub.resourceplanning.skillseeker.repository.SkillSeekerRepository;
import com.flexcub.resourceplanning.skillseeker.service.SkillSeekerProjectService;
import com.flexcub.resourceplanning.skillseeker.service.SkillSeekerTechnologyService;
import com.flexcub.resourceplanning.skillseekeradmin.dto.SkillSeekerProject;
import com.flexcub.resourceplanning.utils.NullPropertyName;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.*;
import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.TITLE_EXISTS;

@Service
public class SkillSeekerProjectServiceImpl implements SkillSeekerProjectService {
    @Autowired
    SkillSeekerProjectRepository projectRepo;

    @Autowired
    SkillSeekerTechnologyService skillSeekerTechnologyService;
    Logger logger = LoggerFactory.getLogger(SkillSeekerProjectServiceImpl.class);

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    SkillSeekerProjectRepository skillSeekerProjectRepository;


    @Autowired
    SkillSeekerRepository skillSeekerRepository;


    /**
     * This method is to insert skillSeeker project details.
     *
     * @return It returns inserted skillSeeker projects.
     * @paramskillSeekerProjectEntityList
     */


    @Override
    public List<SkillSeekerProject> insertData(List<SkillSeekerProject> skillSeekerProjectList) {
        List<SkillSeekerProjectEntity> skillSeekerProjectEntities = new ArrayList<>();

        for (SkillSeekerProject skillSeekerProject1 : skillSeekerProjectList) {

            int departmentId = skillSeekerProject1.getOwnerSkillDomainEntity().getDomainId();
            skillSeekerProject1.getOwnerSkillDomainEntity().setDomainId(departmentId);
            String projectTitle = skillSeekerProject1.getTitle();
            skillSeekerProject1.setTitle(projectTitle);
            int seekerId = skillSeekerProject1.getSkillSeeker().getId();
            skillSeekerProject1.getSkillSeeker().setId(seekerId);
           Optional<SkillSeekerEntity> skillSeeker = skillSeekerRepository.findById(seekerId);
            if (skillSeeker.isPresent()) {
                Optional<List<SkillSeekerProjectEntity>> projectByDepartment = skillSeekerProjectRepository.findProjectByDepartmentAndSeekerId(skillSeekerProject1.getOwnerSkillDomainEntity().getDomainId(),seekerId);
                for (SkillSeekerProjectEntity seekerProject : projectByDepartment.get()) {

                    if (seekerProject.getTitle().equalsIgnoreCase(skillSeekerProject1.getTitle())) {
                        logger.info("Project already exists in the selected department");
                        throw new ServiceException(TITLE_EXISTS.getErrorCode(), TITLE_EXISTS.getErrorDesc());
                    }
                }
                SkillSeekerProjectEntity skillSeekerProjectEntity1 = modelMapper.map(skillSeekerProject1, SkillSeekerProjectEntity.class);
                skillSeekerProjectEntities.add(skillSeekerProjectEntity1);
                skillSeekerProjectEntities.forEach(skillSeekerProjectEntity -> {
                    if (skillSeekerProjectEntity.getSkillSeekerTechnologyData() == null) {
                        skillSeekerProjectEntity.setSkillSeekerTechnologyData(null);
                    } else {
                        skillSeekerProjectEntities.forEach(skillSeekerProject -> skillSeekerProject.setSkillSeekerTechnologyData(skillSeekerTechnologyService.insertMultipleData(skillSeekerProject.getSkillSeekerTechnologyData())));
                    }
                });
                skillSeekerProjectRepository.saveAll(skillSeekerProjectEntities);
            }else {
                logger.info("Invalid seeker id");
                throw new ServiceException(INVALID_SEEKER.getErrorCode(), INVALID_SEEKER.getErrorDesc());
            }
        }
        logger.info("SkillSeekerProjectServiceImpl || insertData || Inserting the SeekerProject list: {}", skillSeekerProjectList);
        List<SkillSeekerProject> skillSeekerProjectLists = new ArrayList<>();
        for (SkillSeekerProjectEntity skillSeekerProjectEntity : skillSeekerProjectEntities) {
            SkillSeekerProject skillSeekerProject = modelMapper.map(skillSeekerProjectEntity, SkillSeekerProject.class);
            skillSeekerProjectLists.add(skillSeekerProject);
        }
        return skillSeekerProjectLists;
    }


    /**
     * @return
     * @paramid
     */
    @Override
    public List<SkillSeekerProject> getProjectData(int skillSeekerId) {
        try {
            List<SkillSeekerProject> skillSeekerProjectList = new ArrayList<>();
            Optional<List<SkillSeekerProjectEntity>> skillSeekerProject = projectRepo.findBySkillSeekerId(skillSeekerId);

            if (!skillSeekerProject.get().isEmpty()) {
                for (SkillSeekerProjectEntity skillSeekerProjectEntity : skillSeekerProject.get()) {
                    SkillSeekerProject skillSeekerProject1 = modelMapper.map(skillSeekerProjectEntity, SkillSeekerProject.class);
                    skillSeekerProjectList.add(skillSeekerProject1);
                }

            }
            //adding a static project as default
            SkillSeekerProject staticProject = new SkillSeekerProject();
            staticProject.setId(0);
            staticProject.setTitle("Default");
            skillSeekerProjectList.add(staticProject);
            return skillSeekerProjectList;
        }
        catch(Exception e){
            throw new ServiceException(INVALID_SEEKER.getErrorCode(), INVALID_SEEKER.getErrorDesc());
        }
    }


    /**
     * This method is to delete skillSeeker project detail based on id.
     *
     * @paramid
     */
    @Override
    public void deleteData(int id) {

        Optional<SkillSeekerProjectEntity> skillSeekerProjectData = projectRepo.findById(id);


        if (skillSeekerProjectData.isPresent()) {
            logger.info("SkillSeekerProjectServiceImpl || deleteData || Deleting the SeekerProject id: {}", id);
            projectRepo.deleteById(id);
        } else {
            throw new ServiceException(INVALID_PROJECT_ID.getErrorCode(), INVALID_PROJECT_ID.getErrorDesc());
        }

    }

    /**
     * This method is to update skillSeeker project details.
     *
     * @return It returns updated data of skillSeeker projects.
     * @paramskillSeekerProjectEntity
     */

    @Override
    public SkillSeekerProject updateData(SkillSeekerProject skillSeekerProject) {
        SkillSeekerProjectEntity seekerProjectEntity = modelMapper.map(skillSeekerProject, SkillSeekerProjectEntity.class);


        int projectId = skillSeekerProject.getId();
        Optional<SkillSeekerProjectEntity> project = skillSeekerProjectRepository.findById(projectId);
        if (!project.isPresent()) {
            logger.info("Project does not exists for this ID");
            throw new ServiceException(INVALID_PROJECT_ID.getErrorCode(), INVALID_PROJECT_ID.getErrorDesc());
        }else{
            int departmentId = project.get().getOwnerSkillDomainEntity().getDomainId();
            Optional<List<SkillSeekerProjectEntity>> skillSeekerProjectData = skillSeekerProjectRepository.findProjectByDepartmentAndSeekerId(departmentId,project.get().getSkillSeeker().getId());
            project.get().setTitle(skillSeekerProject.getTitle());
            project.get().setSummary(skillSeekerProject.getSummary());
            for (SkillSeekerProjectEntity projectEntity : skillSeekerProjectData.get()) {
                if (projectEntity.getTitle().equalsIgnoreCase(project.get().getTitle())) {
                    logger.info("Project already exists in the selected department");
                    throw new ServiceException(TITLE_EXISTS.getErrorCode(), TITLE_EXISTS.getErrorDesc());
                }
            }
            logger.info("SkillSeekerProjectServiceImpl || updateData || Updating the SkillSeeker Project");
            BeanUtils.copyProperties(project, skillSeekerProject, NullPropertyName.getNullPropertyNames(seekerProjectEntity));
            skillSeekerProjectRepository.saveAndFlush(project.get());
        }
        return modelMapper.map(project, SkillSeekerProject.class);

    }

}

