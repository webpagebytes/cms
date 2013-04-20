package com.webbricks.cache;

import java.util.Locale;
import java.util.Map;

import com.webbricks.exception.WBIOException;

public class GaeWBCacheFactory implements WBCacheFactory {
	public WBUriCache createWBUriCacheInstance()
	{
		return new GaeWBUriCache();
	}
	public WBWebPageCache createWBWebPageCacheInstance()
	{
		return new GaeWBWebPageCache();
	}
	public WBParameterCache createWBParameterCacheInstance()
	{
		return new GaeWBParameterCache();
	}
	public WBWebPageModuleCache createWBWebPageModuleCacheInstance()
	{
		return new GaeWBWebPageModuleCache();
	}
	public WBImageCache createWBImageCacheInstance()
	{
		return new GaeWBImageCache();
	}
	public WBArticleCache createWBArticleCacheInstance()
	{
		return new GaeWBArticleCache();
	}
	public WBMessageCache createWBMessageCacheInstance()
	{
		return new GaeWBMessageCache();
	}
	public WBProjectCache createWBProjectCacheInstance()
	{
		return new GaeWBProjectCache();
	}
}
