package com.webbricks.cache.local;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import com.webbricks.cache.WBFilesCache;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalFilesCache implements WBFilesCache {
	
	private AdminDataStorage dataStorage;
	private Map<String, WBFile> localCache;
	private static final Object lock = new Object();
	public WBLocalFilesCache()
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
	public WBFile getByExternalKey(String externalKey) throws WBIOException
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
			Map<String, WBFile> tempMap = new HashMap<String, WBFile>();
			List<WBFile> recList = dataStorage.getAllRecords(WBFile.class);
			for(WBFile item: recList)
			{
				tempMap.put(item.getExternalKey(), item);
			}
			localCache = tempMap;
		}
		
	}
	
}
