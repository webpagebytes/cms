package com.webpagebytes.cms.cache;

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
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.GaeAdminDataStorage;
import com.webpagebytes.cms.exception.WBIOException;

public class GaeWBUrisCache implements WBUrisCache, WBRefreshableCache {
	
	private static final Logger log = Logger.getLogger(GaeWBUrisCache.class.getName());
	
	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBUri";
	private static final String memcacheMapKey = "keyToWBUri";
	private static final String memcacheUrlsKeyGet = "allUrisGet";
	private static final String memcacheUrlsKeyPost = "allUrisPost";
	private static final String memcacheUrlsKeyPut = "allUrisPut";
	private static final String memcacheUrlsKeyDelete = "allUrisDelete";
	
	private static final String memcacheFingerPrint = "fingerPrint";
	
	private AdminDataStorage adminDataStorage = null;
	private SecureRandom random = null;
	public GaeWBUrisCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();
		random = new SecureRandom();
	}
	
	public int httpToOperationIndex(String httpOperation)
	{
		if (httpOperation.toUpperCase().equals("GET"))
		{
			return HTTP_GET_INDEX;
		} else if (httpOperation.toUpperCase().equals("POST"))
		{
			return HTTP_POST_INDEX;
		} else if (httpOperation.toUpperCase().equals("PUT"))
		{
			return HTTP_PUT_INDEX;
		} else if (httpOperation.toUpperCase().equals("DELETE"))
		{
			return HTTP_DELETE_INDEX;
		}
		return -1;	
	}
	public String indexOperationToHttpVerb(int httpIndex)
	{
		if (httpIndex == WBUrisCache.HTTP_GET_INDEX)
		{
			return "GET";
		} else if (httpIndex == WBUrisCache.HTTP_POST_INDEX)
		{
			return "POST";
		} else if (httpIndex == WBUrisCache.HTTP_PUT_INDEX)
		{
			return "PUT";
		} else if (httpIndex == WBUrisCache.HTTP_DELETE_INDEX)
		{
			return "DELETE";
		}
		return null;
	}

	private String httpIndextoString(int httpIndex)
	{
		if (httpIndex == WBUrisCache.HTTP_GET_INDEX)
		{
			return memcacheUrlsKeyGet;
		} else if (httpIndex == WBUrisCache.HTTP_POST_INDEX)
		{
			return memcacheUrlsKeyPost;
		} else if (httpIndex == WBUrisCache.HTTP_PUT_INDEX)
		{
			return memcacheUrlsKeyPut;
		} else if (httpIndex == WBUrisCache.HTTP_DELETE_INDEX)
		{
			return memcacheUrlsKeyDelete;
		}
		return null;
	}
	
	public void Refresh() throws WBIOException
	{
		log.log(Level.INFO, "GaeWBUriCache:Refresh");
		RefreshInternal(null, null, null, null, null);
	}

	private void RefreshInternal(Map<String, WBUri> keyMapAll, 
								 Map<String, WBUri> urisMapGet,
								 Map<String, WBUri> urisMapPost,
								 Map<String, WBUri> urisMapPut,
								 Map<String, WBUri> urisMapDelete) throws WBIOException
	{
		synchronized (this) {
			log.log(Level.INFO, "GaeWBUriCache:RefreshInternal");
			List<WBUri> wburis = adminDataStorage.getAllRecords(WBUri.class);
			if (keyMapAll == null)
			{
				keyMapAll = new HashMap<String, WBUri>();
			}
			if (urisMapGet == null)
			{
				urisMapGet = new HashMap<String, WBUri>();
			}
			if (urisMapPost == null)
			{
				urisMapPost = new HashMap<String, WBUri>();
			}
			if (urisMapPut == null)
			{
				urisMapPut = new HashMap<String, WBUri>();
			}
			if (urisMapDelete == null)
			{
				urisMapDelete = new HashMap<String, WBUri>();
			}
			
			for (WBUri uri : wburis)
			{
				keyMapAll.put(uri.getExternalKey(), uri);
				if (uri.getHttpOperation().equals("GET"))
				{
					urisMapGet.put(uri.getUri(), uri);
				} else if (uri.getHttpOperation().equals("POST"))
				{
					urisMapPost.put(uri.getUri(), uri);
				} else if (uri.getHttpOperation().equals("PUT"))
				{
					urisMapPut.put(uri.getUri(), uri);
				} else if (uri.getHttpOperation().equals("DELETE"))
				{
					urisMapDelete.put(uri.getUri(), uri);
				}					
			}
			memcache.put(memcacheMapKey, keyMapAll, Expiration.byDeltaSeconds(6000));
			memcache.put(memcacheUrlsKeyGet, urisMapGet, Expiration.byDeltaSeconds(6000));
			memcache.put(memcacheUrlsKeyPost, urisMapPost, Expiration.byDeltaSeconds(6000));
			memcache.put(memcacheUrlsKeyPut, urisMapPut, Expiration.byDeltaSeconds(6000));
			memcache.put(memcacheUrlsKeyDelete, urisMapDelete, Expiration.byDeltaSeconds(6000));
			
			Long fingerPrint = 0L;
			while ((fingerPrint = random.nextLong()) == 0) { };			
			memcache.put(memcacheFingerPrint, fingerPrint, Expiration.byDeltaSeconds(6000));			
			
		}
	}

	public synchronized WBUri getByExternalKey(String externalKey) throws WBIOException
	{
		HashMap<String, WBUri> mapkeys = (HashMap<String, WBUri>) memcache.get(memcacheMapKey);
		if (mapkeys != null && mapkeys.containsKey(externalKey))
		{
			return (WBUri) mapkeys.get(externalKey);
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not find externalKey " + externalKey);
		
		Map<String, WBUri> refreshData = new HashMap<String, WBUri>(); 
		RefreshInternal(refreshData, null, null, null, null);
		if (refreshData.containsKey(externalKey))
		{
			return mapkeys.get(externalKey);
		}		
		return null;
	}

	
	public synchronized WBUri get(String uri, int httpIndex) throws WBIOException
	{
		HashMap<String, WBUri> mapkeys = (HashMap<String, WBUri>) memcache.get(httpIndextoString(httpIndex));
		if (mapkeys != null && mapkeys.containsKey(uri))
		{
			return (WBUri) mapkeys.get(uri);
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not find " + uri);
		Map<String, WBUri> refreshData = new HashMap<String, WBUri>(); 
		
		if (httpIndex == HTTP_GET_INDEX) {
			RefreshInternal(null, refreshData, null, null, null);
		} else if (httpIndex == HTTP_POST_INDEX) {
			RefreshInternal(null, null, refreshData, null, null);
		} else if (httpIndex == HTTP_PUT_INDEX) {
			RefreshInternal(null, null, null, refreshData, null);
		} else if (httpIndex == HTTP_DELETE_INDEX) {
			RefreshInternal(null, null, null, null, refreshData);
		}
			
		if (refreshData.containsKey(uri))
		{
			return refreshData.get(uri);
		}		
		return null;
	}

	public synchronized Set<String> getAllUris(int httpIndex) throws WBIOException
	{
		HashMap<String, WBUri> mapkeys = (HashMap<String, WBUri>) memcache.get( httpIndextoString(httpIndex));
		if (mapkeys != null )
		{
			return mapkeys.keySet();
		}
		log.log(Level.INFO, "GaeWBUriCache:get could not getAllUris ");
		Map<String, WBUri> refreshData = new HashMap<String, WBUri>(); 
		if (httpIndex == HTTP_GET_INDEX) {
			RefreshInternal(null, refreshData, null, null, null);
		} else if (httpIndex == HTTP_POST_INDEX) {
			RefreshInternal(null, null, refreshData, null, null);
		} else if (httpIndex == HTTP_PUT_INDEX) {
			RefreshInternal(null, null, null, refreshData, null);
		} else if (httpIndex == HTTP_DELETE_INDEX) {
			RefreshInternal(null, null, null, null, refreshData);
		}

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
