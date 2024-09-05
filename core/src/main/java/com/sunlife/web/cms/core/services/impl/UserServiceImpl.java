package com.sunlife.web.cms.core.services.impl;

import java.security.Principal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.apache.commons.lang3.StringUtils;
import org.apache.jackrabbit.api.security.user.Authorizable;
import org.apache.jackrabbit.api.security.user.AuthorizableExistsException;
import org.apache.jackrabbit.api.security.user.Group;
import org.apache.jackrabbit.api.security.user.User;
import org.apache.jackrabbit.api.security.user.UserManager;
import org.apache.sling.api.resource.ResourceResolver;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.services.ResourceResolverService;
import com.sunlife.web.cms.core.services.UserService;
import com.sunlife.web.cms.core.utils.PasswordGeneratorUtil;

@Component(service = UserService.class, immediate = true)
public class UserServiceImpl implements UserService {

	@Reference
	private ResourceResolverService resolverService;

	@Override
	public Map<String, String> createUser(ResourceResolver resolver, String userId)
			throws AuthorizableExistsException, RepositoryException {

		if (Objects.nonNull(resolver)) {

			UserManager userManager = resolver.adaptTo(UserManager.class);
			Session session = resolver.adaptTo(Session.class);
			if (Objects.nonNull(userManager)) {
				String password = PasswordGeneratorUtil.generatePassword();
				Authorizable userAuthorizable = userManager.getAuthorizable(userId);
				Group groupAuthorizable = (Group) userManager.getAuthorizable("assessmentparticipants");
				if (userAuthorizable == null && groupAuthorizable != null) {
					Principal principal = new Principal() {
						@Override
						public String getName() {
							return userId;
						}
					};
					
					User user = userManager.createUser(userId, password, principal, Paths.PARTICIPANT_USRES_PATH);
					
					groupAuthorizable.addMember(user);
					session.save();
					Map<String, String> userMap = new HashMap<>();
					userMap.put("id", user.getID());
					userMap.put("pwd", password);
					return userMap;

				}

			}
		}
		return null;
	}
	
	public boolean deleteUser(ResourceResolver resourceResolver, String userId) throws RepositoryException {
		if (Objects.nonNull(resourceResolver) && StringUtils.isNotEmpty(userId)) {
			Session session = resourceResolver.adaptTo(Session.class);
			UserManager userManager = resourceResolver.adaptTo(UserManager.class);
			Authorizable userAuthorizable = userManager.getAuthorizable(userId);
			if (Objects.nonNull(userAuthorizable)) {
				User user = (User) userAuthorizable;
				user.remove();
				session.save();
				return true;
			}
		}
		return false;
	}
	

	@Override
	public boolean isRecruiter(ResourceResolver resolver, String principleName) throws RepositoryException {

		return isMemberOf(resolver, principleName, "recruiters");
	}

	@Override
	public boolean isParticipant(ResourceResolver resolver, String principleName) throws RepositoryException {
		return isMemberOf(resolver, principleName, "assessmentparticipants");

	}

	private boolean isMemberOf(ResourceResolver resolver, String principleName, String groupName)
			throws RepositoryException {
		UserManager userManager = resolver.adaptTo(UserManager.class);
		if (Objects.nonNull(userManager)) {
			User user = (User) userManager.getAuthorizable(principleName);
			user.getCredentials();
			Iterator<Group> groupIterator = user.memberOf();
			while (groupIterator.hasNext()) {
				String groupId = groupIterator.next().getID();
				if (StringUtils.equals(groupId, groupName)) {
					return true;
				}
			}
		}
		return false;
	}
}
