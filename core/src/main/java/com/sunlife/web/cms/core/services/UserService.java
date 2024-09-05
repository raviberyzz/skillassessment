package com.sunlife.web.cms.core.services;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.sling.api.resource.ResourceResolver;

public interface UserService {
	
	public Map<String,String> createUser(ResourceResolver resolver, String userId) throws AuthorizableExistsException, RepositoryException ;
	
	public boolean isRecruiter(ResourceResolver resolver, String principleName) throws RepositoryException;
	public boolean isParticipant(ResourceResolver resolver, String principleName) throws RepositoryException;
	public boolean deleteUser(ResourceResolver resourceResolver, String userId) throws RepositoryException;

}
