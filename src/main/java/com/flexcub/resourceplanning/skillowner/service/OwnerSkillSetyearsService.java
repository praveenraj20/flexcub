package com.flexcub.resourceplanning.skillowner.service;

import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillSetYearsEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface OwnerSkillSetyearsService {


     List<OwnerSkillSetYearsEntity> getSkillsetYears();

}
