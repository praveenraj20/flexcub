package com.flexcub.resourceplanning.skillpartner.service;

import java.io.IOException;
import java.io.InputStream;

public interface SkillPartnerFileDataService {

    String readSkillPartnerExcelFile(int id);

    String importExcelFile(InputStream excelInputStream, String fileName) throws IOException;
}
