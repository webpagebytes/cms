package com.webbricks.cache.local;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.webbricks.cache.WBParametersCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.exception.WBIOException;

public class WBLocalParametersCache implements WBParametersCache {
	
	private AdminDataStorage dataStorage;
	private Map<String, WBParameter> cacheParameters;
	private Map<String, List<WBParameter>> cacheOwnerParameters;
	
	private static final Object lock = new Object();

	public WBLocalParametersCache()
	{
		dataStorage = AdminDataStorageFactory.getInstance();
		try
		{
			Refresh();
		} catch (WBIOException e)
		{
			
		}
	}
	public WBParameter getByExternalKey(String externalKey) throws WBIOException
	{
		if (cacheParameters != null) 
		{
			cacheParameters.get(externalKey);
		}
		return null;
	}
	
	public List<WBParameter> getAllForOwner(String ownerExternalKey) throws WBIOException
	{
		if (cacheOwnerParameters != null)
		{
			cacheOwnerParameters.get(ownerExternalKey);
		}
		return new ArrayList<WBParameter>();
	}

	@Override
	public void Refresh() throws WBIOException {
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
