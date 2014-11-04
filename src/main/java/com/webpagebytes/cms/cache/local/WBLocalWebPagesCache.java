package com.webpagebytes.cms.cache.local;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.exception.WBIOException;

public class WBLocalWebPagesCache implements WBWebPagesCache {
	private AdminDataStorage dataStorage;
	private Map<String, WBWebPage> localCacheByExternalId;
	private Map<String, WBWebPage> localCacheByName;	
	private static final Object lock = new Object();
	public WBLocalWebPagesCache()
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
	public WBWebPage getByExternalKey(String externalKey) throws WBIOException
	{
		if (localCacheByExternalId != null)
		{
			return localCacheByExternalId.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WBIOException {
		synchronized (lock)
		{
			Map<String, WBWebPage> tempMapByID = new HashMap<String, WBWebPage>();
			Map<String, WBWebPage> tempMapByName = new HashMap<String, WBWebPage>();
			List<WBWebPage> recList = dataStorage.getAllRecords(WBWebPage.class);
			for(WBWebPage item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByExternalId = tempMapByID;
			localCacheByName = tempMapByName;
		}
		
	}

	public WBWebPage get(String pageName) throws WBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(pageName);
		}
		return null;
	}

}
