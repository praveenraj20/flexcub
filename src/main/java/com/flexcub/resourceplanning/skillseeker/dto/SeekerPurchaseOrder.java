package com.flexcub.resourceplanning.skillseeker.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SeekerPurchaseOrder {

    private int id;
    private int ownerId;
    private String ownerName;
    private String jobId;
    private String skillSeekerProjectName;
    private String skillSeekerProjectDept;
    private String role;
    private String email;
    private String phoneNumber;
    private String status;
}
