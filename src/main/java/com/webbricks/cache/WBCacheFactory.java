package com.webbricks.cache;

public interface WBCacheFactory {
	public WBUriCache createWBUriCacheInstance();
	public WBWebPageCache createWBWebPageCacheInstance();
	public WBWebPageModuleCache createWBWebPageModuleCacheInstance();
	public WBParameterCache createWBParameterCacheInstance();
	public WBImageCache createWBImageCacheInstance();
	public WBArticleCache createWBArticleCacheInstance();
	public WBMessageCache createWBMessageCacheInstance();
	public WBProjectCache createWBProjectCacheInstance();
	
}
