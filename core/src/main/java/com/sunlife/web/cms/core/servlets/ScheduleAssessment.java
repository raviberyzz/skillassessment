package com.sunlife.web.cms.core.servlets;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.jcr.RepositoryException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.event.jobs.JobManager;
import org.apache.sling.event.jobs.ScheduledJobInfo;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.apache.sling.settings.SlingSettingsService;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.sunlife.web.cms.core.beans.Participants;
import com.sunlife.web.cms.core.beans.User;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.Extensions;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.constants.Constant.ResponseConstants;
import com.sunlife.web.cms.core.constants.Constant.SlingJob;
import com.sunlife.web.cms.core.services.ResourceResolverService;
import com.sunlife.web.cms.core.services.UserService;
import com.sunlife.web.cms.core.utils.RunModeUtil;
import com.sunlife.web.cms.core.utils.SlingResourceUtil;

/**
 * @author Ravi Ranjan
 */
@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/schedule")
@ServiceDescription("Schedule Upcoming Assessment")
public class ScheduleAssessment extends SlingAllMethodsServlet {
	private static final long serialVersionUID = 1L;

	@Reference
	private ResourceResolverService resolverService;
	@Reference
	private UserService userService;
	@Reference
	private JobManager jobManager;
	@Reference
	private SlingSettingsService slingSettingService;
	
	private static final int ASSESSMENT_EXPIRATION_HOURS=24;

	@Override
	protected void doPost(final SlingHttpServletRequest request, final SlingHttpServletResponse resp)
			throws ServletException, IOException {

		resp.setContentType(ResponseConstants.JSON_RESPONSE_TYPE);
		Map<String, Object> responseMap = new HashMap<>();
		String body = getRequestBody(request);

		Participants participants = new ObjectMapper().readValue(body, Participants.class);

		if (Objects.nonNull(participants) && isValidPayload(participants)) {
			List<User> users = participants.getParticipants();
			List<User> failedUsers = new ArrayList<>();

			try (ResourceResolver resolver = resolverService
					.getResourceResolver(Constant.ServiceUser.SKILLASSESSMENT_USER)) {
				 // Get the current date and time
		        Calendar calendar = Calendar.getInstance();
		        // Add 24 hours to the current time
		        calendar.add(Calendar.HOUR_OF_DAY, ASSESSMENT_EXPIRATION_HOURS);
		        Date baseDate = calendar.getTime();
		        
		       String technologyPageUrl = getTechnologyPageUrl(resolver,participants.getTechnology());

				users.forEach(user -> {
					String upcomingAssessmentPath = Constant.Paths.UPCOMING_ASSESSMENT_PATH + user.getEmail();
					Map<String, Object> upcomingAssessmentInfo = getParticipantInfo(participants, user);

					try {
						Map<String, String> credentials = userService.createUser(resolver, user.getEmail());
						if (Objects.nonNull(credentials)) {
							upcomingAssessmentInfo.put("creds", credentials.get("pwd"));
						}

						upcomingAssessmentInfo.put(JcrConstants.JCR_PRIMARYTYPE, JcrConstants.NT_UNSTRUCTURED);
						Resource resource = resolver.getResource(upcomingAssessmentPath);
						if (Objects.nonNull(resource)) {

							SlingResourceUtil.updateResource(resolver, resource, upcomingAssessmentInfo);

						} else {
							
							SlingResourceUtil.getOrCreateResource(resolver, upcomingAssessmentPath,
									upcomingAssessmentInfo);
						}

						Map<String, Object> properties = new HashMap<>();
						properties.put(GenericConstant.USER_ID, user.getId());
						properties.put(GenericConstant.ASSESSMENT_PATH, upcomingAssessmentPath);

						createAssessmentExpirationJob(jobManager, properties, baseDate, users.indexOf(user) + 1);
						//create email job
						if(Objects.nonNull(credentials)) {
						createEmailNotificationSlingJob(user,participants,credentials.get("pwd"),technologyPageUrl);
						}
						
						
						
					} catch (PersistenceException | RepositoryException e) {
						failedUsers.add(user);
					}
				});

				if (!failedUsers.isEmpty()) {
					responseMap.put("failedUsers", failedUsers);
					responseMap.put(ResponseConstants.MESSAGE, "User creation failed for some users");
				} else {
					responseMap.put(ResponseConstants.MESSAGE, "User creation successful");
				}
			} catch (LoginException e) {
				resp.setStatus(500);
				responseMap.put(ResponseConstants.MESSAGE, "Failed to get service user");
			}
		} else {
			resp.setStatus(400);
			responseMap.put(ResponseConstants.MESSAGE, "Incorrect/missing payload");
		}
		resp.getWriter().println(new Gson().toJson(responseMap));
	}
	
