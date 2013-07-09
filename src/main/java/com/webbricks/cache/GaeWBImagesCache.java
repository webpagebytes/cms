package com.webbricks.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBImagesCache implements WBFilesCache {

	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBImage";
	private static final String memcacheMapKey = "externalKeyToWBImage";
	private AdminDataStorage adminDataStorage = null;

	public GaeWBImagesCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();		
	}
	
	public WBFile get(Long externalKey) throws WBIOException
	{
		HashMap<Long, WBFile> mapkeys = (HashMap<Long, WBFile>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBFile) mapkeys.get(externalKey);
		}
		Map<Long, WBFile> refreshData = new HashMap<Long, WBFile>(); 
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
	
	private void RefreshInternal(Map<Long, WBFile> keyMap) throws WBIOException
	{
		synchronized (this) {
			List<WBFile> wbImages = adminDataStorage.getAllRecords(WBFile.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<Long, WBFile>();
			}
			for (WBFile wbImage : wbImages)
			{
				Long aKey = wbImage.getExternalKey();
				keyMap.put(aKey, wbImage);
			}
			memcache.put(memcacheMapKey, keyMap);
		}
	}
}
