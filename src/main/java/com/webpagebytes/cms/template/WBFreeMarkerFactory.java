package com.webpagebytes.cms.template;

import java.util.Locale;

import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBMessagesCache;

import freemarker.template.Configuration;

public class WBFreeMarkerFactory {

	public WBResourceBundle createResourceBundle(WPBMessagesCache messageCache, Locale locale)
	{
		return new WBResourceBundle(messageCache, locale);
	}
	public Configuration createConfiguration()
	{
		return new Configuration();
	}
	
	public WBFreeMarkerTemplateLoader createWBFreeMarkerTemplateLoader(WPBCacheInstances cacheInstances)
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
