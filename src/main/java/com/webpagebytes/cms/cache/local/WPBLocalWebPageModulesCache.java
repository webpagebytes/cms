package com.webpagebytes.cms.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalWebPageModulesCache implements WPBWebPageModulesCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WBWebPageModule> localCacheByID;
	private Map<String, WBWebPageModule> localCacheByName;
	private static final Object lock = new Object();
	public WPBLocalWebPageModulesCache()
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
	public WBWebPageModule getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByID != null)
		{
			return localCacheByID.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WBWebPageModule> tempMapByID = new HashMap<String, WBWebPageModule>();
			Map<String, WBWebPageModule> tempMapByName = new HashMap<String, WBWebPageModule>();
			
			List<WBWebPageModule> recList = dataStorage.getAllRecords(WBWebPageModule.class);
			for(WBWebPageModule item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByID = tempMapByID;
			localCacheByName = tempMapByName;
		}
		
	}

		
	public WBWebPageModule get(String moduleName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(moduleName);
		}
		return null;
	}
	
}
