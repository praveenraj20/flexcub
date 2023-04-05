package com.flexcub.resourceplanning.skillseeker.repository;

import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StatementOfWorkRepository extends JpaRepository<StatementOfWorkEntity, Integer> {
    Optional<StatementOfWorkEntity> findById(int id);

    Optional<List<StatementOfWorkEntity>> findBySkillSeekerId(int skillSeekerId);

    @Query(value = "SELECT * FROM public.statement_of_work WHERE skill_owner_id=?", nativeQuery = true)
    Optional<StatementOfWorkEntity> findByOwnerId(int ownerId);

    @Query(value = "SELECT * FROM public.statement_of_work WHERE skill_owner_id=? AND skill_seeker_id=? AND " +
            "domain_id =? AND skill_seeker_project_id=? AND job_id =? ", nativeQuery = true)
    Optional<StatementOfWorkEntity> findByAllField(int ownerId, int seekerId, int domainId, int projectID, String jobId);


    @Query(value =
            "Select * from statement_of_work where job_id = ? and skill_owner_id = ?;", nativeQuery = true)
    Optional<StatementOfWorkEntity> findByJobIdAndSkillOwnerId(String jobId, int skillOwnerId);

    @Query(value = "SELECT * FROM public.statement_of_work WHERE skill_owner_id=?", nativeQuery = true)
    StatementOfWorkEntity findByOwner(int ownerId);
}
