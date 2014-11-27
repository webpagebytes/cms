package com.webpagebytes.cms.cache.local;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalWebPagesCache implements WPBWebPagesCache {
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBWebPage> localCacheByExternalId;
	private Map<String, WPBWebPage> localCacheByName;	
	private static final Object lock = new Object();
	public WPBLocalWebPagesCache()
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
	public WPBWebPage getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByExternalId != null)
		{
			return localCacheByExternalId.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBWebPage> tempMapByID = new HashMap<String, WPBWebPage>();
			Map<String, WPBWebPage> tempMapByName = new HashMap<String, WPBWebPage>();
			List<WPBWebPage> recList = dataStorage.getAllRecords(WPBWebPage.class);
			for(WPBWebPage item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByExternalId = tempMapByID;
			localCacheByName = tempMapByName;
		}
		
	}

	public WPBWebPage get(String pageName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(pageName);
		}
		return null;
	}

}
