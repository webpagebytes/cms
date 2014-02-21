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
	public WBUrisCache createWBUrisCacheInstance()
	{
		return new WBLocalUrisCache();
	}
	public WBWebPagesCache createWBWebPagesCacheInstance()
	{
		return new WBLocalWebPagesCache();
	}
	public WBParametersCache createWBParametersCacheInstance()
	{
		return new WBLocalParametersCache();
	}
	public WBWebPageModulesCache createWBWebPageModulesCacheInstance()
	{
		return new WBLocalWebPageModulesCache();
	}
	public WBFilesCache createWBImagesCacheInstance()
	{
		return new WBLocalFilesCache();
	}
	public WBArticlesCache createWBArticlesCacheInstance()
	{
		return new WBLocalArticlesCache();
	}
	public WBMessagesCache createWBMessagesCacheInstance()
	{
		return new WBLocalMessagesCache();
	}
	public WBProjectCache createWBProjectCacheInstance()
	{
		return new WBLocalProjectCache();
	}
}