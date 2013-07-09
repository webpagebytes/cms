package com.webbricks.cache;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

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
	
	private void RefreshInternal(Map<Long, WBArticle> keyMap) throws WBIOException
	{
		synchronized (this) {
			List<WBArticle> records = adminDataStorage.getAllRecords(WBArticle.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<Long, WBArticle>();
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

	public synchronized WBArticle get(Long externalKey) throws WBIOException
	{
		HashMap<Long, WBArticle> mapkeys = (HashMap<Long, WBArticle>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBArticle) mapkeys.get(externalKey);
		}
		Map<Long, WBArticle> refreshData = new HashMap<Long, WBArticle>(); 
		RefreshInternal(refreshData);
		if (refreshData.containsKey(externalKey))
		{
			return refreshData.get(externalKey);
		}		
		return null;
	}
}
