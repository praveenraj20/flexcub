package com.flexcub.resourceplanning.skillseeker.repository;

import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillSeekerRepository extends JpaRepository<SkillSeekerEntity, Integer> {

    //    @Query(value = "SELECT e FROM  skill_seeker e WHERE e.is_added_by_admin=?1", nativeQuery = true)
    Optional<List<SkillSeekerEntity>> findByIsAddedByAdminTrue();

    @Query(value = "SELECT e FROM  skill_seeker e WHERE e.sub_role_id=1", nativeQuery = true)
    SkillSeekerEntity findBySubRolesId(Long subRoleId);

    @Query(value = "SELECT * FROM  skill_seeker  WHERE tax_id_business_license=? AND sub_role_id=?;", nativeQuery = true)
    List<SkillSeekerEntity> findByTaxIdBusinessLicenseAndSubRoles(String taxIdBusinessLicenseId, Long subRoles);

    Optional<List<SkillSeekerEntity>> findByTaxIdBusinessLicense(String taxIdBusinessLicense);


    @Query(value = "SELECT * FROM skill_seeker WHERE  tax_id_business_license=? AND id =?;", nativeQuery = true)
    Optional<SkillSeekerEntity> findByTaxIdBusinessLicenseAndSeekerId(String taxIdBusinessLicense, int id);

    SkillSeekerEntity findByEmail(String emailId);
}
