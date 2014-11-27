package com.webpagebytes.cms.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalFilesCache implements WPBFilesCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBFile> localCache;
	private static final Object lock = new Object();
	public WPBLocalFilesCache()
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
	public WPBFile getByExternalKey(String externalKey) throws WPBIOException
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
			Map<String, WPBFile> tempMap = new HashMap<String, WPBFile>();
			List<WPBFile> recList = dataStorage.getAllRecords(WPBFile.class);
			for(WPBFile item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}
	
}
