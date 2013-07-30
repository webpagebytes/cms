package com.webbricks.cache;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.exception.WBIOException;

public class GaeWBParametersCache implements WBParametersCache, WBRefreshableCache {
	
	private static final Logger log = Logger.getLogger(GaeWBParametersCache.class.getName());
	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBParameter";
	private static final String memcacheMapKey = "keyToWBParameter";
	private static final String memcacheMapOwners = "ownerToWBParameter";
	private AdminDataStorage adminDataStorage = null;
	
	public GaeWBParametersCache() {
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();
	}
	
	public void Refresh() throws WBIOException
	{
		RefreshInternal(null, null);
	}

	private void RefreshInternal(Map<String, WBParameter> keyMap, Map<String, List<WBParameter>> ownersMap) throws WBIOException
	{
		synchronized (this) {
			log.log(Level.INFO, "GaeWBParameterCache:RefreshInternal");
			
			List<WBParameter> wbParameters = adminDataStorage.getAllRecords(WBParameter.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<String, WBParameter>();
			}
			if (ownersMap == null)
			{
				ownersMap = new HashMap<String, List<WBParameter>>();
			}
			for (WBParameter wbParameter : wbParameters)
			{
				String aExternalKey = wbParameter.getExternalKey();
				String ownerExternalKey = wbParameter.getOwnerExternalKey();
				keyMap.put(aExternalKey, wbParameter);
				if (ownersMap.get(ownerExternalKey) == null)
				{
					List<WBParameter> aList = new ArrayList<WBParameter>();
					aList.add(wbParameter);
					ownersMap.put(ownerExternalKey, aList);
				} else
				{
					ownersMap.get(ownerExternalKey).add(wbParameter);
				}
			}
			memcache.put(memcacheMapKey, keyMap);
			memcache.put(memcacheMapOwners, ownersMap);
		}
	}

	public WBParameter getByExternalKey(String externalKey) throws WBIOException
	{
		HashMap<String, WBParameter> mapkeys = (HashMap<String, WBParameter>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBParameter) mapkeys.get(externalKey);
		}
		Map<String, WBParameter> refreshData = new HashMap<String, WBParameter>(); 
		RefreshInternal(refreshData, null);
		if (refreshData.containsKey(externalKey))
		{
			return refreshData.get(externalKey);
		}		
		return null;
	}
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException
	{
		HashMap<String, List<WBParameter>> ownersMap = (HashMap<String, List<WBParameter>>) memcache.get(memcacheMapOwners);
		if (ownersMap != null && ownersMap.containsKey(ownerExternalKey))
		{
			return (List<WBParameter>) ownersMap.get(ownerExternalKey);
		}
		Map<String, List<WBParameter>> refreshData = new HashMap<String, List<WBParameter>>(); 
		RefreshInternal(null, refreshData);
		if (refreshData.containsKey(ownerExternalKey))
		{
			return refreshData.get(ownerExternalKey);
		}		
		return new ArrayList<WBParameter>();
	}

	

}
