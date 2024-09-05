package com.sunlife.web.cms.core.services;

import java.util.Map;

import javax.jcr.RepositoryException;

import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.sling.api.resource.ResourceResolver;

/**
 * @author Ravi Ranjan
 */
public interface UserService {

	/**
	 * Method is used to create user in the AEM JCR repository for the given userid.
	 * 
	 * @param resolver instance of {@link ResourceResolver}
	 * @param userId   userid in the form of string
	 * @return user login credentials in the form of key value pair. If user didn't
	 *         got created then null value will be returned.
	 * @throws AuthorizableExistsException
	 * @throws RepositoryException
	 */
	public Map<String, String> createUser(ResourceResolver resolver, String userId)
			throws AuthorizableExistsException, RepositoryException;

	/**
	 * Method checks if the given principle name is the member of recruiters group
	 * 
	 * @param resolver      instance of {@link Resource Resolver}
	 * @param principleName principle name of the user
	 * @return true if the principle is member of recruiters user group.
	 * @throws RepositoryException
	 */
	public boolean isRecruiter(ResourceResolver resolver, String principleName) throws RepositoryException;

	/**
	 * Method checks if the given principle name is the member of
	 * 'assessmentparticipants' group
	 * 
	 * @param resolver      instance of {@link Resource Resolver}
	 * @param principleName principle name of the user
	 * @return true if the principle is member of 'assessmentparticipants' user
	 *         group.
	 * @throws RepositoryException
	 */
	public boolean isParticipant(ResourceResolver resolver, String principleName) throws RepositoryException;

	/**
	 * Method deletes user from the aem jcr repository.
	 * 
	 * @param resourceResolver instance of {@link Resource Resolver}
	 * @param userId           userid of the user
	 * @return true if deleted successfully
	 * @throws RepositoryException
	 */
	public boolean deleteUser(ResourceResolver resourceResolver, String userId) throws RepositoryException;

}
