package com.sunlife.web.cms.core.jobs;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import org.apache.commons.lang.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunlife.web.cms.core.constants.Constant.EmailConstants;
import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.constants.Constant.ServiceUser;
import com.sunlife.web.cms.core.constants.Constant.SlingJob;
import com.sunlife.web.cms.core.services.CustomEmailService;
import com.sunlife.web.cms.core.services.ResourceResolverService;

@Component(service = JobConsumer.class, immediate = true, property = {
		JobConsumer.PROPERTY_TOPICS + "=" + SlingJob.ASSESSMENT_SCHEDULE_EMAIL_TOPIC })
public class AssessmentScheduleEmailJobConsumer implements JobConsumer{
	
	private final Logger logger = LoggerFactory.getLogger(getClass());
	
	@Reference
	private ResourceResolverService resolverService;
	
	@Reference
	private CustomEmailService emailService;
	private static final String SUBJECT="Assessment Invitation";

	@Override
	@SuppressWarnings("resource")
	public JobResult process(Job job) {
		
		String firstName = (String) job.getProperty("firstName");
		String lastName = (String) job.getProperty("lastName");
		String participantEmail = (String) job.getProperty("email");
		String[] receipientsEmail = (String[]) job.getProperty("leadsEmail");
		String recruiter = (String) job.getProperty("recruiter");
		String credential = (String) job.getProperty("crendential");
		String assessmentUrl = (String) job.getProperty("assessmentUrl");
		
		String receipients = StringUtils.EMPTY;
		for (int i = 0; i < receipientsEmail.length; i++) {
			receipients = receipients.concat(receipientsEmail[i] + ";");
		}
		receipients = receipients.concat(recruiter + ";");

		
		try (ResourceResolver resolver = resolverService.getResourceResolver(ServiceUser.SKILLASSESSMENT_USER)) {
			Resource emailTemplateResource = resolver.getResource(Paths.ASSESSMENT_SCHEDULE_EMAIL_TEMPLATE_PATH);
			
			Map<String, String> emailVariablesMap = new HashMap<>();
			emailVariablesMap.put("${firstName}", firstName);
			emailVariablesMap.put("${lastName}", lastName);
			emailVariablesMap.put("${username}", participantEmail);
			emailVariablesMap.put("${password}", credential);
			emailVariablesMap.put("${assessmentUrl}", assessmentUrl);
			
			if (Objects.nonNull(emailTemplateResource)) {
				Node jcnode = emailTemplateResource.adaptTo(Node.class).getNode("jcr:content");
				InputStream inputStream = jcnode.getProperty("jcr:data").getBinary().getStream();
			
				String fileContent = new BufferedReader(new InputStreamReader(inputStream)).lines()
						.collect(Collectors.joining("\n"));
				for (Map.Entry<String, String> entry : emailVariablesMap.entrySet()) {
					fileContent = fileContent.replace(entry.getKey(), entry.getValue());
				}
				Map<String,String> emailProperties = setEmail(participantEmail, receipients, SUBJECT, fileContent);
				 emailService.sendEmail(emailProperties);
				
			}
			
		} catch (LoginException | RepositoryException e) {
			logger.error("Error while sending email: {}",e.getMessage());
		}
		
		 return JobResult.OK;
	}
	
	private Map<String, String> setEmail(String participantEmail, String ccEmail, String emailSubject, String body) {
		Map<String, String> payload = new HashMap<>();
		payload.put(EmailConstants.FROM_EMAIL_ADDRESS, "do-not-reply@sunlife.com");
		payload.put(EmailConstants.TO_EMAIL_ADDRES, participantEmail);
		payload.put(EmailConstants.CC_EMAIL_ADDRES, ccEmail);
		payload.put(EmailConstants.BCC_EMAIL_ADDRES, StringUtils.EMPTY);
		payload.put(EmailConstants.EMAIL_SUBJECT, emailSubject);
		payload.put(EmailConstants.EMAIL_BODY, body);
		return payload;

	}

}
