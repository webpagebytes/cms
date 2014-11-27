package com.webpagebytes.cms.cache;

public interface WPBCacheFactory {
	public WPBUrisCache createWBUrisCacheInstance();
	public WPBWebPagesCache createWBWebPagesCacheInstance();
	public WPBWebPageModulesCache createWBWebPageModulesCacheInstance();
	public WPBParametersCache createWBParametersCacheInstance();
	public WPBFilesCache createWBFilesCacheInstance();
	public WPBArticlesCache createWBArticlesCacheInstance();
	public WPBMessagesCache createWBMessagesCacheInstance();
	public WPBProjectCache createWBProjectCacheInstance();
	
}
