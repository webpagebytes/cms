package com.webpagebytes.cms.local;

import java.util.logging.Logger;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBRefreshableCache;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBMemCacheSyncRunnable implements Runnable {

	private static final Logger log = Logger.getLogger(WPBMemCacheSyncRunnable.class.getName());
	
	private WPBMemCacheClient memcacheClient;
	private WPBMemCacheFactory cacheFactory;
	public WPBMemCacheSyncRunnable(WPBMemCacheFactory cacheFactory, WPBMemCacheClient memcacheClient)
	{
		this.cacheFactory = cacheFactory;
		this.memcacheClient = memcacheClient;
	}
	
	private void checkCache(String key, WPBRefreshableCache cache) throws WPBIOException
	{
		String cacheFingerPrint = memcacheClient.getFingerPrint(key);
		String localFingerPrint = cache.getFingerPrint();
		log.info(String.format("WPBMemCacheSyncRunnable check cache key=%s (cache=%s) (local=%s)", key, cacheFingerPrint, localFingerPrint));
		if (cacheFingerPrint == null || cacheFingerPrint.length() == 0)
		{
			memcacheClient.putFingerPrint(key, localFingerPrint);
		} else if (!cacheFingerPrint.equals(localFingerPrint))
		{
			log.info("WPBMemCacheSyncRunnable refresh for key=" + key);
			cache.Refresh();
		}
	}
	
	@Override
	public void run() {
		while (true)
		{
			try
			{
				WPBUrisCache cacheUris = cacheFactory.getUrisCacheInstance();
				checkCache(WPBMemCacheUrisCache.CACHE_KEY, cacheUris);
				
				WPBPagesCache cachePages = cacheFactory.getPagesCacheInstance();
				checkCache(WPBMemCachePagesCache.CACHE_KEY, cachePages);
				
				WPBPageModulesCache cacheModules = cacheFactory.getPageModulesCacheInstance();
				checkCache(WPBMemCachePageModulesCache.CACHE_KEY, cacheModules);
				
				WPBArticlesCache cacheArticles = cacheFactory.getArticlesCacheInstance();
				checkCache(WPBMemCacheArticlesCache.CACHE_KEY, cacheArticles);
				
				WPBMessagesCache cacheMessages = cacheFactory.getMessagesCacheInstance();
				checkCache(WPBMemCacheMessagesCache.CACHE_KEY, cacheMessages);
				
				WPBFilesCache cacheFiles = cacheFactory.getFilesCacheInstance();
				checkCache(WPBMemCacheFilesCache.CACHE_KEY, cacheFiles);
				
				WPBParametersCache cacheParameters = cacheFactory.getParametersCacheInstance();
				checkCache(WPBMemCacheParametersCache.CACHE_KEY, cacheParameters);
				
				WPBProjectCache cacheProject = cacheFactory.getProjectCacheInstance();
				checkCache(WPBMemCacheProjectCache.CACHE_KEY, cacheProject);
				
				Thread.sleep(10000);
			}
			catch (WPBIOException e)
			{
				// TBD
			}
			catch (InterruptedException e)
			{
				// TBD
			}
		}
	}

}
