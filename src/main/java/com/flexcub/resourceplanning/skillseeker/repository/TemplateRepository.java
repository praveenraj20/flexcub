package com.flexcub.resourceplanning.skillseeker.repository;

import com.flexcub.resourceplanning.skillseeker.entity.TemplateTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateTable, Long> {

    @Query(value = "SELECT * FROM public.template_table WHERE template_name ='MSA_Agreement_Template_2023.docx'", nativeQuery = true)
    Optional<TemplateTable> findByMSAFile();

    @Query(value = "SELECT * FROM public.template_table WHERE template_name ='SOW_Agreement_Template_2023.docx'", nativeQuery = true)
    Optional<TemplateTable> findBySOWFile();

    @Query(value = "SELECT * FROM public.template_table WHERE template_name ='PO_Agreement_Template_2023.docx'", nativeQuery = true)
    Optional<TemplateTable> findByPOFile();

}