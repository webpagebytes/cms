package com.webpagebytes.template;

import java.util.Locale;

import com.webpagebytes.cache.WBCacheInstances;
import com.webpagebytes.cache.WBMessagesCache;

import freemarker.template.Configuration;

public class WBFreeMarkerFactory {

	public WBResourceBundle createResourceBundle(WBMessagesCache messageCache, Locale locale)
	{
		return new WBResourceBundle(messageCache, locale);
	}
	public Configuration createConfiguration()
	{
		return new Configuration();
	}
	
	public WBFreeMarkerTemplateLoader createWBFreeMarkerTemplateLoader(WBCacheInstances cacheInstances)
	{
		return new WBFreeMarkerTemplateLoader(cacheInstances);
	}
	
	public WBFreeMarkerModuleDirective createWBFreeMarkerModuleDirective()
	{
		return new WBFreeMarkerModuleDirective();
	}
	public WBFreeMarkerImageDirective createWBFreeMarkerImageDirective()
	{
		return new WBFreeMarkerImageDirective();
	}
	
	public WBFreeMarkerArticleDirective createWBFreeMarkerArticleDirective()
	{
		return new WBFreeMarkerArticleDirective();
	}

}
