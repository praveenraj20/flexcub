package com.flexcub.resourceplanning.notifications.service;

import com.flexcub.resourceplanning.invoice.dto.AdminInvoice;
import com.flexcub.resourceplanning.invoice.entity.Invoice;
import com.flexcub.resourceplanning.invoice.entity.InvoiceAdmin;
import com.flexcub.resourceplanning.invoice.entity.SkillSeekerInvoice;
import com.flexcub.resourceplanning.job.dto.*;
import com.flexcub.resourceplanning.job.entity.RequirementPhase;
import com.flexcub.resourceplanning.job.entity.SelectionPhase;
import com.flexcub.resourceplanning.notifications.dto.Notification;
import com.flexcub.resourceplanning.notifications.entity.*;
import com.flexcub.resourceplanning.skillpartner.dto.HistoryOfJobs;
import com.flexcub.resourceplanning.skillpartner.dto.JobHistory;
import com.flexcub.resourceplanning.skillpartner.dto.OwnerDetails;
import com.flexcub.resourceplanning.skillseeker.entity.PoEntity;
import com.flexcub.resourceplanning.skillseeker.entity.SkillSeekerMSAEntity;
import com.flexcub.resourceplanning.skillseeker.entity.StatementOfWorkEntity;

import java.util.List;

public interface NotificationService {

    List<OwnerNotificationsEntity> getOwnerNotification(int id);

    List<SeekerNotificationsEntity> getSeekerNotification(int id);

    List<PartnerNotificationsEntity> getPartnerNotification(int id);

    ContentEntity scheduleInterviewNotification(ScheduleInterviewDto scheduleInterviewDto
            , Notification notificationDto);

    ContentEntity autoScheduleInterviewNotification(ScheduleInterviewDto scheduleInterviewDto
            , Notification requirementPhaseDetailsDto);

    ContentEntity slotBySkillOwnerNotification(SlotConfirmByOwnerDto slotConfirmByOwnerDto);

    ContentEntity acceptBySkillOwnerNotification(AcceptRejectDto acceptRejectDto);

    ContentEntity acceptInterviewBySkillOwnerNotification(String jobId, int ownerId);

    ContentEntity newSlotBySeekerNotification(String jobId, int skillOwnerId
            , NewSlotRequestBySeekerDto newSlotRequestBySeekerDto);

    ContentEntity newSlotBySeekerNotification(int skillOwnerId);

    ContentEntity slotConfirmedBySeekerNotification(SlotConfirmBySeekerDto slotConfirmBySeekerDto);

    ContentEntity shortlistBySeekerNotification(SelectionPhase phase);

    ContentEntity qualifiedNotification(SelectionPhase selectionPhase, RequirementPhase requirementPhase);

    ContentEntity rejectedNotification(SelectionPhase selectionPhase);

    ContentEntity reinitiateNotification(SelectionPhase selectionPhase);

    ContentEntity msaStatusNotification(SkillSeekerMSAEntity skillSeekerMSAEntity
            , Notification notificationDto);

    ContentEntity sowStatusNotification(StatementOfWorkEntity statementOfWorkEntity
            , Notification notificationDto);

    Boolean markAsReadSeeker(int id);

    Boolean markAsReadOwner(int id);

    Boolean markAsReadPartner(int id);

    Boolean markAsReaderAdmin(int id);

    List<OwnerNotificationsEntity> getLastFiveNotificationOfOwner(int ownerId);

    List<JobHistory> getJobHistoryInPartner(int ownerId);

    List<PartnerNotificationsEntity> getNotificationForParticularOwner(int ownerId, String jobId);

    List<PartnerNotificationsEntity> getLastFiveNotificationOfPartner(int partnerId);

    List<SeekerNotificationsEntity> getSeekerLastFiveNotification(int seekerId);

    List<OwnerDetails> getOwnerDetailsInPartner(int partnerId);

    List<SeekerNotificationsEntity> getSeekerNotificationByOwner(int ownerId, String jobId);

    List<OwnerNotificationsEntity> getJobSpecificNotificationForOwner(int skillownerId, String jobId);



    ContentEntity seekerInvoiceStatusNotification(InvoiceAdmin skillSeekerInvoice
            , Notification notificationDto);

//    ContentEntity getContractNotificationsInSeeker(int ownerId);


//    ContentEntity getContractNotificationsInPartner(int ownerId);

//    ContentEntity getContractNotificationsInOwner(int ownerId);
    ContentEntity poStatusNotification(PoEntity poEntity, Notification notificationDto);

    ContentEntity partnerInvoiceStatusNotification(Invoice invoice
            , Notification notificationDto);

    List<SuperAdminNotifications> getSuperAdminNotification();

    List<SuperAdminNotifications> getLastFiveAdminNotification();


    List<HistoryOfJobs> getHistoryOfJobs(int ownerId);
}
