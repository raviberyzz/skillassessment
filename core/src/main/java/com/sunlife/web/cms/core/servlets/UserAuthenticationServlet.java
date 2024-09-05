package com.sunlife.web.cms.core.servlets;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;

import org.apache.commons.lang.StringUtils;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.servlets.annotations.SlingServletPaths;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.propertytypes.ServiceDescription;

import com.google.gson.Gson;

@Component(service = { Servlet.class })
@SlingServletPaths(value = "/bin/authenticate")
@ServiceDescription("User Authentication Service")
public class UserAuthenticationServlet extends SlingAllMethodsServlet {

	private static final long serialVersionUID = 1L;
	@Reference
	private HttpClientBuilderFactory httpClientBuilderFactory;

	@Override
	protected void doGet(final SlingHttpServletRequest request, final SlingHttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("application/json");
		Map<String, Object> responseMap = new HashMap<>();
		Cookie cookie = request.getCookie("login-token");
		if (Objects.nonNull(cookie) && StringUtils.isNotEmpty(cookie.getValue())) {
			resp.setStatus(200);
			responseMap.put("authenticated", true);

		} else {
			resp.setStatus(401);
			responseMap.put("authenticated", false);
		}
		resp.getWriter().println(new Gson().toJson(responseMap));

	}

}
