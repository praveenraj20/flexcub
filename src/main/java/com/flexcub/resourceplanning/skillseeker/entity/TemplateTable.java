package com.flexcub.resourceplanning.skillseeker.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "template_table")
@Data
public class TemplateTable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String templateName;

    @Column
    private String templateType;

    @Column
    private long size;

    @JsonIgnore
    @Lob
    private byte[] data;

}