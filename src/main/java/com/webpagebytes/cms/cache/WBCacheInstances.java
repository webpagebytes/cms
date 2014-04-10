package com.webpagebytes.cms.cache;

public class WBCacheInstances {
	private WBUrisCache wbUriCache;
	private WBWebPagesCache wbWebPageCache;
	private WBWebPageModulesCache wbWebPageModuleCache;
	private WBParametersCache wbParameterCache;
	private WBFilesCache wbFilesCache;
	private WBArticlesCache wbArticleCache;
	private WBMessagesCache wbMessageCache;
	private WBProjectCache wbProjectCache;
	public WBCacheInstances(WBUrisCache uriCache,
							WBWebPagesCache webPageCache,
							WBWebPageModulesCache webPageModuleCache,
							WBParametersCache parameterCache,
							WBFilesCache filesCache,
							WBArticlesCache articleCache,
							WBMessagesCache messageCache,
							WBProjectCache projectCache)
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
	
	public WBMessagesCache getWBMessageCache()
	{
		return wbMessageCache;
	}
	public WBWebPagesCache getWBWebPageCache()
	{
		return wbWebPageCache;
	}
	public WBWebPageModulesCache getWBWebPageModuleCache()
	{
		return wbWebPageModuleCache;
	}
	public WBFilesCache getWBFilesCache()
	{
		return wbFilesCache;
	}
	public WBUrisCache getWBUriCache()
	{
		return wbUriCache;
	}
	public WBArticlesCache getWBArticleCache()
	{
		return wbArticleCache;
	}
	public WBParametersCache getWBParameterCache()
	{
		return wbParameterCache;
	}
	public WBProjectCache getProjectCache()
	{
		return wbProjectCache;
	}
}
