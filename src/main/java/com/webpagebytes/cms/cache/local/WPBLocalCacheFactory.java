package com.webpagebytes.cms.cache.local;


import com.webpagebytes.cms.cache.WPBArticlesCache;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cache.WPBMessagesCache;
import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.cache.WPBUrisCache;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cache.WPBWebPagesCache;

public class WPBLocalCacheFactory implements WPBCacheFactory {
	
	private static WPBUrisCache uriCacheInstance;
	private static WPBWebPagesCache pageCacheInstance;
	private static WPBParametersCache parametersCacheInstance;
	private static WPBWebPageModulesCache pageModulesCacheInstance;
	private static WPBFilesCache filesCacheInstance;
	private static WPBArticlesCache articlesCacheInstance;
	private static WPBMessagesCache messagesCacheInstance;
	private static WPBProjectCache projectCacheInstance;
	
	public WPBUrisCache createWBUrisCacheInstance()
	{
		if (null == uriCacheInstance)
		{
			uriCacheInstance = new WPBLocalUrisCache();
		}
		return uriCacheInstance;
	}
	public WPBWebPagesCache createWBWebPagesCacheInstance()
	{
		if (null == pageCacheInstance)
		{
			pageCacheInstance = new WPBLocalWebPagesCache();
		}
		return pageCacheInstance;
	}
	public WPBParametersCache createWBParametersCacheInstance()
	{
		if (parametersCacheInstance == null)
		{
			parametersCacheInstance = new WPBLocalParametersCache();
		}
		return parametersCacheInstance;
	}
	
	public WPBWebPageModulesCache createWBWebPageModulesCacheInstance()
	{
		if (pageModulesCacheInstance == null)
		{
			pageModulesCacheInstance = new WPBLocalWebPageModulesCache();
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache createWBFilesCacheInstance()
	{
		if (filesCacheInstance == null)
		{
			filesCacheInstance = new WPBLocalFilesCache();
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache createWBArticlesCacheInstance()
	{
		if (articlesCacheInstance == null)
		{
			articlesCacheInstance = new WPBLocalArticlesCache();
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache createWBMessagesCacheInstance()
	{
		if (messagesCacheInstance == null)
		{
			messagesCacheInstance = new WPBLocalMessagesCache(); 
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache createWBProjectCacheInstance()
	{
		if (projectCacheInstance == null)
		{
			projectCacheInstance = new WPBLocalProjectCache();
		}
		return projectCacheInstance;
	}
}