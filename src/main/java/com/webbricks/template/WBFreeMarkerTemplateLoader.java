package com.webbricks.template;

import java.io.IOException;

import java.io.Reader;
import java.io.StringReader;

import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBWebPageCache;
import com.webbricks.cache.WBWebPageModuleCache;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;
import com.webbricks.template.WBFreeMarkerTemplateObject.TemplateType;

import freemarker.cache.TemplateLoader;

public class WBFreeMarkerTemplateLoader implements TemplateLoader {
	
	private String webPagesPathPrefix;
	private String webModulesPathPrefix;

	private WBCacheInstances cacheInstances;
	
	public WBFreeMarkerTemplateLoader()
	{
		
	}
	WBFreeMarkerTemplateLoader(WBCacheInstances wbCacheInstances)
	{
		setWebModulesPathPrefix(WBTemplateEngine.WEBMODULES_PATH_PREFIX);
		setWebPagesPathPrefix(WBTemplateEngine.WEBPAGES_PATH_PREFIX);

		this.cacheInstances = wbCacheInstances; 
	}
	
	private Object findTemplateWebPageSource(String name)
	{
		try
		{
			WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(name);
			if (null != wbWebPage)
			{
				return new WBFreeMarkerTemplateObject(name, WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE, wbWebPage.getLastModified().getTime());
			}
		} catch (WBIOException e)
		{
			//define logging here
		}
		return null;
	}
	private Object findTemplateWebModuleSource(String name)
	{
		try
		{
			WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(name);
			if (null != wbWebPageModule)
			{
				return new WBFreeMarkerTemplateObject(name, WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE, wbWebPageModule.getLastModified().getTime());
			}
		} catch (WBIOException e)
		{
			//define logging here
		}
		return null;
	}
	
	public Object findTemplateSource(java.lang.String name) throws IOException
    {
		if (name.startsWith(webPagesPathPrefix))
		{
			return findTemplateWebPageSource(name.substring(webPagesPathPrefix.length()));
		} else
		if (name.startsWith(webModulesPathPrefix)){
			return findTemplateWebModuleSource(name.substring(webModulesPathPrefix.length()));
		} else
		return null;
    }
	
	public long getLastModified(Object templateSource)
	{
		if (templateSource == null)
		{
			return 0L;
		}
		WBFreeMarkerTemplateObject templateObject = (WBFreeMarkerTemplateObject)templateSource;
		TemplateType templateObjectType = templateObject.getType();
		if (templateObjectType == WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE)
		{
			try
			{
				WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(templateObject.getName());
				if (null != wbWebPage)
				{
					return wbWebPage.getLastModified().getTime();
				}
			} catch (WBIOException e)
			{
				return 0L;
			}
		} 
		if (templateObjectType == WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE)
		{
			try
			{
				WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(templateObject.getName());
				if (null != wbWebPageModule)
				{
					return wbWebPageModule.getLastModified().getTime();
				}
			} catch (WBIOException e)
			{
				return 0L;
			}
		}
			
		return 0L;
	}
	
	public Reader getReader(Object templateSource,
            String encoding) throws IOException
    {
		if (templateSource == null)
		{
			throw new IOException ("No reader for null templateSource");
		}
		WBFreeMarkerTemplateObject templateObject = (WBFreeMarkerTemplateObject)templateSource;
		TemplateType templateObjectType = templateObject.getType();
		
		if (templateObjectType == WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_PAGE)
		{
			try
			{
				WBWebPage wbWebPage = cacheInstances.getWBWebPageCache().get(templateObject.getName());
				if (null != wbWebPage)
				{
					return new StringReader(wbWebPage.getHtmlSource());
				}
			} catch (WBIOException e)
			{
				throw new IOException(e);
			}
		}
		if (templateObjectType == WBFreeMarkerTemplateObject.TemplateType.TEMPLATE_MODULE)
		{
			try
			{
				WBWebPageModule wbWebPageModule = cacheInstances.getWBWebPageModuleCache().get(templateObject.getName());
				if (null != wbWebPageModule)
				{
					return new StringReader(wbWebPageModule.getHtmlSource());
				}
			} catch (WBIOException e)
			{
				throw new IOException(e);
			}
		}
		throw new IOException ("Could not find any template reader");
    }
	
	public void closeTemplateSource(Object templateSource) throws IOException
    {
		
    }
	public String getWebPagesPathPrefix() {
		return webPagesPathPrefix;
	}
	public void setWebPagesPathPrefix(String webPagesPathPrefix) {
		this.webPagesPathPrefix = webPagesPathPrefix;
	}
	public String getWebModulesPathPrefix() {
		return webModulesPathPrefix;
	}
	public void setWebModulesPathPrefix(String webModulesPathPrefix) {
		this.webModulesPathPrefix = webModulesPathPrefix;
	}
	
}
