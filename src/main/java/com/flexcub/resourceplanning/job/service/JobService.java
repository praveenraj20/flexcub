package com.flexcub.resourceplanning.job.service;

import com.flexcub.resourceplanning.job.dto.JobDto;
import com.flexcub.resourceplanning.job.entity.HiringPriority;
import com.flexcub.resourceplanning.job.entity.Job;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface JobService {

    List<Job> getJobDetails();

    JobDto publish(String jobId);

    JobDto createJobDetails(JobDto jobDto);

    List<JobDto> getAllJobDetails(int seekerId);

    List<HiringPriority> getHiringPriority();

    void deleteJob(String jobId);

}


