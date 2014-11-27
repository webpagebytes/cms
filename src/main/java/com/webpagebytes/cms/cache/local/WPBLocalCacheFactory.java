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
			uriCacheInstance = new WBLocalUrisCache();
		}
		return uriCacheInstance;
	}
	public WPBWebPagesCache createWBWebPagesCacheInstance()
	{
		if (null == pageCacheInstance)
		{
			pageCacheInstance = new WBLocalWebPagesCache();
		}
		return pageCacheInstance;
	}
	public WPBParametersCache createWBParametersCacheInstance()
	{
		if (parametersCacheInstance == null)
		{
			parametersCacheInstance = new WBLocalParametersCache();
		}
		return parametersCacheInstance;
	}
	
	public WPBWebPageModulesCache createWBWebPageModulesCacheInstance()
	{
		if (pageModulesCacheInstance == null)
		{
			pageModulesCacheInstance = new WBLocalWebPageModulesCache();
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache createWBFilesCacheInstance()
	{
		if (filesCacheInstance == null)
		{
			filesCacheInstance = new WBLocalFilesCache();
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache createWBArticlesCacheInstance()
	{
		if (articlesCacheInstance == null)
		{
			articlesCacheInstance = new WBLocalArticlesCache();
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache createWBMessagesCacheInstance()
	{
		if (messagesCacheInstance == null)
		{
			messagesCacheInstance = new WBLocalMessagesCache(); 
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache createWBProjectCacheInstance()
	{
		if (projectCacheInstance == null)
		{
			projectCacheInstance = new WBLocalProjectCache();
		}
		return projectCacheInstance;
	}
}