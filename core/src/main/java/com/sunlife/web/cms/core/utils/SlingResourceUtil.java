package com.sunlife.web.cms.core.utils;

import java.util.Map;
import java.util.Objects;

import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;

import com.adobe.aemds.guide.utils.JcrResourceConstants;

import edu.umd.cs.findbugs.annotations.NonNull;

/**
 * Utility class to perform operations on a resource
 * 
 * @author Sunlife
 */
public class SlingResourceUtil {

	private SlingResourceUtil() {
		throw new IllegalStateException("This is a utility class and cannot be instantiated");
	}

	/**
	 * Method creates resource under the given path with given properties
	 * 
	 * @param resolver      instance of {@link ResourceResolver}
	 * @param bindingPath-  folder path under which node should be created
	 * @param jcrProperties map contains properties which will be added to the node
	 * @return created node path
	 * @throws PersistenceException
	 */
	public static Resource getOrCreateResource(ResourceResolver resolver, String bindingPath,
			Map<String, Object> jcrProperties) throws PersistenceException {
		return ResourceUtil.getOrCreateResource(resolver, bindingPath, jcrProperties,
				JcrResourceConstants.NT_SLING_ORDERED_FOLDER, true);
	}

	/**
	 * Method updates the existing Resource with the given properties.
	 * 
	 * @param resolver          instance of {@link ResourceResolver}
	 * @param resourcePath      instance of {@link Resource}
	 * @param updatedProperties instance of {@link Map} having modified jcr
	 *                          properties.
	 * @throws PersistenceException
	 */
	public static void updateResource(@NonNull ResourceResolver resolver, @NonNull Resource resource,
			@NonNull Map<String, Object> updatedProperties) throws PersistenceException {
		ModifiableValueMap valuemap = resource.adaptTo(ModifiableValueMap.class);
		valuemap.putAll(updatedProperties);
		resolver.commit();

	}
}