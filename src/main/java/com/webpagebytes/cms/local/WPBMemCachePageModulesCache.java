package com.webpagebytes.cms.local;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCachePageModulesCache implements WPBPageModulesCache {

	public static final String CACHE_KEY = "wpbpagemodulescache";
	private String fingerPrint = "";
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBPageModule> localCacheByID;
	private Map<String, WPBPageModule> localCacheByName;
	private static final Object lock = new Object();
	private WPBMemCacheClient memcacheClient;
	
	public WPBMemCachePageModulesCache(WPBMemCacheClient memcacheClient)
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
	public WPBPageModule getByExternalKey(String externalKey) throws WPBIOException
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
			Map<String, WPBPageModule> tempMapByID = new HashMap<String, WPBPageModule>();
			Map<String, WPBPageModule> tempMapByName = new HashMap<String, WPBPageModule>();
			
			List<WPBPageModule> recList = dataStorage.getAllRecords(WPBPageModule.class, "externalKey", AdminSortOperator.ASCENDING);
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBPageModule item: recList)
			{
				md.update(item.getVersion().getBytes());
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			localCacheByID = tempMapByID;
			localCacheByName = tempMapByName;
			fingerPrint = CmsBase64Utility.toBase64(md.digest());			
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
		
	}

		
	public WPBPageModule get(String moduleName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(moduleName);
		}
		return null;
	}
	@Override
	public String getFingerPrint() {
		return fingerPrint;
	}

}
