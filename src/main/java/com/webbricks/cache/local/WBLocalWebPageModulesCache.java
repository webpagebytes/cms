package com.webbricks.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webbricks.cache.WBWebPageModulesCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalWebPageModulesCache implements WBWebPageModulesCache {
	
	private AdminDataStorage dataStorage;
	private Map<String, WBWebPageModule> localCache;
	private static final Object lock = new Object();
	public WBLocalWebPageModulesCache()
	{
		dataStorage = AdminDataStorageFactory.getInstance();
		try
		{
			Refresh();
		} catch (WBIOException e)
		{
			
		}
	}
	public WBWebPageModule getByExternalKey(String externalKey) throws WBIOException
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
			Map<String, WBWebPageModule> tempMap = new HashMap<String, WBWebPageModule>();
			List<WBWebPageModule> recList = dataStorage.getAllRecords(WBWebPageModule.class);
			for(WBWebPageModule item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}

		
	public WBWebPageModule get(String pageName) throws WBIOException
	{
		return null;
	}
	
}
