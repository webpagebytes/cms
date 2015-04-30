package com.webpagebytes.cms.local;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;

public class WPBMemCacheFactory implements WPBCacheFactory {

	private Object lock = new Object();
	private static WPBUrisCache uriCacheInstance;
	private static WPBPagesCache pageCacheInstance;
	private static WPBParametersCache parametersCacheInstance;
	private static WPBPageModulesCache pageModulesCacheInstance;
	private static WPBFilesCache filesCacheInstance;
	private static WPBArticlesCache articlesCacheInstance;
	private static WPBMessagesCache messagesCacheInstance;
	private static WPBProjectCache projectCacheInstance;
	
	public WPBUrisCache getUrisCacheInstance()
	{
		synchronized (lock) {			
			if (null == uriCacheInstance)
			{
				uriCacheInstance = new WPBMemCacheUrisCache();
			}
		}
		return uriCacheInstance;
	}
	public WPBPagesCache getWebPagesCacheInstance()
	{
		synchronized (lock) {		
			if (null == pageCacheInstance)
			{
				pageCacheInstance = new WPBMemCachePagesCache();
			}
		}
		return pageCacheInstance;
	}
	public WPBParametersCache getParametersCacheInstance()
	{
		synchronized (lock) {
			if (parametersCacheInstance == null)
			{
				parametersCacheInstance = new WPBMemCacheParametersCache();
			}
		}
		return parametersCacheInstance;
	}
	
	public WPBPageModulesCache getPageModulesCacheInstance()
	{
		synchronized (lock) {
			if (pageModulesCacheInstance == null)
			{
				pageModulesCacheInstance = new WPBMemCachePageModulesCache();
			}
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache getFilesCacheInstance()
	{
		synchronized (lock) {
			if (filesCacheInstance == null)
			{
				filesCacheInstance = new WPBMemCacheFilesCache();
			}
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache getArticlesCacheInstance()
	{
		synchronized (lock) {
			if (articlesCacheInstance == null)
			{
				articlesCacheInstance = new WPBMemCacheArticlesCache();
			}
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache getMessagesCacheInstance()
	{
		synchronized (lock) {
			if (messagesCacheInstance == null)
			{
				messagesCacheInstance = new WPBMemCacheMessagesCache(); 
			}
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache getProjectCacheInstance()
	{
		synchronized (lock) {
			if (projectCacheInstance == null)
			{
				projectCacheInstance = new WPBMemCacheProjectCache();
			}
		}
		return projectCacheInstance;
	}
}
