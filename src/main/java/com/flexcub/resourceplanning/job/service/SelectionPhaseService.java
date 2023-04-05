package com.flexcub.resourceplanning.job.service;

import com.flexcub.resourceplanning.job.dto.*;
import com.flexcub.resourceplanning.job.entity.FeedbackRate;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.job.entity.WorkFlowComponent;
import liquibase.pro.packaged.S;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SelectionPhaseService {

    List<SelectionPhase> getSelectionPhase();

    @Transactional
    List<SelectionPhase> insertMultipleData(List<SelectionPhaseDto> selectionPhasesDto);

    List<SelectionPhaseResponse> insertRequirementPhase(InsertRequirementPhaseDto insertRequirementPhase);

    StageStatusDto scheduleInterview(ScheduleInterviewDto scheduleInterviewDto, Boolean isAutoScheduled);

    StageStatusDto acceptInterview(String jobId, int ownerId);

    StageStatusDto rejectCandidate(RejectCandidateDto rejectCandidateDto);

    List<SelectionPhaseResponse> getCandidatesByJobId(String jobId);

    StageStatusDto reInitiateHiring(String jobId, int skillOwnerId);

    @Transactional
    SelectionPhaseResponse candidateInterviewDetails(String jobId, int skillOwnerId);

    List<WorkFlowComponent> getWorkflow();

    int currentStage(SelectionPhase selectionPhase);

    @Transactional
    SlotConfirmBySeekerDto slotConfirmedBySeeker(String jobId, int skillOwnerId);

    @Transactional
    NewSlotRequestBySeekerDto updateNewSlotBySeeker(String jobId, int skillOwnerId);

    RequirementPhaseDetailsDto updateDetailsForParticularRound(RequirementPhaseDetailsDto requirementPhaseDetailsDto);

    SlotConfirmByOwnerDto getSlots(String jobId, int skillOwnerId);

    SlotConfirmByOwnerDto updateSlotBySkillOwner(SlotConfirmByOwnerDto slotConfirmByOwnerDto);

    AcceptRejectDto acceptRejectBySkillOwner(AcceptRejectDto acceptRejectDto);

    void shortlistingMail(String jobId);

    RequirementPhaseDetailsDto selectedForRound(String jobId, int skillOwnerId, int stage);

    FlowLockedDto isLocked(String jobid);

    @Transactional
    List<SelectionPhaseResponse> addDataToDb(InsertRequirementPhaseDto insertRequirementPhase, List<SelectionPhase> selectionPhaseList);

    boolean autoSchedule(String jobId, int skillOwnerId, int stage);

    SlotConfirmByOwnerDto updateCommonSlotByOwner(SlotConfirmByOwnerDto slotConfirmByOwnerDto);


    List<RateApprovalDto> updateSkillOwnerRate(List<RateApprovalDto> rateApprovalDto);


    List<FeedbackRate> getFeedback();

    RescheduleDto  rescheduleRound(String jobId, int skillOwnerId, int stage);

}
