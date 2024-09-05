package com.sunlife.web.cms.core.services;

import java.util.List;
import java.util.Map;

/**
 * @author Ravi Ranjan
 */
public interface ContentFragmentService {

	/**
	 * Method is used to fetch content fragments data in the form of {@link Map}
	 * object. Implementation uses JCR-SQL2 query to query the content fragments
	 * 
	 * @param folderPath folder path in jcr repository where content fragments are
	 *                   stored.
	 * @param modelPath  content-fragment model path which was used to create
	 *                   content fragment.
	 * @return list of {@link Map} instances containing content-fragment data in the
	 *         form of key value pair.
	 */
	public List<Map<String, Object>> getContentFragments(String folderPath, String modelPath);

}
