package com.webpagebytes.cms.local;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCachePagesCache implements WPBPagesCache {

	private WPBMemCacheClient memcacheClient;
	private static final String CACHE_KEY = "wpbpagescache";
	private String fingerPrint = ""; 
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBPage> localCacheByExternalId;
	private Map<String, WPBPage> localCacheByName;	
	private static final Object lock = new Object();
	
	public WPBMemCachePagesCache(WPBMemCacheClient memcacheClient)
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
	public WPBPage getByExternalKey(String externalKey) throws WPBIOException
	{
		if (localCacheByExternalId != null)
		{
			return localCacheByExternalId.get(externalKey);
		}
		return null;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock)
		{
			Map<String, WPBPage> tempMapByID = new HashMap<String, WPBPage>();
			Map<String, WPBPage> tempMapByName = new HashMap<String, WPBPage>();
			List<WPBPage> recList = dataStorage.getAllRecords(WPBPage.class, "externalKey", AdminSortOperator.ASCENDING);
			
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBPage item: recList)
			{
				md.update(item.getVersion().getBytes());
				tempMapByID.put(item.getExternalKey(), item);
				tempMapByName.put(item.getName(), item);
			}
			
			localCacheByExternalId = tempMapByID;
			localCacheByName = tempMapByName;
			fingerPrint = CmsBase64Utility.toBase64(md.digest());
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
		
	}

	public WPBPage get(String pageName) throws WPBIOException
	{
		if (localCacheByName != null)
		{
			return localCacheByName.get(pageName);
		}
		return null;
	}
	@Override
	public String getFingerPrint() {
		return fingerPrint;
	}

}
