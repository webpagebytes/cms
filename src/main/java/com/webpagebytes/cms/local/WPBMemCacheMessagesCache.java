package com.webpagebytes.cms.local;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCacheMessagesCache implements WPBMessagesCache {

	public static final String CACHE_KEY = "wpbmessagescache";
	WPBMemCacheClient memcacheClient;
	Map<String, Map<String, String>> cacheMessages;
	private String fingerPrint = "";
	private WPBAdminDataStorage dataStorage;
	private static final Object lock = new Object();

	public WPBMemCacheMessagesCache(WPBMemCacheClient memcacheClient)
	{
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
			List<WPBMessage> records = dataStorage.getAllRecords(WPBMessage.class, "externalKey", AdminSortOperator.ASCENDING);
			
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBMessage item: records)
			{
				md.update(item.getVersion().getBytes());
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
			fingerPrint = CmsBase64Utility.toBase64(md.digest());
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
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
