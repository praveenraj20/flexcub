package com.flexcub.resourceplanning.skillseeker.service.impl;


import com.flexcub.resourceplanning.config.ModelMapperConfiguration;
import com.flexcub.resourceplanning.exceptions.ServiceException;
import com.flexcub.resourceplanning.job.entity.Job;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.job.repository.JobRepository;
import com.flexcub.resourceplanning.job.repository.SelectionPhaseRepository;
import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillSetEntity;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerEntity;
import com.flexcub.resourceplanning.skillowner.entity.SkillOwnerResumeAndImage;
import com.flexcub.resourceplanning.skillowner.repository.OwnerSkillSetRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerRepository;
import com.flexcub.resourceplanning.skillowner.repository.SkillOwnerResumeAndImageRepository;
import com.flexcub.resourceplanning.skillowner.service.OwnerSkillSetService;
import com.flexcub.resourceplanning.skillseeker.dto.RecommendedCandidates;
import com.flexcub.resourceplanning.skillseeker.service.LocationMatchForJobService;
import com.flexcub.resourceplanning.skillseeker.service.TalentRecommendationService;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.flexcub.resourceplanning.utils.FlexcubErrorCodes.INVALID_JOB_ID;

@Service

@Component
@Log4j2
public class TalentRecommendationServiceImpl implements TalentRecommendationService {

    @Autowired
    ModelMapper modelMapper;
    Logger logger = LoggerFactory.getLogger(TalentRecommendationServiceImpl.class);

    @Autowired
    ModelMapperConfiguration modelMapperConfiguration;
    @Autowired
    OwnerSkillSetService ownerSkillSetService;
    @Autowired
    SkillOwnerRepository skillOwnerRepository;
    @Autowired
    LocationMatchForJobService locationMatchForJobService;
    @Autowired
    SelectionPhaseRepository selectionPhaseRepository;
    @Autowired
    JobRepository jobRepository;
    @Autowired
    SkillOwnerResumeAndImageRepository skillOwnerResumeAndImageRepository;
    @Autowired
    OwnerSkillSetRepository ownerSkillSetRepository;

