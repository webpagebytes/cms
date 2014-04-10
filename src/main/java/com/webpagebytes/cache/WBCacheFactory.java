package com.webpagebytes.cache;

public interface WBCacheFactory {
	public WBUrisCache createWBUrisCacheInstance();
	public WBWebPagesCache createWBWebPagesCacheInstance();
	public WBWebPageModulesCache createWBWebPageModulesCacheInstance();
	public WBParametersCache createWBParametersCacheInstance();
	public WBFilesCache createWBImagesCacheInstance();
	public WBArticlesCache createWBArticlesCacheInstance();
	public WBMessagesCache createWBMessagesCacheInstance();
	public WBProjectCache createWBProjectCacheInstance();
	
}
