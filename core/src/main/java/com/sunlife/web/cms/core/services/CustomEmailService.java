package com.sunlife.web.cms.core.services;

import java.util.Map;

/**
 * @author Ravi Ranjan
 */
public interface CustomEmailService {

	/**
	 * This method is used to send email via invoking Sunlife's external email api.
	 * The API is hosted on different server and managed by different team within
	 * sunlife.
	 * 
	 * @param emailParams email parameters in the form of key value pairs.
	 * @return email response in the form of key value pair.
	 */
	public Map<String, Object> sendEmail(Map<String, String> emailParams);

}
