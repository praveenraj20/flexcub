package com.flexcub.resourceplanning.skillowner.service;

import com.flexcub.resourceplanning.skillowner.dto.OwnerSkillDomain;

import java.util.List;

public interface OwnerSkillDomainService {
    List<OwnerSkillDomain> getDatadomain();

    OwnerSkillDomain insertDataDomain(OwnerSkillDomain ownerSkillDomainDto);

    OwnerSkillDomain updateDomain(OwnerSkillDomain ownerSkillDomainDto);
}
