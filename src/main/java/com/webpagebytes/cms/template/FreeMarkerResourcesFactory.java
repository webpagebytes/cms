package com.webpagebytes.cms.template;

import java.util.Locale;

import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBMessagesCache;

import freemarker.template.Configuration;

class FreeMarkerResourcesFactory {

	public CmsResourceBundle createResourceBundle(WPBMessagesCache messageCache, Locale locale)
	{
		return new CmsResourceBundle(messageCache, locale);
	}
	public Configuration createConfiguration()
	{
		return new Configuration();
	}
	
	public FreeMarkerTemplateLoader createWBFreeMarkerTemplateLoader(WPBCacheInstances cacheInstances)
	{
		return new FreeMarkerTemplateLoader(cacheInstances);
	}
	
	public FreeMarkerModuleDirective createWBFreeMarkerModuleDirective()
	{
		return new FreeMarkerModuleDirective();
	}
	public FreeMarkerImageDirective createWBFreeMarkerImageDirective()
	{
		return new FreeMarkerImageDirective();
	}
	
	public FreeMarkerArticleDirective createWBFreeMarkerArticleDirective()
	{
		return new FreeMarkerArticleDirective();
	}

}
