package com.sunlife.web.cms.core.services;

import java.util.Map;

public interface CustomEmailService {
	
	public Map<String,Object> sendEmail(Map<String,String> emailParams);

}