    /**
     * Get the matching skill owners for the job
     *
     * @param jobId
     * @return the list of recommended candidates
     */
    @Transactional
    @Override
    public List<RecommendedCandidates> getTalentRecommendation(String jobId) {
        List<SkillOwnerEntity> skillOwnerEntity = skillOwnerRepository.getAvailableTalents();
        List<RecommendedCandidates> recommendedCandidatesList = new ArrayList<>();
        Optional<Job> job = jobRepository.findByJobId(jobId);
        if (job.isPresent()) {
            skillOwnerEntity.forEach(availableSkillOwnerEntity -> {
                if (skillOwnerRestriction(job, availableSkillOwnerEntity)) {
                    double jobMaxRate = job.get().getMaxRate();
                    Integer ownerRateCard = availableSkillOwnerEntity.getRateCard();
                    double ratePercentage = (0.1 * jobMaxRate);
                    double max_limit=jobMaxRate+ratePercentage;
                    if (max_limit>=ownerRateCard) {

                        try {
                            logger.info("TalentRecommendationServiceImpl || getTalentRecommendation ||Setting Values to recommended list");
                            RecommendedCandidates recommendedCandidatesDto = new RecommendedCandidates();
                            recommendedCandidatesDto.setJobId(jobId);
                            recommendedCandidatesDto.setJobTitle(job.get().getJobTitle());
                            recommendedCandidatesDto.setPreScreen(job.get().getScreeningQuestions());
                            recommendedCandidatesDto.setVerified(true);
                            int ownerTotalExp = availableSkillOwnerEntity.getExpMonths() + (availableSkillOwnerEntity.getExpYears() * 12); //30
                            int jobExpMonth = 0;
                            if(job.get().getOwnerSkillYearOfExperience()!=null){
                            String jobExp = job.get().getOwnerSkillYearOfExperience().getExperience();

                            if (jobExp.equalsIgnoreCase("0")) {
                                jobExpMonth =0;
                            } else {
                                jobExpMonth = Integer.parseInt(jobExp.substring(0, jobExp.length() - 1)) * 12;
                            }
                            }else {
                                int jobExp = job.get().getOwnerSkillLevelEntity().getSkillSetLevelId() + job.get().getOwnerSkillSetYearsEntity().getOwnerSkillSetYearsId();

                                if (jobExpMonth == 0) {

                                } else {
                                    jobExpMonth = jobExp * 12;
                                }
                            }
                            if (jobExpMonth < ownerTotalExp) {
                                logger.info("TalentRecommendationServiceImpl || getTalentRecommendation ||Setting Values to recommended list");
                                recommendedCandidatesDto.setSkillOwnerContact(availableSkillOwnerEntity.getPhoneNumber());
                                recommendedCandidatesDto.setSkillOwnerEmailAddress(availableSkillOwnerEntity.getPrimaryEmail());
                                recommendedCandidatesDto.setSkillOwnerExperience(availableSkillOwnerEntity.getExpYears() + " Years, " +
                                        availableSkillOwnerEntity.getExpMonths() + " Months");
                                recommendedCandidatesDto.setSkillOwnerId(availableSkillOwnerEntity.getSkillOwnerEntityId());
                                recommendedCandidatesDto.setSkillOwnerName(availableSkillOwnerEntity.getFirstName() + " " + availableSkillOwnerEntity.getLastName());
                                //TODO : resume must be inserted into skillOwner
                                recommendedCandidatesDto.setSkillOwnerResume(" ");
                                Optional<List<OwnerSkillSetEntity>> ownerSkillSetEntity = Optional.ofNullable(ownerSkillSetRepository.findBySkillOwnerEntityId(availableSkillOwnerEntity.getSkillOwnerEntityId()));
                                if (ownerSkillSetEntity.isPresent()) {
                                    logger.info("TalentRecommendationServiceImpl || getTalentRecommendation ||Setting Values for skill set percentage");
                                    int skillSetPercentage = ownerSkillSetService.skillSetPercentage(jobId, ownerSkillSetEntity.get());
                                    recommendedCandidatesDto.setSkillSetMatchPercentage(skillSetPercentage);
                                    String jobLocation = job.get().getJobLocation().replace(" ", "+");
                                    String ownerLocation = (availableSkillOwnerEntity.getCity() + "," + availableSkillOwnerEntity.getState()).replace(" ", "+");
                                    int locationMatchPercentage = locationMatchForJobService.getLocationMatchPercentage(jobLocation, ownerLocation);
                                    recommendedCandidatesDto.setLocationMatchPercentage(locationMatchPercentage);
                                    logger.info("TalentRecommendationServiceImpl || getTalentRecommendation ||Setting Values for Rate percentage");
                                    int rateMatchPercentage = 0;
                                    double ownerRate = availableSkillOwnerEntity.getRateCard();
                                    double jobRate = job.get().getMaxRate();
                                    if (ownerRate <= jobRate) {
                                        rateMatchPercentage = 100;
                                    } else if (ownerRate <= (0.1*jobRate+jobRate)) {
                                        rateMatchPercentage = (int) (100.0 - (((ownerRate-jobRate) / (0.1*jobRate)) * 100));
                                    }
                                    recommendedCandidatesDto.setRateMatchPercentage(rateMatchPercentage);
                                    double avgMatchPercentage = (double) (rateMatchPercentage + locationMatchPercentage + skillSetPercentage) / 3;
                                    recommendedCandidatesDto.setOverallMatchPercentage((int) avgMatchPercentage);
                                    Optional<SelectionPhase> selectionPhase = selectionPhaseRepository.findByJobIdAndSkillOwnerId(jobId, availableSkillOwnerEntity.getSkillOwnerEntityId());
                                    if (selectionPhase.isPresent()) {
                                        recommendedCandidatesDto.setShortlist(true);
                                        recommendedCandidatesDto.setAccepted(selectionPhase.get().getAccepted());
                                    }
                                    recommendedCandidatesDto.setResumeAvailable(availableSkillOwnerEntity.isResumeAvailable());
                                    recommendedCandidatesDto.setImageAvailable(availableSkillOwnerEntity.isImageAvailable());
                                    if (job.get().getFederalSecurityClearance() && !availableSkillOwnerEntity.isFederalSecurityClearance()) {
                                        recommendedCandidatesList.remove(recommendedCandidatesDto);

                                    } else {
                                        recommendedCandidatesDto.setFederalSecurityClearance(availableSkillOwnerEntity.isFederalSecurityClearance());
                                        recommendedCandidatesList.add(recommendedCandidatesDto);
                                        recommendedCandidatesList.sort(Comparator.comparing(RecommendedCandidates::getOverallMatchPercentage).reversed());

                                    }

                                }
                            }
                        } catch (Exception e) {
                            logger.error("Error setting SkillOwnerID {} to recommended Talent List, with exception {}",
                                    availableSkillOwnerEntity.getSkillOwnerEntityId(), e.getMessage());
                        }
                    }
                }
            });

        } else {
            throw new ServiceException(INVALID_JOB_ID.getErrorCode(), INVALID_JOB_ID.getErrorDesc());
        }
        logger.info("TalentRecommendationServiceImpl || AddTalentRecommendation ||RecommendedCandidateListAdded");
        return recommendedCandidatesList;
    }

    private boolean skillOwnerRestriction(Optional<Job> job, SkillOwnerEntity skillOwnerEntity) {
        Optional<List<SelectionPhase>> bySkillOwnerId = selectionPhaseRepository.findBySkillOwnerId(skillOwnerEntity.getSkillOwnerEntityId());
        if (bySkillOwnerId.isPresent() && !bySkillOwnerId.get().isEmpty()) {
            int numberOfInterviewAttending = bySkillOwnerId.get().size();
            for (SelectionPhase selectionPhase : bySkillOwnerId.get()) {
                if (job.get().getSkillSeeker().getId() == selectionPhase.getJob().getSkillSeeker().getId()) {
                    if (null == selectionPhase.getRejectedOn() || LocalDate.now().isBefore(selectionPhase.getRejectedOn().plusDays(60))) {
                        return false;
                    }
                }
                if (null != selectionPhase.getRejectedOn()) {
                    numberOfInterviewAttending--;
                }
            }
            return numberOfInterviewAttending < 3;
        }
        return true;
    }

}