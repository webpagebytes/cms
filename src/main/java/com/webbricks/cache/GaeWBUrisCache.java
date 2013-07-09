package com.webbricks.cache;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.sun.java.swing.plaf.windows.WindowsButtonUI;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBUrisCache implements WBUrisCache, WBRefreshableCache {
	
	private static final Logger log = Logger.getLogger(GaeWBUrisCache.class.getName());
	
	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBUri";
	private static final String memcacheMapKey = "keyToWBUri";
	private static final String memcacheUrlsKey = "allUris";
	private static final String memcacheFingerPrint = "fingerPrint";
	
	private AdminDataStorage adminDataStorage = null;
	private SecureRandom random = null;
	public GaeWBUrisCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();
		random = new SecureRandom();
	}
	
	public void Refresh() throws WBIOException
	{
		log.log(Level.INFO, "GaeWBUriCache:Refresh");
		RefreshInternal(null, null);
	}

	private void RefreshInternal(Map<Long, WBUri> keyMap, Map<String, WBUri> urisMap) throws WBIOException
	{
		synchronized (this) {
			log.log(Level.INFO, "GaeWBUriCache:RefreshInternal");
			List<WBUri> wburis = adminDataStorage.getAllRecords(WBUri.class);
			if (keyMap == null)
			{
				keyMap = new HashMap<Long, WBUri>();
			}
			if (urisMap == null)
			{
				urisMap = new HashMap<String, WBUri>();
			}
			for (WBUri uri : wburis)
			{
				keyMap.put(uri.getExternalKey(), uri);
				urisMap.put(uri.getUri(), uri);
			}
			memcache.put(memcacheMapKey, keyMap, Expiration.byDeltaSeconds(600));
			memcache.put(memcacheUrlsKey, urisMap, Expiration.byDeltaSeconds(600));
			
			Long fingerPrint = 0L;
			while ((fingerPrint = random.nextLong()) == 0) { };			
			memcache.put(memcacheFingerPrint, fingerPrint, Expiration.byDeltaSeconds(600));
			
			
		}
	}

	public synchronized WBUri get(Long externalKey) throws WBIOException
	{
		HashMap<Long, WBUri> mapkeys = (HashMap<Long, WBUri>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBUri) mapkeys.get(externalKey);
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not find externalKey " + externalKey);
		
		Map<Long, WBUri> refreshData = new HashMap<Long, WBUri>(); 
		RefreshInternal(refreshData, null);
		if (refreshData.containsKey(externalKey))
		{
			return mapkeys.get(externalKey);
		}		
		return null;
	}
	
	public synchronized WBUri get(String uri) throws WBIOException
	{
		HashMap<String, WBUri> mapkeys = (HashMap<String, WBUri>) memcache.get(memcacheUrlsKey);
		if (mapkeys != null && mapkeys.containsKey(uri))
		{
			return (WBUri) mapkeys.get(uri);
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not find " + uri);
		Map<String, WBUri> refreshData = new HashMap<String, WBUri>(); 
		RefreshInternal(null, refreshData);
		if (refreshData.containsKey(uri))
		{
			return refreshData.get(uri);
		}		
		return null;
	}

	public synchronized Set<String> getAllUris() throws WBIOException
	{
		HashMap<String, WBUri> mapkeys = (HashMap<String, WBUri>) memcache.get(memcacheUrlsKey);
		if (mapkeys != null )
		{
			return mapkeys.keySet();
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not getAllUris ");
		Map<String, WBUri> refreshData = new HashMap<String, WBUri>(); 
		RefreshInternal(null, refreshData);
		return refreshData.keySet();
	}

	public Long getCacheFingerPrint()
	{
		Long fingerPrint = (Long) memcache.get(memcacheFingerPrint);
		if (null != fingerPrint)
		{
			return fingerPrint;
		}	
		log.log(Level.INFO, "GaeWBUriCache:get could not getfingerprint");
		return 0L;
	}

}
