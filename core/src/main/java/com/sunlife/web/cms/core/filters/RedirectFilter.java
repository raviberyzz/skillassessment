package com.sunlife.web.cms.core.filters;

import java.io.IOException;
import java.util.Objects;

import javax.jcr.RepositoryException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.engine.EngineConstants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.Extensions;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
import com.sunlife.web.cms.core.constants.Constant.Paths;
import com.sunlife.web.cms.core.constants.Constant.ServiceUser;
import com.sunlife.web.cms.core.services.ResourceResolverService;
import com.sunlife.web.cms.core.services.UserService;

import static com.day.cq.wcm.api.NameConstants.NT_PAGE;

@Component(service = Filter.class, property = {
		EngineConstants.SLING_FILTER_SCOPE + "=" + EngineConstants.FILTER_SCOPE_REQUEST,
		EngineConstants.SLING_FILTER_RESOURCETYPES + "=" + NT_PAGE,
		EngineConstants.SLING_FILTER_PATTERN + "=" + "/content/skillassessment/.*",
		EngineConstants.SLING_FILTER_EXTENSIONS + "=" + "html", EngineConstants.SLING_FILTER_METHODS + "=" + "GET",
		"service.ranking" + ":Integer=0" })
public class RedirectFilter implements Filter {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedirectFilter.class);

	

	@Reference
	private ResourceResolverService resolverService;
	@Reference
	private UserService userService;

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {

		final SlingHttpServletResponse slingResponse = (SlingHttpServletResponse) response;
		final SlingHttpServletRequest slingRequest = (SlingHttpServletRequest) request;
		String resourcePath = slingRequest.getRequestPathInfo().getResourcePath();
		if (isLoginPage(resourcePath)) {
			filterChain.doFilter(request, response);
		} else if (isDefaultPage(resourcePath)) {

			String userName = slingRequest.getUserPrincipal().getName();

			try (ResourceResolver resolver = resolverService.getResourceResolver(ServiceUser.SKILLASSESSMENT_USER)) {

				if (userService.isRecruiter(resolver, userName)) {
					String recruiterPage = Paths.RECRUITER_PAGE_URL;
					slingResponse.sendRedirect(recruiterPage);

				} else if (userService.isParticipant(resolver, userName)) {

					// redirect to participant's technology page.
					Resource resource = resolver.getResource(Constant.Paths.UPCOMING_ASSESSMENT_PATH + userName);
					if (Objects.nonNull(resource)) {
						ValueMap vmap = resource.getValueMap();

						String firstName = (String) vmap.getOrDefault(GenericConstant.FIRST_NAME, StringUtils.EMPTY);
						String lastName = (String) vmap.getOrDefault(GenericConstant.LAST_NAME, StringUtils.EMPTY);
						setCookie(slingResponse, firstName, lastName, userName);
						String technology = vmap.get(GenericConstant.TECHNOLOGY, String.class);
						String technologyPage = Paths.BASE_PAGE_PATH + technology + Extensions.HTML;
						slingResponse.sendRedirect(technologyPage);
					}

				} else {
					filterChain.doFilter(slingRequest, slingResponse);
				}

			} catch (RepositoryException | LoginException e) {
				LOGGER.error("Error during filter execution : {}", e.getMessage());
			}

		}

		else {
			filterChain.doFilter(request, response);
		}

	}

	private boolean isLoginPage(String resourcePath) {
		return StringUtils.equals(resourcePath, Paths.LOGIN_PAGE_URL);
	}

	private boolean isDefaultPage(String resourcePath) {
		return StringUtils.equals(resourcePath, Paths.DEFAULT_PAGE);
	}

	private void setCookie(SlingHttpServletResponse response, String firstName, String lastName, String userId) {

		response.addCookie(new Cookie(GenericConstant.FIRST_NAME, firstName));
		response.addCookie(new Cookie(GenericConstant.LAST_NAME, lastName));
		response.addCookie(new Cookie(GenericConstant.EMAIL, userId));
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

}
