package com.webpagebytes.cms.local;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCacheUrisCache extends WPBUrisCache {

	public static final String CACHE_KEY = "wpburiscache";
	private WPBMemCacheClient memcacheClient; 
	private String memCacheKey;	
	private static final Object lock = new Object();
	private WPBAdminDataStorage dataStorage;
	private Map<Integer, Map<String, WPBUri>> localCache;
	protected String cacheFingerPrint;

	public WPBMemCacheUrisCache(WPBMemCacheClient memcacheClient)
	{
		memCacheKey = WPBUri.class.getSimpleName();
		this.memcacheClient = memcacheClient;
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
		
	public WPBUri getByExternalKey(String key) throws WPBIOException
	{
		return null;
	}
	
	public WPBUri get(String uri, int httpIndex) throws WPBIOException
	{
		if (localCache != null)
		{
			Map<String, WPBUri> uris = localCache.get(httpIndex);
			if (uris != null)
			{
				return uris.get(uri);
			}
		}
		return null;
	}

	public Set<String> getAllUris(int httpIndex) throws WPBIOException
	{
		if (localCache != null)
		{
			Map<String, WPBUri> uris = localCache.get(httpIndex);
			if (uris != null)
			{
				return uris.keySet();
			}
		}
		return new HashSet<String>();
	}
	
	public String getFingerPrint()
	{
		return cacheFingerPrint;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<Integer, Map<String, WPBUri>> tempMapUris = new HashMap<Integer, Map<String, WPBUri>>();
			
			List<WPBUri> recList = dataStorage.getAllRecords(WPBUri.class, "externalKey", AdminSortOperator.ASCENDING);
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBUri item: recList)
			{
				md.update(item.getVersion().getBytes());
				
				int httpIndex = httpToOperationIndex(item.getHttpOperation().toUpperCase());
				if (httpIndex >=0)
				{
					Map<String, WPBUri> aMap = tempMapUris.get(httpIndex);
					if (aMap == null)
					{
						aMap = new HashMap<String, WPBUri>();
						tempMapUris.put(httpIndex, aMap);
					}
					aMap.put(item.getUri(), item);
				}			
			}
			localCache = tempMapUris;
			cacheFingerPrint = CmsBase64Utility.toBase64(md.digest());
			memcacheClient.putFingerPrint(CACHE_KEY, cacheFingerPrint);
		}		
	}

}
