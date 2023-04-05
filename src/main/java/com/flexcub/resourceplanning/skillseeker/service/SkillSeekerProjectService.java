package com.flexcub.resourceplanning.skillseeker.service;

import com.flexcub.resourceplanning.skillseekeradmin.dto.SkillSeekerProject;

import java.util.List;

public interface SkillSeekerProjectService {

    List<SkillSeekerProject> insertData(List<SkillSeekerProject> skillSeekerProjectEntityList);

    List<SkillSeekerProject> getProjectData(int id);

    void deleteData(int id);

    SkillSeekerProject updateData(SkillSeekerProject skillSeekerprojectEntity);
}
