package com.webbricks.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webbricks.cache.WBArticlesCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalArticlesCache implements WBArticlesCache {
	
	private AdminDataStorage dataStorage;
	private Map<String, WBArticle> localCache;
	private static final Object lock = new Object();
	public WBLocalArticlesCache()
	{
		dataStorage = AdminDataStorageFactory.getInstance();
		try
		{
			Refresh();
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
