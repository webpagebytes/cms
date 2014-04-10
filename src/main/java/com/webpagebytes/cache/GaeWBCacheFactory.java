package com.webpagebytes.cache;

import java.util.Locale;
import java.util.Map;

import com.webpagebytes.exception.WBIOException;

public class GaeWBCacheFactory implements WBCacheFactory {
	public WBUrisCache createWBUrisCacheInstance()
	{
		return new GaeWBUrisCache();
	}
	public WBWebPagesCache createWBWebPagesCacheInstance()
	{
		return new GaeWBWebPagesCache();
	}
	public WBParametersCache createWBParametersCacheInstance()
	{
		return new GaeWBParametersCache();
	}
	public WBWebPageModulesCache createWBWebPageModulesCacheInstance()
	{
		return new GaeWBWebPageModulesCache();
	}
	public WBFilesCache createWBImagesCacheInstance()
	{
		return new GaeWBFilesCache();
	}
	public WBArticlesCache createWBArticlesCacheInstance()
	{
		return new GaeWBArticlesCache();
	}
	public WBMessagesCache createWBMessagesCacheInstance()
	{
		return new GaeWBMessagesCache();
	}
	public WBProjectCache createWBProjectCacheInstance()
	{
		return new GaeWBProjectCache();
	}
}
