package com.sunlife.web.cms.core.jobs;

import java.util.Objects;

import javax.jcr.RepositoryException;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.event.jobs.Job;
import org.apache.sling.event.jobs.consumer.JobConsumer;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
import com.sunlife.web.cms.core.constants.Constant.SlingJob;
import com.sunlife.web.cms.core.services.ResourceResolverService;
import com.sunlife.web.cms.core.services.UserService;

@Component(service = JobConsumer.class, immediate = true, property = {
		JobConsumer.PROPERTY_TOPICS + "=" + SlingJob.ASSESSMENT_EXPIRATION_TOPIC })
public class AssessmentExpirationJobConsumer implements JobConsumer {
	private static final Logger LOGGER = LoggerFactory.getLogger(AssessmentExpirationJobConsumer.class);

	@Reference
	private UserService userService;
	@Reference
	private ResourceResolverService resolverService;

	@Override
	@SuppressWarnings("unchecked")
	public JobResult process(Job job) {
		String assessmentPath = (String) job.getProperty(GenericConstant.ASSESSMENT_PATH);
		String userId = (String) job.getProperty(GenericConstant.USER_ID);
		try (ResourceResolver resolver = resolverService
				.getResourceResolver(Constant.ServiceUser.SKILLASSESSMENT_USER)) {

			userService.deleteUser(resolver, userId);
			Resource resource = resolver.getResource(assessmentPath);
			if (Objects.nonNull(resource)) {
				resolver.delete(resource);
			}
			resolver.commit();

		} catch (LoginException | RepositoryException | PersistenceException e) {
			LOGGER.error("Error while executing job : {}", e.getMessage());
		}
		return JobResult.OK;
	}

}
