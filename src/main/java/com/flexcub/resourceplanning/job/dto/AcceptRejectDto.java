package com.flexcub.resourceplanning.job.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptRejectDto {

    private String jobId;
    private int skillOwnerEntityId;
    private Boolean accepted;
}
