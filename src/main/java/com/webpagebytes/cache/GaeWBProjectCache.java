package com.webpagebytes.cache;

import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webpagebytes.cms.Pair;
import com.webpagebytes.cmsdata.WBProject;
import com.webpagebytes.datautility.AdminDataStorage;
import com.webpagebytes.datautility.GaeAdminDataStorage;
import com.webpagebytes.exception.WBIOException;

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
	public Set<String> getSupportedLanguages() throws WBIOException
	{
		WBProject project = (WBProject)memcache.get(memcacheProjectKey);
		if (project == null)
		{
			project = RefreshInternal();
		}
		Set<String> result = project.getSupportedLanguagesSet();
		return result;
	}
	public Pair<String, String> getDefaultLocale() throws WBIOException
	{
		WBProject project = (WBProject)memcache.get(memcacheProjectKey);
		if (project == null)
		{
			project = RefreshInternal();
		}
		Pair<String, String> result = new Pair<String, String>();
		String defaultLanguage = project.getDefaultLanguage();
		String[] langs_ = defaultLanguage.split("_");
		if (langs_.length == 1)
		{
			return new Pair<String, String>(langs_[0], "");
		} else
		if (langs_.length == 2)
		{
			return new Pair<String, String>(langs_[0], langs_[1]);
		} else
			throw new WBIOException("Invalid default language");		
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
