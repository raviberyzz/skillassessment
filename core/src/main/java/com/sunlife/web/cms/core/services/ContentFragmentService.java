package com.sunlife.web.cms.core.services;

import java.util.List;
import java.util.Map;

import org.apache.sling.api.resource.ResourceResolver;

public interface ContentFragmentService {
	
	public List<Map<String, Object>> getContentFragments(String folderPath,String modelPath);

}
