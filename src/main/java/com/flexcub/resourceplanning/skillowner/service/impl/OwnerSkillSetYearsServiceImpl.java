package com.flexcub.resourceplanning.skillowner.service.impl;

import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillSetYearsEntity;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillSetYearsRepository;
import com.flexcub.resourceplanning.skillowner.service.OwnerSkillSetyearsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OwnerSkillSetYearsServiceImpl implements OwnerSkillSetyearsService {

    @Autowired
    OwnerSkillSetYearsRepository ownerSkillSetYearsRepository;

    @Override
    public List<OwnerSkillSetYearsEntity> getSkillsetYears() {

        return ownerSkillSetYearsRepository.findAll();
    }
}
