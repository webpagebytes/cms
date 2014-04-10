package com.webpagebytes.cache.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

import com.webpagebytes.cache.WBMessagesCache;
import com.webpagebytes.cmsdata.WBFile;
import com.webpagebytes.cmsdata.WBMessage;
import com.webpagebytes.datautility.AdminDataStorage;
import com.webpagebytes.datautility.AdminDataStorageFactory;
import com.webpagebytes.exception.WBIOException;

public class WBLocalMessagesCache implements WBMessagesCache {
	long cacheFingerPrint = 0;
	Map<String, Map<String, String>> cacheMessages;
	private AdminDataStorage dataStorage;
	private static final Object lock = new Object();

	public WBLocalMessagesCache()
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
	
	private String lcidFromLocale(Locale locale)
	{
		return (locale.getCountry().length()>0) ? (locale.getLanguage() + "_" + locale.getCountry()): locale.getLanguage();  
	}

	public Map<String, String> getAllMessages(Locale locale) throws WBIOException
	{
		String lcid = lcidFromLocale(locale);
		return getAllMessages(lcid);
	}
	
	public Map<String, String> getAllMessages(String lcid) throws WBIOException
	{
		if (cacheMessages != null)
		{
			return cacheMessages.get(lcid);
		}
		return new HashMap<String, String>();
	}
	
	public Long getFingerPrint(Locale locale)
	{
		return cacheFingerPrint;
	}
	
	@Override
	public void Refresh() throws WBIOException {
		synchronized (lock) {
			Map<String, Map<String, String>> tempCache = new HashMap<String, Map<String, String>>();
			List<WBMessage> records = dataStorage.getAllRecords(WBMessage.class);
			for(WBMessage item: records)
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
			Random r = new Random();
			cacheFingerPrint = r.nextLong();
		}
		
	}
}
