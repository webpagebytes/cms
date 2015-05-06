package com.webpagebytes.cms.local;

import java.io.IOException;
import java.util.Map;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.exception.WPBIOException;

public class WPBMemCacheFactory implements WPBCacheFactory {

	private Object lock = new Object();
	private WPBMemCacheClient memcacheClient;
	private static WPBUrisCache uriCacheInstance;
	private static WPBPagesCache pageCacheInstance;
	private static WPBParametersCache parametersCacheInstance;
	private static WPBPageModulesCache pageModulesCacheInstance;
	private static WPBFilesCache filesCacheInstance;
	private static WPBArticlesCache articlesCacheInstance;
	private static WPBMessagesCache messagesCacheInstance;
	private static WPBProjectCache projectCacheInstance;
	
	private static final String CONFIG_MEMCACHE_SERVERS = "memcacheservers";
	
	public void initialize(Map<String, String> params) throws WPBIOException
	{
		try
		{
			memcacheClient = new WPBMemCacheClient();
			String address = "";
			if (params!= null && params.get(CONFIG_MEMCACHE_SERVERS) != null)
			{
				address = params.get(CONFIG_MEMCACHE_SERVERS);
			}
			memcacheClient.initialize(address);
		} catch (IOException e)
		{
			throw new WPBIOException("cannot create memcache client", e);
		}
	}
	
	public WPBUrisCache getUrisCacheInstance()
	{
		synchronized (lock) {			
			if (null == uriCacheInstance)
			{
				uriCacheInstance = new WPBMemCacheUrisCache(memcacheClient);
			}
		}
		return uriCacheInstance;
	}
	public WPBPagesCache getWebPagesCacheInstance()
	{
		synchronized (lock) {		
			if (null == pageCacheInstance)
			{
				pageCacheInstance = new WPBMemCachePagesCache(memcacheClient);
			}
		}
		return pageCacheInstance;
	}
	public WPBParametersCache getParametersCacheInstance()
	{
		synchronized (lock) {
			if (parametersCacheInstance == null)
			{
				parametersCacheInstance = new WPBMemCacheParametersCache(memcacheClient);
			}
		}
		return parametersCacheInstance;
	}
	
	public WPBPageModulesCache getPageModulesCacheInstance()
	{
		synchronized (lock) {
			if (pageModulesCacheInstance == null)
			{
				pageModulesCacheInstance = new WPBMemCachePageModulesCache(memcacheClient);
			}
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache getFilesCacheInstance()
	{
		synchronized (lock) {
			if (filesCacheInstance == null)
			{
				filesCacheInstance = new WPBMemCacheFilesCache(memcacheClient);
			}
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache getArticlesCacheInstance()
	{
		synchronized (lock) {
			if (articlesCacheInstance == null)
			{
				articlesCacheInstance = new WPBMemCacheArticlesCache(memcacheClient);
			}
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache getMessagesCacheInstance()
	{
		synchronized (lock) {
			if (messagesCacheInstance == null)
			{
				messagesCacheInstance = new WPBMemCacheMessagesCache(memcacheClient); 
			}
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache getProjectCacheInstance()
	{
		synchronized (lock) {
			if (projectCacheInstance == null)
			{
				projectCacheInstance = new WPBMemCacheProjectCache(memcacheClient);
			}
		}
		return projectCacheInstance;
	}
}
