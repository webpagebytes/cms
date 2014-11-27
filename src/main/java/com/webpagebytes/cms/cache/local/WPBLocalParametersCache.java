package com.webpagebytes.cms.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBLocalParametersCache implements WPBParametersCache {
	
	private WPBAdminDataStorage dataStorage;
	private Map<String, WPBParameter> cacheParameters;
	private Map<String, List<WPBParameter>> cacheOwnerParameters;
	
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
			List<WPBParameter> records = dataStorage.getAllRecords(WPBParameter.class);
			Map<String, WPBParameter> localCache = new HashMap<String, WPBParameter>();
			Map<String, List<WPBParameter>> ownersLocalCache = new HashMap<String, List<WPBParameter>>();
			for(WPBParameter item: records)
			{
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
		}
	}
}
