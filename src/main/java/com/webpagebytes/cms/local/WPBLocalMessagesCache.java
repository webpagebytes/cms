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

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalMessagesCache implements WPBMessagesCache {
	Map<String, Map<String, String>> cacheMessages;
	private String fingerPrint = "";
	private WPBAdminDataStorage dataStorage;
	private static final Object lock = new Object();

	public WPBLocalMessagesCache()
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
	
	private String lcidFromLocale(Locale locale)
	{
		return (locale.getCountry().length()>0) ? (locale.getLanguage() + "_" + locale.getCountry()): locale.getLanguage();  
	}

	public Map<String, String> getAllMessages(Locale locale) throws WPBIOException
	{
		String lcid = lcidFromLocale(locale);
		return getAllMessages(lcid);
	}
	
	public Map<String, String> getAllMessages(String lcid) throws WPBIOException
	{
		if (cacheMessages != null)
		{
			return cacheMessages.get(lcid);
		}
		return new HashMap<String, String>();
	}
	
	public String getFingerPrint(Locale locale)
	{
		return fingerPrint;
	}
	
	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			Map<String, Map<String, String>> tempCache = new HashMap<String, Map<String, String>>();
			List<WPBMessage> records = dataStorage.getAllRecords(WPBMessage.class);
			for(WPBMessage item: records)
			{
				String lcid = item.getLcid();
				Map<String, String> lcidRecords = tempCache.get(lcid);
				if (null == lcidRecords)
				{
					lcidRecords = new HashMap<String, String>();
					tempCache.put(lcid, lcidRecords);
				}
				lcidRecords.put(item.getName(), item.getValue());
			}
			cacheMessages =  tempCache;
			fingerPrint = UUID.randomUUID().toString();
		}
		
	}
	
	public Set<String> getSupportedLocales()
	{
		return cacheMessages.keySet();
	}

	@Override
	public String getFingerPrint() {
		return fingerPrint;
	}
}
