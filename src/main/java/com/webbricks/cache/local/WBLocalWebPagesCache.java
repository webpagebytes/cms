package com.webbricks.cache.local;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webbricks.cache.WBWebPagesCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalWebPagesCache implements WBWebPagesCache {
	private AdminDataStorage dataStorage;
	private Map<String, WBWebPage> localCache;
	private static final Object lock = new Object();
	public WBLocalWebPagesCache()
	{
		dataStorage = AdminDataStorageFactory.getInstance();
		try
		{
			Refresh();
		} catch (WBIOException e)
		{
			
		}
	}
	public WBWebPage getByExternalKey(String externalKey) throws WBIOException
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
			Map<String, WBWebPage> tempMap = new HashMap<String, WBWebPage>();
			List<WBWebPage> recList = dataStorage.getAllRecords(WBWebPage.class);
			for(WBWebPage item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}

	public WBWebPage get(String pageName) throws WBIOException
	{
		return null;
	}

}
