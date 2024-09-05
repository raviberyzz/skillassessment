package com.sunlife.web.cms.core.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import com.sunlife.web.cms.core.beans.Assessment;
import com.sunlife.web.cms.core.beans.AssessmentReport;
/**
 * @author Ravi Ranjan
 */
public interface AssessmentReportGeneratorService {

	/**
	 * Method performs logic to calculate participant's passing score and then store
	 * it in {@link AssessmentReport} object.
	 * 
	 * @param resolver        instance of {@link Resource Resolver}
	 * @param assessmentModel object created by mapping json payload.
	 * @return {@link AssessmentReport} object
	 */
	public AssessmentReport generateAssessmentReport(ResourceResolver resolver, Assessment assessmentModel);

	/**
	 * Method persists the assessment report of user into jcr repository.Folders are
	 * created in jcr in following order
	 * /var/skillassessment/assessmentreport/technology/year/month/date/userid.
	 * 
	 * @param resolver         instance of {@link ResourceResolver}
	 * @param assessmentReport instance of {@link AssessmentReport} object
	 * @return true if stored successfully otherwise.
	 * @throws PersistenceException
	 * @throws JsonProcessingException
	 */
	public boolean storeAssessmentReport(ResourceResolver resolver, AssessmentReport assessmentReport)
			throws PersistenceException, JsonProcessingException;

}
