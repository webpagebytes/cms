package com.webpagebytes.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cache.WBArticlesCache;
import com.webpagebytes.cmsdata.WBArticle;
import com.webpagebytes.datautility.AdminDataStorage;
import com.webpagebytes.datautility.AdminDataStorageFactory;
import com.webpagebytes.exception.WBIOException;

public class WBLocalArticlesCache implements WBArticlesCache {
	
	private AdminDataStorage dataStorage;
	private Map<String, WBArticle> localCache;
	private static final Object lock = new Object();
	public WBLocalArticlesCache()
	{
		dataStorage = AdminDataStorageFactory.getInstance();
		try
		{
			if (dataStorage != null)
			{
				Refresh();
			}
		} catch (WBIOException e)
		{
			
		}
	}
	public WBArticle getByExternalKey(String externalKey) throws WBIOException
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

	@Override
	public void Refresh() throws WBIOException {
		synchronized (lock)
		{
			Map<String, WBArticle> tempMap = new HashMap<String, WBArticle>();
			List<WBArticle> recList = dataStorage.getAllRecords(WBArticle.class);
			for(WBArticle item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}
}
