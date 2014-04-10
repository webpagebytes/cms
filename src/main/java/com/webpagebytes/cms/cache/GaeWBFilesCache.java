package com.webpagebytes.cms.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.GaeAdminDataStorage;
import com.webpagebytes.cms.exception.WBIOException;

public class GaeWBFilesCache implements WBFilesCache {

	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBFile";
	private static final String memcacheMapKey = "externalKeyToWBFile";
	private AdminDataStorage adminDataStorage = null;

	public GaeWBFilesCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();		
	}
	
	public WBFile getByExternalKey(String externalKey) throws WBIOException
	{
		HashMap<String, WBFile> mapkeys = (HashMap<String, WBFile>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBFile) mapkeys.get(externalKey);
		}
		Map<String, WBFile> refreshData = new HashMap<String, WBFile>(); 
		RefreshInternal(refreshData);
		if (refreshData.containsKey(externalKey))
		{
			return refreshData.get(externalKey);
		}		
		return null;
	}
	
	public void Refresh() throws WBIOException
	{
		RefreshInternal(null);
	}
	
	private void RefreshInternal(Map<String, WBFile> keyMap) throws WBIOException
	{
		synchronized (this) {
			List<WBFile> wbImages = adminDataStorage.getAllRecords(WBFile.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<String, WBFile>();
			}
			for (WBFile wbImage : wbImages)
			{
				String aKey = wbImage.getExternalKey();
				keyMap.put(aKey, wbImage);
			}
			memcache.put(memcacheMapKey, keyMap);
		}
	}
}
