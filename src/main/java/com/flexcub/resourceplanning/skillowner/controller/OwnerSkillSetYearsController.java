package com.flexcub.resourceplanning.skillowner.controller;

import com.flexcub.resourceplanning.skillowner.entity.OwnerSkillSetYearsEntity;
import com.flexcub.resourceplanning.skillowner.service.impl.OwnerSkillSetYearsServiceImpl;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/v1/OwnerSkillSetYears", produces = {"application/json"})
@ApiResponses({@ApiResponse(responseCode = "200", description = "Success"),
        @ApiResponse(responseCode = "500", description = "Internal Server Error"),
        @ApiResponse(responseCode = "404", description = "Bad Request"),
        @ApiResponse(responseCode = "400", description = "Bad Request")})
public class OwnerSkillSetYearsController {

    @Autowired
    OwnerSkillSetYearsServiceImpl ownerSkillsetYearsService;

    Logger logger = LoggerFactory.getLogger(OwnerSkillSetController.class);

    @GetMapping(value = "/getOwnerSkillSetYears")
    public ResponseEntity<List<OwnerSkillSetYearsEntity>> getDetails(){
        logger.info("OwnerSkillSetController || getDetails || Getting the Details from the OwnerSkillSetYearsEntity ");
        return new ResponseEntity<>(ownerSkillsetYearsService.getSkillsetYears(), HttpStatus.OK);
    }
}
