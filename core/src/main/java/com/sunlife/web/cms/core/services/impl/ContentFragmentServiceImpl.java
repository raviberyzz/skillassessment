package com.sunlife.web.cms.core.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.jcr.query.Query;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.adobe.cq.dam.cfm.ContentElement;
import com.adobe.cq.dam.cfm.ContentFragment;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
import com.sunlife.web.cms.core.constants.Constant.SQL2Query;
import com.sunlife.web.cms.core.services.ContentFragmentService;
import com.sunlife.web.cms.core.services.ResourceResolverService;

@Component(service = ContentFragmentService.class, immediate = true)
public class ContentFragmentServiceImpl implements ContentFragmentService {
	
	  private static final Logger LOGGER = LoggerFactory.getLogger(ContentFragmentServiceImpl.class);

	@Reference
	private ResourceResolverService resourceResolverService;

	@Override
	public List<Map<String, Object>> getContentFragments(String folderPath, String modelPath) {

		List<Map<String, Object>>dataList = new ArrayList<>();
		try (ResourceResolver resolver = resourceResolverService.getResourceResolver(Constant.ServiceUser.SKILLASSESSMENT_USER)) {
			String sql2Query = SQL2Query.CONTENT_FRAGMENT_BY_PATH_MODEL;
			Iterator<Resource> resources = resolver.findResources(String.format(sql2Query, folderPath, modelPath),
					Query.JCR_SQL2);
			while (resources.hasNext()) {
				Resource resource = resources.next();
				ContentFragment contentFragment = resource.adaptTo(ContentFragment.class);
				Iterator<ContentElement> elements = contentFragment.getElements();
				Map<String, Object> dataMap = new HashMap<>();
				while (elements.hasNext()) {
					ContentElement contentElement = elements.next();
					dataMap.put(contentElement.getName(), contentElement.getValue().getValue());
				}
				dataMap.put(GenericConstant.ID, contentFragment.getName());
				dataList.add(dataMap);
			}

		} catch (LoginException e) {
			LOGGER.error("Error while fetching content fragments",e.getMessage());
		}

		return dataList;
	}

}
