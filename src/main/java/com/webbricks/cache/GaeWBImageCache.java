package com.webbricks.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBImage;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBImageCache implements WBImageCache {

	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBImage";
	private static final String memcacheMapKey = "externalKeyToWBImage";
	private AdminDataStorage adminDataStorage = null;

	public GaeWBImageCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();		
	}
	
	public WBImage get(Long externalKey) throws WBIOException
	{
		HashMap<Long, WBImage> mapkeys = (HashMap<Long, WBImage>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBImage) mapkeys.get(externalKey);
		}
		Map<Long, WBImage> refreshData = new HashMap<Long, WBImage>(); 
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
	
	private void RefreshInternal(Map<Long, WBImage> keyMap) throws WBIOException
	{
		synchronized (this) {
			List<WBImage> wbImages = adminDataStorage.getAllRecords(WBImage.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<Long, WBImage>();
			}
			for (WBImage wbImage : wbImages)
			{
				Long aKey = wbImage.getExternalKey();
				keyMap.put(aKey, wbImage);
			}
			memcache.put(memcacheMapKey, keyMap);
		}
	}
}
