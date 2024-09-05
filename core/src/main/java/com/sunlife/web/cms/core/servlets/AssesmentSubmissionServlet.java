package com.sunlife.web.cms.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.day.cq.mailer.MessageGatewayService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sunlife.web.cms.core.beans.Assessment;
import com.sunlife.web.cms.core.beans.AssessmentReport;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
import com.sunlife.web.cms.core.constants.Constant.ResponseConstants;
import com.sunlife.web.cms.core.constants.Constant.SlingJob;
import com.sunlife.web.cms.core.services.AssessmentReportGeneratorService;
import com.sunlife.web.cms.core.services.CustomEmailService;
import com.sunlife.web.cms.core.services.ResourceResolverService;
import com.sunlife.web.cms.core.services.UserService;

/**
 * Servlet for generating and storing assessment report in AEM.
 * 
 * @author Ravi Ranjan
 */
@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/submit")
@ServiceDescription("Assessment Submission Service")
public class AssesmentSubmissionServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;

	@Reference
	private AssessmentReportGeneratorService assessmentService;

	@Reference
	private ResourceResolverService resolverService;

	@Reference
	private MessageGatewayService messageGatewayService;
	
	@Reference
	private UserService userService;
	@Reference
	private JobManager jobManager;
	
	@Reference
	private CustomEmailService emailService;
	
	

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType(ResponseConstants.JSON_RESPONSE_TYPE);

		String body = getRequestBody(request);
		ObjectMapper mapper = new ObjectMapper();
		Assessment assessment = mapper.readValue(body, Assessment.class);
		AssessmentReport assessmentReport = null;
		boolean storedinRepository = false;

		try (ResourceResolver resolver = resolverService
				.getResourceResolver(Constant.ServiceUser.SKILLASSESSMENT_USER)) {
			assessmentReport = assessmentService.generateAssessmentReport(resolver, assessment);
			storedinRepository = assessmentService.storeAssessmentReport(resolver, assessmentReport);
	
			if (Objects.nonNull(assessmentReport) && storedinRepository) {
			
				createSlingJobForSendingEmailNotification(resolver,assessmentReport);
				deleteUserInformationFromSystem(resolver, assessment);
				
				
				String response = new Gson().toJson(assessmentReport);
				resp.setStatus(201);
				resp.getWriter().println(response);

			} else {
				
				resp.setStatus(500);
				Map<String, Object> errorResponse = new HashMap<>();
				errorResponse.put(ResponseConstants.STATUS, 500);
				errorResponse.put(ResponseConstants.MESSAGE, "Internal Server Error. Check logs for details");
				resp.getWriter().println(new Gson().toJson(errorResponse));

			}

		} catch (LoginException | RepositoryException e) {
			resp.setStatus(500);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put(ResponseConstants.STATUS, 500);
			errorResponse.put(ResponseConstants.MESSAGE, "Internal Server Error. Check logs for details");
			resp.getWriter().println(new Gson().toJson(errorResponse));
		}

	}
	
	
	private void deleteUserInformationFromSystem(ResourceResolver resolver,Assessment assessment) throws RepositoryException, PersistenceException {
		//delete user account
		userService.deleteUser(resolver, assessment.getUser().getId());
		Resource resource = resolver.getResource(Constant.Paths.UPCOMING_ASSESSMENT_PATH+assessment.getUser().getId());
		if (Objects.nonNull(resource)) {
			resolver.delete(resource);
		}
		resolver.commit();
		unscheduleExistingJob(assessment.getUser().getId());	
	}
	
	private void unscheduleExistingJob(String userId) {
		jobManager.getScheduledJobs().stream()
		.filter(job -> StringUtils.equals(job.getJobTopic(), SlingJob.ASSESSMENT_EXPIRATION_TOPIC) && StringUtils
				.equals((String) job.getJobProperties().get(GenericConstant.USER_ID), userId))
		.forEach(ScheduledJobInfo::unschedule);
	}
	
	private void createSlingJobForSendingEmailNotification(ResourceResolver resolver,AssessmentReport assessmentReport) {
		Resource resource = resolver.getResource(Constant.Paths.UPCOMING_ASSESSMENT_PATH+assessmentReport.getUserId());
		
		if (Objects.nonNull(resource)) {
			Map<String,Object> payload = new HashMap<>();
			ValueMap valueMap = resource.adaptTo(ValueMap.class);
			String[] leadsEmail = (String[]) valueMap.get("leadsEmail");
			String recruiter = (String) valueMap.get("recruiter");
			payload.put("leadsEmail", leadsEmail);
			payload.put("recruiter", recruiter);
			payload.put("email", assessmentReport.getUserEmail());
			payload.put("firstName", assessmentReport.getUserFirstName());
			payload.put("lastName", assessmentReport.getUserLastName());
			payload.put("score", assessmentReport.getPercentageObtained());
			payload.put("status", assessmentReport.isPassed());
			jobManager.addJob(SlingJob.ASSESSMENT_REPORT_EMAIL_TOPIC,payload);	
		}
		
	}
	
	
	private String getRequestBody(SlingHttpServletRequest request) throws IOException {
		try (BufferedReader reader = request.getReader()) {
			return reader.lines().collect(Collectors.joining());
		}
	}

	

}