	public void createEmailNotificationSlingJob(User user, Participants participants,String pwd,String pageUrl) {
		Map<String,Object> jobProperties = new HashMap<>();
		jobProperties.put("firstName", user.getFirstName());
		jobProperties.put("lastName", user.getLastName());
		jobProperties.put("email", user.getEmail());
		jobProperties.put("leadsEmail", participants.getLeadsEmail());
		jobProperties.put("recruiter", participants.getRecruiter());
		jobProperties.put("crendential", pwd);
		jobProperties.put("assessmentUrl", pageUrl);
		jobManager.addJob(SlingJob.ASSESSMENT_SCHEDULE_EMAIL_TOPIC, jobProperties);
		
	}
	
	
	public String getTechnologyPageUrl(ResourceResolver resolver, String technology) {
		String domain = RunModeUtil.getDomain(resolver, slingSettingService);
		return domain + Paths.LOGIN_PAGE_URL + Extensions.HTML;
	}

	private void createAssessmentExpirationJob(JobManager jobManager, Map<String, Object> properties, Date baseDate,
			int count) {
		jobManager.getScheduledJobs().stream()
				.filter(job -> StringUtils.equals(job.getJobTopic(), SlingJob.ASSESSMENT_EXPIRATION_TOPIC) && StringUtils
						.equals((String) job.getJobProperties().get(GenericConstant.USER_ID), (String) properties.get(GenericConstant.USER_ID)))
				.forEach(ScheduledJobInfo::unschedule);

		jobManager.createJob(SlingJob.ASSESSMENT_EXPIRATION_TOPIC).properties(properties).schedule()
				.at(getDateIncrementedByMinutes(baseDate, count)).add();
	}

	/**
	 * Method is used to get the 24 hrs date incremented by a minute. This is done
	 * to prevent jcr repository persistance exception if two sling jobs tried to
	 * commit in jcr at the same time.
	 * 
	 * @param baseDate
	 * @param minutesToAdd
	 * @return
	 */
	private Date getDateIncrementedByMinutes(Date baseDate, int minutesToAdd) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(baseDate);
		calendar.add(Calendar.MINUTE, minutesToAdd);
		return calendar.getTime();
	}

	private boolean isValidPayload(Participants participants) {
		return !Objects.isNull(participants.getLeadsEmail()) && participants.getLeadsEmail().length > 0
				|| !participants.getParticipants().isEmpty() || StringUtils.isNotBlank(participants.getTechnology());
	}

	@SuppressWarnings("serial")
	private Map<String, Object> getParticipantInfo(Participants participants, User user) {
		return new HashMap<String, Object>() {
			{
				put("firstName", user.getFirstName());
				put("lastName", user.getLastName());
				put("email", user.getEmail());
				put("leadsEmail", participants.getLeadsEmail());
				put("recruiter", participants.getRecruiter());
				put("technology", participants.getTechnology());
			}
		};
	}

	private String getRequestBody(SlingHttpServletRequest request) throws IOException {
		try (BufferedReader reader = request.getReader()) {
			return reader.lines().collect(Collectors.joining());
		}
	}
}