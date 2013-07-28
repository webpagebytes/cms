package com.webbricks.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBWebPagesCache implements WBWebPagesCache, WBRefreshableCache {
	private static final Logger log = Logger.getLogger(GaeWBWebPagesCache.class.getName());
	
	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBWebPage";
	private static final String memcacheMapKey = "externalKeyToWBWebPage";
	private static final String memcacheMapNames = "nameToWBWebPage";
	private AdminDataStorage adminDataStorage = null;
	
	public GaeWBWebPagesCache() {
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();
	}
	
	public void Refresh() throws WBIOException
	{
		RefreshInternal(null, null);
	}

	private void RefreshInternal(Map<Long, WBWebPage> keyMap, Map<String, WBWebPage> pageNamesMap) throws WBIOException
	{
		synchronized (this) {
			log.log(Level.INFO, "GaeWBWebPageCache:RefreshInternal");
			List<WBWebPage> wbWebPages = adminDataStorage.getAllRecords(WBWebPage.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<Long, WBWebPage>();
			}
			if (pageNamesMap == null)
			{
				pageNamesMap = new HashMap<String, WBWebPage>();
			}
			for (WBWebPage webPage : wbWebPages)
			{
				keyMap.put(webPage.getExternalKey(), webPage);
				pageNamesMap.put(webPage.getName(), webPage);
			}
			memcache.put(memcacheMapKey, keyMap);
			memcache.put(memcacheMapNames, pageNamesMap);
		}
	}

	public synchronized WBWebPage get(Long externalKey) throws WBIOException
	{
		HashMap<Long, WBWebPage> mapkeys = (HashMap<Long, WBWebPage>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBWebPage) mapkeys.get(externalKey);
		}
		Map<Long, WBWebPage> refreshData = new HashMap<Long, WBWebPage>(); 
		RefreshInternal(refreshData, null);
		if (refreshData.containsKey(externalKey))
		{
			return mapkeys.get(externalKey);
		}		
		return null;

	}
	
	public WBWebPage get(String pageName) throws WBIOException
	{
		HashMap<String, WBWebPage> mapNames = (HashMap<String, WBWebPage>) memcache.get(memcacheMapNames);
		if (mapNames != null && mapNames.containsKey(pageName))
		{
			return (WBWebPage) mapNames.get(pageName);
		}
		Map<String, WBWebPage> refreshData = new HashMap<String, WBWebPage>(); 
		RefreshInternal(null, refreshData);
		if (refreshData.containsKey(pageName))
		{
			return refreshData.get(pageName);
		}		
		return null;
	}

}
