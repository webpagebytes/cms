/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.local;

import java.util.HashMap;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalUrisCache extends WPBUrisCache {

	private static final Object lock = new Object();
	private WPBAdminDataStorage dataStorage;
	Map<Integer, Map<String, WPBUri>> localCache;
	protected String cacheFingerPrint;
	
	public WPBLocalUrisCache()
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
			
			List<WPBUri> recList = dataStorage.getAllRecords(WPBUri.class);
			for(WPBUri item: recList)
			{
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
			cacheFingerPrint = UUID.randomUUID().toString();
		}

		
	}

}
