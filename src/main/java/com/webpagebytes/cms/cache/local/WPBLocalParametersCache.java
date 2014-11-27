package com.webpagebytes.cms.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalParametersCache implements WPBParametersCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WBParameter> cacheParameters;
	private Map<String, List<WBParameter>> cacheOwnerParameters;
	
	private static final Object lock = new Object();

	public WPBLocalParametersCache()
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
	public WBParameter getByExternalKey(String externalKey) throws WPBIOException
	{
		if (cacheParameters != null) 
		{
			return cacheParameters.get(externalKey);
		}
		return null;
	}
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WPBIOException
	{
		List<WBParameter> result = null;
		if (cacheOwnerParameters != null)
		{
			result = cacheOwnerParameters.get(ownerExternalKey);
		}
		if (result == null)
		{
			result = new ArrayList<WBParameter>();
		}
		return result;
	}

	public void Refresh() throws WPBIOException {
		synchronized (lock) {
			List<WBParameter> records = dataStorage.getAllRecords(WBParameter.class);
			Map<String, WBParameter> localCache = new HashMap<String, WBParameter>();
			Map<String, List<WBParameter>> ownersLocalCache = new HashMap<String, List<WBParameter>>();
			for(WBParameter item: records)
			{
				localCache.put(item.getExternalKey(), item);
				String ownerKey = item.getOwnerExternalKey();
				List<WBParameter> aList = ownersLocalCache.get(ownerKey);
				if (null == aList)
				{
					aList = new ArrayList<WBParameter>();
					ownersLocalCache.put(ownerKey, aList);
				}
				aList.add(item);
			}
			cacheParameters = localCache;
			cacheOwnerParameters = ownersLocalCache;
		}
	}
}
