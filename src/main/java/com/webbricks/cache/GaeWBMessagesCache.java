package com.webbricks.cache;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBMessage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.exception.WBIOException;

public class GaeWBMessagesCache implements WBMessagesCache {
	private static final Logger log = Logger.getLogger(GaeWBMessagesCache.class.getName());
	private AdminDataStorage adminDataStorage;
	private MemcacheService memcache = null;
	private static final String memcacheNamespace = "cacheWBMessage";
	private static final String memcacheMapKey = "lcidToMessages_";
	private static final String memcacheFingerPrint = "fingerprint_";
	private SecureRandom random;
	
	public GaeWBMessagesCache()
	{
		memcache = MemcacheServiceFactory.getMemcacheService(memcacheNamespace);
		adminDataStorage = new GaeAdminDataStorage();		
		random = new SecureRandom();
	}
	private String lcidFromLocale(Locale locale)
	{
		return (locale.getCountry().length()>0) ? (locale.getLanguage() + "_" + locale.getCountry()): locale.getLanguage();  
	}
	public Map<String, String> getAllMessages(Locale locale) throws WBIOException
	{
		String cacheKey = memcacheMapKey + lcidFromLocale(locale);
		Map<String, String> result = (Map<String, String>) memcache.get(cacheKey);
		if (null == result)
		{
			result = new HashMap<String, String>();
			RefreshInternal(result, locale);
		}
		return result;
	}

	public Map<String, String> getAllMessages(String lcid) throws WBIOException
	{
		String cacheKey = memcacheMapKey + lcid;
		Map<String, String> result = (Map<String, String>) memcache.get(cacheKey);
		if (null == result)
		{
			result = new HashMap<String, String>();
			RefreshInternal(result, lcid);
		}
		return result;
	}

	public void RefreshInternal(Map<String, String> resources, Locale locale) throws WBIOException
	{
		synchronized (this) {
			log.log(Level.INFO, "GaeWBMessageCache:RefreshInternal");
			if (resources == null)
			{
				resources = new HashMap<String, String>();
			}
			String lcid = lcidFromLocale(locale);
			List<WBMessage> result = adminDataStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
			for (WBMessage message: result)
			{
				resources.put(message.getName(), message.getValue());
			}
			String cacheKey = memcacheMapKey + lcid;
			memcache.put(cacheKey, resources);

			cacheKey = memcacheFingerPrint + lcid;
			memcache.put(cacheKey, nextRandom());

		}
	}

	public void RefreshInternal(Map<String, String> resources, String lcid) throws WBIOException
	{
		synchronized (this) {
			if (resources == null)
			{
				resources = new HashMap<String, String>();
			}
			List<WBMessage> result = adminDataStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
			for (WBMessage message: result)
			{
				resources.put(message.getName(), message.getValue());
			}
			String cacheKey = memcacheMapKey + lcid;
			memcache.put(cacheKey, resources);
			
			cacheKey = memcacheFingerPrint + lcid;
			memcache.put(cacheKey, nextRandom());
		}
	}

	public void RefreshInternalAll() throws WBIOException
	{
		synchronized (this) {
			Map<String, String> resources = new HashMap<String, String>();
			Map mapOfMap = new HashMap();
			
			Set<String> setFingerPrint = new HashSet<String>();
			List<WBMessage> result = adminDataStorage.getAllRecords(WBMessage.class);
			for (WBMessage message: result)
			{
				String cacheKey = memcacheMapKey + message.getLcid();
				String fingerPrintKey = memcacheFingerPrint + message.getLcid();
				Map<String, String> aMap = null;
				if (!mapOfMap.containsKey(cacheKey))
				{
					aMap = new HashMap<String, String>();
					mapOfMap.put(cacheKey, aMap);
				} else
				{
					aMap = (Map<String, String>) mapOfMap.get(cacheKey);
				}
				aMap.put(message.getName(), message.getValue());
			}
			
			memcache.clearAll();
			
			Set<String> keySet = mapOfMap.keySet();
			for(String cacheKey: keySet)
			{
				memcache.put(cacheKey, (Map<String, String>) mapOfMap.get(cacheKey));				
			}
			for(String cacheKey: setFingerPrint)
			{
				memcache.put(cacheKey, nextRandom());				
			}
		}
	}

	public void Refresh() throws WBIOException
	{
		RefreshInternalAll();
	}
	
	public void Refresh(Locale lcid) throws WBIOException
	{
		RefreshInternal(null, lcid);
	}

	public Long getFingerPrint(Locale locale)
	{
		String key = memcacheFingerPrint + lcidFromLocale(locale);
		Long fingerPrint = (Long) memcache.get(key);
		return (fingerPrint != null) ? fingerPrint : 0L;
	}
	
	protected Long nextRandom()
	{
		//make sure 0L is not returned;
		Long result = 0L;
		while ((result = random.nextLong())==0L) {};
		return result;
	}
}
