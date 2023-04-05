package com.flexcub.resourceplanning.skillowner.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "owner_skill_set_years")
@Getter
@Setter
public class OwnerSkillSetYearsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, name = "owner_skill_set_years_id")
    private int ownerSkillSetYearsId;

    @Column(name = "owner_skill_set_years")
    private String ownerSkillSetYears;

}
