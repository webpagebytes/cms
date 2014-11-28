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

package com.webpagebytes.cms.cache.local;

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalWebPageModulesCache implements WPBWebPageModulesCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBWebPageModule> localCacheByID;
	private Map<String, WPBWebPageModule> localCacheByName;
	private static final Object lock = new Object();
	public WPBLocalWebPageModulesCache()
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
	public WPBWebPageModule getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByID != null)
		{
			return localCacheByID.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBWebPageModule> tempMapByID = new HashMap<String, WPBWebPageModule>();
			Map<String, WPBWebPageModule> tempMapByName = new HashMap<String, WPBWebPageModule>();
			
			List<WPBWebPageModule> recList = dataStorage.getAllRecords(WPBWebPageModule.class);
			for(WPBWebPageModule item: recList)
			{
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByID = tempMapByID;
			localCacheByName = tempMapByName;
		}
		
	}

		
	public WPBWebPageModule get(String moduleName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(moduleName);
		}
		return null;
	}
	
}
