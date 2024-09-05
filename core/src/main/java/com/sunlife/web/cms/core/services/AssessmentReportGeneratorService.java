package com.sunlife.web.cms.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import com.sunlife.web.cms.core.beans.Assessment;
import com.sunlife.web.cms.core.beans.AssessmentReport;

public interface AssessmentReportGeneratorService {
	
	
	public AssessmentReport generateAssessmentReport(ResourceResolver resolver,Assessment assessmentModel);
	
	public boolean storeAssessmentReport(ResourceResolver resolver,AssessmentReport assessmentReport) throws PersistenceException, JsonProcessingException;

}
