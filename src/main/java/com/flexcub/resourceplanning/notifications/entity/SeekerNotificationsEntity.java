package com.flexcub.resourceplanning.notifications.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "seeker_notifications")

public class SeekerNotificationsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;


    @Column
    private int skillSeekerEntityId;

    private int skillOwnerId;

    @Column
    private String title;

    @Column
    private int contentId;

    @ManyToOne
    @JoinColumn(name = "content_obj")
    private ContentEntity contentObj;

    @Column
    private String jobId;

    @Column
    private String taxIdBusinessLicense;

    @Column
    private Date date;

    @Column
    private Boolean markAsRead;

    @Column
    private String content;

    @Column
    private int stage;

    private int ownerId;

}
