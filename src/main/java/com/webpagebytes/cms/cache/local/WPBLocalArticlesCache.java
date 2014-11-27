package com.webpagebytes.cms.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBArticlesCache;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalArticlesCache implements WPBArticlesCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBArticle> localCache;
	private static final Object lock = new Object();
	public WPBLocalArticlesCache()
	{
		dataStorage = WPBAdminDataStorageFactory.getInstance();
		try
		{
			if (dataStorage != null)
			{
				Refresh();
			}
		} catch (WPBIOException e)
		{
			
		}
	}
	public WPBArticle getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCache == null)
		{
			Refresh();
		}
		if (localCache != null)
		{
			return localCache.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBArticle> tempMap = new HashMap<String, WPBArticle>();
			List<WPBArticle> recList = dataStorage.getAllRecords(WPBArticle.class);
			for(WPBArticle item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}
}
