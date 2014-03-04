package com.webbricks.cache.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.webbricks.cache.WBUrisCache;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalUrisCache implements WBUrisCache {

	private static final Object lock = new Object();
	private AdminDataStorage dataStorage;
	Map<Integer, Map<String, WBUri>> localCache;
	long cacheFingerPrint;
	
	public WBLocalUrisCache()
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
	public WBUri getByExternalKey(String key) throws WBIOException
	{
		return null;
	}
	
	public WBUri get(String uri, int httpIndex) throws WBIOException
	{
		if (localCache != null)
		{
			Map<String, WBUri> uris = localCache.get(httpIndex);
			if (uris != null)
			{
				return uris.get(uri);
			}
		}
		return null;
	}

	public Set<String> getAllUris(int httpIndex) throws WBIOException
	{
		if (localCache != null)
		{
			Map<String, WBUri> uris = localCache.get(httpIndex);
			if (uris != null)
			{
				return uris.keySet();
			}
		}
		return new HashSet<String>();
	}
	
	public Long getCacheFingerPrint()
	{
		return cacheFingerPrint;
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

	@Override
	public void Refresh() throws WBIOException {
		synchronized (lock)
		{
			Map<Integer, Map<String, WBUri>> tempMapUris = new HashMap<Integer, Map<String, WBUri>>();
			
			List<WBUri> recList = dataStorage.getAllRecords(WBUri.class);
			for(WBUri item: recList)
			{
				int httpIndex = httpToOperationIndex(item.getHttpOperation().toUpperCase());
				if (httpIndex >=0)
				{
					Map<String, WBUri> aMap = tempMapUris.get(httpIndex);
					if (aMap == null)
					{
						aMap = new HashMap<String, WBUri>();
						tempMapUris.put(httpIndex, aMap);
					}
					aMap.put(item.getUri(), item);
				}			
			}
			localCache = tempMapUris;
			Random r = new Random();
			cacheFingerPrint = r.nextLong();
		}

		
	}

}
