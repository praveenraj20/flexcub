package com.flexcub.resourceplanning.skillseeker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillDomainEntity;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.utils.BaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
public class PoEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "name")
    private String name;

    @Column
    private int skillSeekerId;

    @ManyToOne(targetEntity = SkillSeekerProjectEntity.class)
    @JsonIgnoreProperties("skill_seeker_project")
    @JoinColumn(name = "skill_seeker_project_id", referencedColumnName = "id", updatable = false)
    private SkillSeekerProjectEntity skillSeekerProject;

    @ManyToOne(targetEntity = SkillOwnerEntity.class)
    @JoinColumn(name = "skill_owner_id", referencedColumnName = "id", updatable = false)
    private SkillOwnerEntity skillOwnerEntity;

    @ManyToOne(targetEntity = OwnerSkillDomainEntity.class)
    @JoinColumn(name = "skill_domain_id", referencedColumnName = "id", updatable = false)
    private OwnerSkillDomainEntity ownerSkillDomainEntity;

    @ManyToOne(targetEntity = Job.class)
    @JoinColumn(name = "job_id", referencedColumnName = "job_Id", updatable = false)
    private Job jobId;

    @Column
    private String role;

    @Column(name = "mime_type")
    private String mimeType;

    @Column(name = "size")
    private long size = 0;

    @JsonIgnore
    @Lob
    private byte[] data;

    @OneToOne(targetEntity = ContractStatus.class)
    private ContractStatus poStatus;
    @Column
    private Date dateOfRelease;
    @Column
    private Date expiryDate;
    @Column
    private Date onBoarding;
    @Column
    private Date startDate;
    @Column
    private Date endDate;


}
