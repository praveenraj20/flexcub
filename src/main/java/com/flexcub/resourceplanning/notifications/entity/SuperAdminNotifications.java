package com.flexcub.resourceplanning.notifications.entity;


import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "superAdmin_notifications")
public class SuperAdminNotifications {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Column
    private int skillSeekerEntityId;

    @Column
    private String title;

    @Column
    private int contentId;

    @ManyToOne
    @JoinColumn(name = "content_obj")
    private ContentEntity contentObj;

    @Column
    private Boolean markAsRead;

    @Column
    private String content;

    private int msaId;

    private String msaStatus;

    @Column
    private Date date;

}
