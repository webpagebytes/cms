package com.webpagebytes.cms.cache;

public class WPBCacheInstances {
	private WPBUrisCache wbUriCache;
	private WPBWebPagesCache wbWebPageCache;
	private WPBWebPageModulesCache wbWebPageModuleCache;
	private WPBParametersCache wbParameterCache;
	private WPBFilesCache wbFilesCache;
	private WPBArticlesCache wbArticleCache;
	private WPBMessagesCache wbMessageCache;
	private WPBProjectCache wbProjectCache;
	public WPBCacheInstances(WPBUrisCache uriCache,
							WPBWebPagesCache webPageCache,
							WPBWebPageModulesCache webPageModuleCache,
							WPBParametersCache parameterCache,
							WPBFilesCache filesCache,
							WPBArticlesCache articleCache,
							WPBMessagesCache messageCache,
							WPBProjectCache projectCache)
	{
		wbArticleCache = articleCache;
		wbUriCache = uriCache;
		wbWebPageCache = webPageCache;
		wbWebPageModuleCache = webPageModuleCache;
		wbParameterCache = parameterCache;
		wbFilesCache = filesCache;
		wbMessageCache = messageCache;
		wbProjectCache = projectCache;
	}
	public WPBCacheInstances(WPBCacheFactory cacheFactory)
	{
		wbUriCache = cacheFactory.createWBUrisCacheInstance();
		wbWebPageCache = cacheFactory.createWBWebPagesCacheInstance();
		wbWebPageModuleCache = cacheFactory.createWBWebPageModulesCacheInstance();
		wbParameterCache = cacheFactory.createWBParametersCacheInstance();
		wbFilesCache = cacheFactory.createWBFilesCacheInstance();
		wbArticleCache = cacheFactory.createWBArticlesCacheInstance();
		wbMessageCache = cacheFactory.createWBMessagesCacheInstance();
		wbProjectCache = cacheFactory.createWBProjectCacheInstance();
	}
	public WPBMessagesCache getWBMessageCache()
	{
		return wbMessageCache;
	}
	public WPBWebPagesCache getWBWebPageCache()
	{
		return wbWebPageCache;
	}
	public WPBWebPageModulesCache getWBWebPageModuleCache()
	{
		return wbWebPageModuleCache;
	}
	public WPBFilesCache getWBFilesCache()
	{
		return wbFilesCache;
	}
	public WPBUrisCache getWBUriCache()
	{
		return wbUriCache;
	}
	public WPBArticlesCache getWBArticleCache()
	{
		return wbArticleCache;
	}
	public WPBParametersCache getWBParameterCache()
	{
		return wbParameterCache;
	}
	public WPBProjectCache getProjectCache()
	{
		return wbProjectCache;
	}
}
