package com.webbricks.cache;

public class WBCacheInstances {
	private WBUriCache wbUriCache;
	private WBWebPageCache wbWebPageCache;
	private WBWebPageModuleCache wbWebPageModuleCache;
	private WBParameterCache wbParameterCache;
	private WBImageCache wbImageCache;
	private WBArticleCache wbArticleCache;
	private WBMessageCache wbMessageCache;
	private WBProjectCache wbProjectCache;
	public WBCacheInstances(WBUriCache uriCache,
							WBWebPageCache webPageCache,
							WBWebPageModuleCache webPageModuleCache,
							WBParameterCache parameterCache,
							WBImageCache imageCache,
							WBArticleCache articleCache,
							WBMessageCache messageCache,
							WBProjectCache projectCache)
	{
		wbArticleCache = articleCache;
		wbUriCache = uriCache;
		wbWebPageCache = webPageCache;
		wbWebPageModuleCache = webPageModuleCache;
		wbParameterCache = parameterCache;
		wbImageCache = imageCache;
		wbMessageCache = messageCache;
		wbProjectCache = projectCache;
	}
	
	public WBMessageCache getWBMessageCache()
	{
		return wbMessageCache;
	}
	public WBWebPageCache getWBWebPageCache()
	{
		return wbWebPageCache;
	}
	public WBWebPageModuleCache getWBWebPageModuleCache()
	{
		return wbWebPageModuleCache;
	}
	public WBImageCache getWBImageCache()
	{
		return wbImageCache;
	}
	public WBUriCache getWBUriCache()
	{
		return wbUriCache;
	}
	public WBArticleCache getWBArticleCache()
	{
		return wbArticleCache;
	}
	public WBParameterCache getWBParameterCache()
	{
		return wbParameterCache;
	}
	public WBProjectCache getProjectCache()
	{
		return wbProjectCache;
	}
}
