package com.sunlife.web.cms.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.settings.SlingSettingsService;

import com.day.cq.commons.Externalizer;
import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.constants.Constant.GenericConstant;
/**
 * This class provides information of Instance RunMode.
 * 
 * @author Ravi Ranjan
 *
 */
public class RunModeUtil {
	
	private RunModeUtil() {
		throw new IllegalStateException("This is a utility class and cannot be instantiated");
	}

	  /**
	   * Determines if the current AEM instance has the 'author' runmode enabled.
	   *
	   * @return TRUE when the 'author' runmode is enabled.
	   */
	  public static boolean isAuthorMode(SlingSettingsService settingsService) {
	    if (settingsService == null || settingsService.getRunModes() == null) {
	      return false;
	    }
	    return settingsService.getRunModes().contains(Externalizer.AUTHOR);
	  }

	  /**
	   * Determines if the current AEM instance has the 'publish' runmode enabled.
	   *
	   * @return TRUE when the 'publish' runmode is enabled.
	   */
	  public static boolean isPublishMode(SlingSettingsService settingsService) {
	    if (settingsService == null || settingsService.getRunModes() == null) {
	      return false;
	    }
	    return settingsService.getRunModes().contains(Externalizer.PUBLISH);
	  }

	  /**
	   * Determines if the current AEM instance has the 'local' runmode enabled.
	   *
	   * @return TRUE when the 'local' runmode is enabled.
	   */
	  public static boolean isLocalMode(SlingSettingsService settingsService) {
	    if (settingsService == null || settingsService.getRunModes() == null) {
	      return false;
	    }
	    return settingsService.getRunModes().contains(Externalizer.LOCAL);
	  }
	  
		/**
		 * Provides the domain (http://host:port/) of the aem instance by fetching it
		 * from externalizer configuration.
		 * 
		 * @param resolver        the instance of {@link ResourceResolver}
		 * @param settingsService the instance of {@link SlingSettingsService}
		 * @return domain url
		 */
		public static String getDomain(ResourceResolver resolver, SlingSettingsService settingsService) {
			Externalizer externalizer = resolver.adaptTo(Externalizer.class);
			return isAuthorMode(settingsService)
					? (externalizer.authorLink(null, StringUtils.EMPTY) + GenericConstant.FORWARD_SLASH)
					: (externalizer.publishLink(null, StringUtils.EMPTY) + GenericConstant.FORWARD_SLASH);
		}
	}