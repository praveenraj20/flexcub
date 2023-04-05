package com.flexcub.resourceplanning.skillowner.dto;

import com.flexcub.resourceplanning.skillowner.entity.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SkillOwnerSkillSet {

    private int ownerSkillSetId;

    private int skillOwnerEntityId;

    private OwnerSkillLevelEntity ownerSkillLevelEntity;

    private OwnerSkillTechnologiesEntity ownerSkillTechnologiesEntity;

    private OwnerSkillRolesEntity ownerSkillRolesEntity;

    private OwnerSkillDomainEntity ownerSkillDomainEntity;

    private OwnerSkillSetYearsEntity ownerSkillSetYearsEntity;

    private String lastUsed;
}
