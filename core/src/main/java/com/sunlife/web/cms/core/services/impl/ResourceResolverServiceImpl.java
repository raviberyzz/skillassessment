package com.sunlife.web.cms.core.services.impl;

import java.util.Collections;

import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.sunlife.web.cms.core.services.ResourceResolverService;

@Component(service=ResourceResolverService.class,immediate=true)
public class ResourceResolverServiceImpl implements ResourceResolverService {

	 @Reference
	  private ResourceResolverFactory resourceResolverFactory;

	@Override
	public ResourceResolver getResourceResolver(String serviceUserName) throws LoginException {
		return resourceResolverFactory.getServiceResourceResolver(Collections.singletonMap(ResourceResolverFactory.SUBSERVICE, serviceUserName));
	}

}
