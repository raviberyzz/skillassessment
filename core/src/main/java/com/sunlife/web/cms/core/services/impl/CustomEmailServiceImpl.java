package com.sunlife.web.cms.core.services.impl;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.http.util.EntityUtils;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.sunlife.web.cms.core.constants.Constant.EmailConstants;
import com.sunlife.web.cms.core.services.CustomEmailService;

@Component(service = CustomEmailService.class, immediate = true)
public class CustomEmailServiceImpl implements CustomEmailService {

	@Reference
	private HttpClientBuilderFactory httpClientBuilderFactory;
	private static final int TIMEOUT = 5000;
	private static final String API="https://stage-www.sunlife.ca/slfServiceApp/invokeService.wca?service=email&method=email&format=json";
	private static final String API_KEY = "Ng1vE%^Uvdk$aP@E7SepA1N06#lQ!*kLs4siHSFq";

	@Override
	public Map<String, Object> sendEmail(Map<String, String> emailParams) {
		Map<String, Object> responseMap = new HashMap<>();

		HttpClientBuilder builder = httpClientBuilderFactory.newBuilder();
		RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(TIMEOUT).setSocketTimeout(TIMEOUT)
				.build();
		 builder.setDefaultRequestConfig(requestConfig);

		HttpClient client = builder.build();
		HttpPost post = new HttpPost(API);
		List<BasicNameValuePair> apiParameters = new ArrayList<>(1);
		apiParameters.add(new BasicNameValuePair(EmailConstants.FROM_EMAIL_ADDRESS,
				emailParams.get(EmailConstants.FROM_EMAIL_ADDRESS)));
		apiParameters.add(new BasicNameValuePair(EmailConstants.CC_EMAIL_ADDRES,
				emailParams.get(EmailConstants.CC_EMAIL_ADDRES)));
		apiParameters.add(new BasicNameValuePair(EmailConstants.BCC_EMAIL_ADDRES,
				emailParams.get(EmailConstants.BCC_EMAIL_ADDRES)));
		apiParameters.add(new BasicNameValuePair(EmailConstants.TO_EMAIL_ADDRES,
				emailParams.get(EmailConstants.TO_EMAIL_ADDRES)));
		apiParameters.add(
				new BasicNameValuePair(EmailConstants.EMAIL_SUBJECT, emailParams.get(EmailConstants.EMAIL_SUBJECT)));
		apiParameters.add(new BasicNameValuePair(EmailConstants.EMAIL_BODY, Base64.getEncoder()
				.encodeToString(emailParams.get(EmailConstants.EMAIL_BODY).getBytes(StandardCharsets.UTF_8))));
		apiParameters.add(new BasicNameValuePair(EmailConstants.API_KEY, API_KEY));
		post.setEntity(new UrlEncodedFormEntity(apiParameters, StandardCharsets.UTF_8));
		try {
			HttpResponse response = client.execute(post);
			responseMap.put("status", response.getStatusLine().getStatusCode());
			responseMap.put("message", EntityUtils.toString(response.getEntity()));
		} catch (IOException e) {
			responseMap.put("status", 500);
		}

		return responseMap;
	}

}
