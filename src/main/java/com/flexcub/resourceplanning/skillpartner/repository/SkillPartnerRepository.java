package com.flexcub.resourceplanning.skillpartner.repository;

import com.flexcub.resourceplanning.skillpartner.entity.SkillPartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillPartnerRepository extends JpaRepository<SkillPartnerEntity, Integer> {

    SkillPartnerEntity findByExcelId(String excelId);


}