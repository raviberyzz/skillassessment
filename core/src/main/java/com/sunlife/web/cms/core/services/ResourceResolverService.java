package com.sunlife.web.cms.core.services;


import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author Ravi Ranjan
 */
public interface ResourceResolverService {
	
	/**
	 * Method returns the ${@link ResourceResolver} instance for the given subservice username.
	 * @param serviceusername
	 * @return
	 * @throws LoginException
	 */
	 public  ResourceResolver getResourceResolver(String serviceusername) throws LoginException;

}
