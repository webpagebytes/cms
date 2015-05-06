package com.webpagebytes.cms.local;

import java.security.MessageDigest;

import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.engine.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.CmsBase64Utility;

public class WPBMemCacheParametersCache implements WPBParametersCache {

	private static final String CACHE_KEY = "wpbparameterscache";
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBParameter> cacheParameters;
	private Map<String, List<WPBParameter>> cacheOwnerParameters;
	private String fingerPrint = "";
	private static final Object lock = new Object();
	private WPBMemCacheClient memcacheClient;

	public WPBMemCacheParametersCache(WPBMemCacheClient memcacheClient)
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
	public WPBParameter getByExternalKey(String externalKey) throws WPBIOException
	{
		if (cacheParameters != null) 
		{
			return cacheParameters.get(externalKey);
		}
		return null;
	}
	
	public List<WPBParameter> getAllForOwner(String ownerExternalKey) throws WPBIOException
	{
		List<WPBParameter> result = null;
		if (cacheOwnerParameters != null)
		{
			result = cacheOwnerParameters.get(ownerExternalKey);
		}
		if (result == null)
		{
			result = new ArrayList<WPBParameter>();
		}
		return result;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			List<WPBParameter> records = dataStorage.getAllRecords(WPBParameter.class, "externalKey", AdminSortOperator.ASCENDING);
			
			Map<String, WPBParameter> localCache = new HashMap<String, WPBParameter>();
			Map<String, List<WPBParameter>> ownersLocalCache = new HashMap<String, List<WPBParameter>>();
			
			MessageDigest md = null;
			try
			{
				md = MessageDigest.getInstance("MD5");
			} catch (NoSuchAlgorithmException e)
			{
				throw new WPBIOException("cannot calculate fingerprint", e);
			}
			
			for(WPBParameter item: records)
			{
				md.update(item.getVersion().getBytes());
				localCache.put(item.getExternalKey(), item);
				String ownerKey = item.getOwnerExternalKey();
				List<WPBParameter> aList = ownersLocalCache.get(ownerKey);
				if (null == aList)
				{
					aList = new ArrayList<WPBParameter>();
					ownersLocalCache.put(ownerKey, aList);
				}
				aList.add(item);
			}
			cacheParameters = localCache;
			cacheOwnerParameters = ownersLocalCache;
			fingerPrint = CmsBase64Utility.toBase64(md.digest());
			memcacheClient.putFingerPrint(CACHE_KEY, fingerPrint);
		}
	}
	@Override
	public String getFingerPrint() {
		return fingerPrint;
	}

}
