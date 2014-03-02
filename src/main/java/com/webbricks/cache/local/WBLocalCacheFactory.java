package com.webbricks.cache.local;


import com.webbricks.cache.WBArticlesCache;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBFilesCache;
import com.webbricks.cache.WBMessagesCache;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cache.WBProjectCache;
import com.webbricks.cache.WBUrisCache;
import com.webbricks.cache.WBWebPageModulesCache;
import com.webbricks.cache.WBWebPagesCache;

public class WBLocalCacheFactory implements WBCacheFactory {
	
	private static WBUrisCache uriCacheInstance;
	private static WBWebPagesCache pageCacheInstance;
	private static WBParametersCache parametersCacheInstance;
	private static WBWebPageModulesCache pageModulesCacheInstance;
	private static WBFilesCache filesCacheInstance;
	private static WBArticlesCache articlesCacheInstance;
	private static WBMessagesCache messagesCacheInstance;
	private static WBProjectCache projectCacheInstance;
	
	public WBUrisCache createWBUrisCacheInstance()
	{
		if (null == uriCacheInstance)
		{
			uriCacheInstance = new WBLocalUrisCache();
		}
		return uriCacheInstance;
	}
	public WBWebPagesCache createWBWebPagesCacheInstance()
	{
		if (null == pageCacheInstance)
		{
			pageCacheInstance = new WBLocalWebPagesCache();
		}
		return pageCacheInstance;
	}
	public WBParametersCache createWBParametersCacheInstance()
	{
		if (parametersCacheInstance == null)
		{
			parametersCacheInstance = new WBLocalParametersCache();
		}
		return parametersCacheInstance;
	}
	
	public WBWebPageModulesCache createWBWebPageModulesCacheInstance()
	{
		if (pageModulesCacheInstance == null)
		{
			pageModulesCacheInstance = new WBLocalWebPageModulesCache();
		}
		return pageModulesCacheInstance;
	}
	public WBFilesCache createWBImagesCacheInstance()
	{
		if (filesCacheInstance == null)
		{
			filesCacheInstance = new WBLocalFilesCache();
		}
		return filesCacheInstance;
	}
	public WBArticlesCache createWBArticlesCacheInstance()
	{
		if (articlesCacheInstance == null)
		{
			articlesCacheInstance = new WBLocalArticlesCache();
		}
		return articlesCacheInstance;
	}
	public WBMessagesCache createWBMessagesCacheInstance()
	{
		if (messagesCacheInstance == null)
		{
			messagesCacheInstance = new WBLocalMessagesCache(); 
		}
		return messagesCacheInstance;
	}
	public WBProjectCache createWBProjectCacheInstance()
	{
		if (projectCacheInstance == null)
		{
			projectCacheInstance = new WBLocalProjectCache();
		}
		return projectCacheInstance;
	}
}