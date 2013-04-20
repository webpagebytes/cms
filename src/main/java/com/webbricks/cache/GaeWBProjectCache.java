package com.webbricks.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBProject;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBProjectCache implements WBProjectCache {
	private static final Logger log = Logger.getLogger(GaeWBProjectCache.class.getName());
	
	AdminDataStorage adminStorage;
	private MemcacheService memcache;
	
	private static final String memcacheNamespace = "cacheWBProject";
	private static final String memcacheProjectKey = "projectid";
	
	public GaeWBProjectCache()
	{
		adminStorage = new GaeAdminDataStorage();
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
	}
	
	public String getDefaultLanguage() throws WBIOException
	{
		WBProject project = (WBProject)memcache.get(memcacheProjectKey);
		if (project == null)
		{
			project = RefreshInternal();
		}
		return project.getDefaultLanguage();
	}
	public Set<String> getSupportedLanguage() throws WBIOException
	{
		WBProject project = (WBProject)memcache.get(memcacheProjectKey);
		if (project == null)
		{
			project = RefreshInternal();
		}
		Set<String> result = new HashSet<String>();
		String languagesStr = project.getSupportedLanguages();
		String[] languages = languagesStr.split(",");
		for (String language: languages)
		{
			if (language.length()>0)
			{
				result.add(language);
			}
		}
		return result;
	}
	
	public void Refresh() throws WBIOException {
		RefreshInternal();
	}
	
	protected WBProject RefreshInternal() throws WBIOException
	{
		log.log(Level.INFO, "GaeWBProjectCache:RefreshInternal");
		WBProject project = (WBProject) adminStorage.get(WBProject.PROJECT_KEY, WBProject.class);
		memcache.put(memcacheProjectKey, project);		
		return project;
	}
	
	public WBProject getProject() throws WBIOException
	{
		WBProject project = (WBProject)memcache.get(memcacheProjectKey);
		if (project == null)
		{
			project = RefreshInternal();
		}
		return project;
	}
}
