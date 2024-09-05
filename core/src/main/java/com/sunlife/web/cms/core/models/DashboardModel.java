package com.sunlife.web.cms.core.models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.apache.sling.models.annotations.DefaultInjectionStrategy;
import org.apache.sling.models.annotations.Model;
import org.apache.sling.models.annotations.injectorspecific.OSGiService;

import com.sunlife.web.cms.core.constants.Constant;
import com.sunlife.web.cms.core.services.ResourceResolverService;

@Model(adaptables = { Resource.class,
		SlingHttpServletRequest.class }, defaultInjectionStrategy = DefaultInjectionStrategy.OPTIONAL)
public class DashboardModel {
	
	private List<Map<String,String>>technologies;
	private List<Map<String,String>> leads;
	
	@OSGiService
	private ResourceResolverService resolverService;
	
	@PostConstruct
	protected void init() {
		technologies = new ArrayList<>();
		leads = new ArrayList<>();
		try(ResourceResolver resolver = resolverService.getResourceResolver(Constant.ServiceUser.SKILLASSESSMENT_USER)){
			Resource resource = resolver.getResource(Constant.Paths.TECHNOLOGY_GENERIC_LIST_PATH);
			Resource leadsResource = resolver.getResource(Constant.Paths.TECHNOLOGY_LEADS_LIST_PATH);
			addValues(technologies,resource);
			addValues(leads,leadsResource);
			
		} catch (LoginException e) {
			
		}
		
	}
	
	public List<Map<String,String>> getLeads(){
		return leads;
	}
	public List<Map<String,String>> getTechnologies(){
		return technologies;
	}
	
	
	private void addValues(List<Map<String,String>> list,Resource resource) {
		if(Objects.nonNull(resource)) {
			
			Iterator<Resource> itr = resource.listChildren();
			while(itr.hasNext()) {
				Map<String,String> map = new HashMap<>();
				ValueMap vmap = itr.next().adaptTo(ValueMap.class);
				String title = vmap.get("jcr:title", String.class);
				String value = vmap.get("value", String.class);
				map.put("title", title);
				map.put("value", value);
				list.add(map);
			}
		}
	}
	
	

}
