package com.flexcub.resourceplanning.skillowner.repository;

import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillSetYearsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OwnerSkillSetYearsRepository extends JpaRepository<OwnerSkillSetYearsEntity,Integer> {

//   List<OwnerSkillSetYearsEntity> getAll();
//    List<OwnerSkillSetYearsEntity> findByExperience(String s);
}
