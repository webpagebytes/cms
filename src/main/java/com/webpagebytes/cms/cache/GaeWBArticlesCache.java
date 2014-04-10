package com.webpagebytes.cms.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.GaeAdminDataStorage;
import com.webpagebytes.cms.exception.WBIOException;

public class GaeWBArticlesCache implements WBArticlesCache, WBRefreshableCache{

	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBArticle";
	private static final String memcacheMapKey = "keyToWBArticle";
	
	private AdminDataStorage adminDataStorage = null;

	public GaeWBArticlesCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();
	}
	
	private void RefreshInternal(Map<String, WBArticle> keyMap) throws WBIOException
	{
		synchronized (this) {
			List<WBArticle> records = adminDataStorage.getAllRecords(WBArticle.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<String, WBArticle>();
			}
			for (WBArticle rec : records)
			{
				keyMap.put(rec.getExternalKey(), rec);
			}
			memcache.put(memcacheMapKey, keyMap);
		}
	}

	public void Refresh() throws WBIOException
	{
		RefreshInternal(null);
	}

	public synchronized WBArticle getByExternalKey(String externalKey) throws WBIOException
	{
		HashMap<String, WBArticle> mapkeys = (HashMap<String, WBArticle>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBArticle) mapkeys.get(externalKey);
		}
		Map<String, WBArticle> refreshData = new HashMap<String, WBArticle>(); 
		RefreshInternal(refreshData);
		if (refreshData.containsKey(externalKey))
		{
			return refreshData.get(externalKey);
		}		
		return null;
	}
}
