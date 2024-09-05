package com.sunlife.web.cms.core.models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.models.annotations.Default;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;
import org.apache.sling.models.annotations.injectorspecific.ScriptVariable;
import org.apache.sling.models.annotations.injectorspecific.ValueMapValue;

import com.day.cq.commons.jcr.JcrConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunlife.web.cms.core.beans.AssessmentTemplate;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.services.ContentFragmentService;

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)

public class AssessmentModel {

	@OSGiService
	private ContentFragmentService cfService;

	private List<AssessmentTemplate> assessmentList;

	@ScriptVariable
	private SlingHttpServletRequest request;

	private String technologyTitle;

	@ValueMapValue
	private String technology;
	@ValueMapValue
	private String duration;
	@ValueMapValue
	@Default(values="false")
	private String showResult;

	@PostConstruct
	protected void init() {
		if (StringUtils.isEmpty(technology)) {

			assessmentList = new ArrayList<>();
		} else {
			List<Map<String, Object>> contentFragmentsData = cfService.getContentFragments(
					Paths.BASE_CONTENT_FRAGMENT_FOLDER_PATH + technology, Paths.ASSESSMENT_MODEL_PATH);
			ObjectMapper mapper = new ObjectMapper();
			assessmentList = mapper.convertValue(contentFragmentsData,
					mapper.getTypeFactory().constructCollectionType(List.class, AssessmentTemplate.class));

			ResourceResolver resourceResolver = request.getResourceResolver();
			Resource resource = resourceResolver.getResource(Constant.Paths.TECHNOLOGY_GENERIC_LIST_PATH);
			if (Objects.nonNull(resource)) {
				technologyTitle = findTitleForTechnologyValue(technology, resource);
			}
			if (StringUtils.isNotEmpty(duration)) {
				duration = String.valueOf(Integer.valueOf(duration) * 60);
			}

		}

	}

	private String findTitleForTechnologyValue(String value, Resource resource) {
		Iterator<Resource> resourceItr = resource.listChildren();
		String title = StringUtils.EMPTY;
		while (resourceItr.hasNext()) {
			Resource child = resourceItr.next();
			String resouceValue = child.getValueMap().get("value", String.class);
			if (StringUtils.equals(value, resouceValue)) {
				title = child.getValueMap().get(JcrConstants.JCR_TITLE, String.class);
				break;
			}
		}
		return title;
	}

	public List<AssessmentTemplate> getAssessments() {
		return assessmentList;
	}

	public String getTechnology() {
		return technology;
	}

	public String getTechnologyTitle() {
		return technologyTitle;
	}

	public String getDuration() {
		return duration;
	}

	public String showResult() {
		return showResult;
	}

}
