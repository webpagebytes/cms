package com.webpagebytes.cms.cache.local;


import com.webpagebytes.cms.cache.WBArticlesCache;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBFilesCache;
import com.webpagebytes.cms.cache.WBMessagesCache;
import com.webpagebytes.cms.cache.WBParametersCache;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.cache.WBUrisCache;
import com.webpagebytes.cms.cache.WBWebPageModulesCache;
import com.webpagebytes.cms.cache.WBWebPagesCache;

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
	public WBFilesCache createWBFilesCacheInstance()
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