package com.webpagebytes.cms;

import java.util.HashMap;
import java.util.Locale;

import com.webpagebytes.cms.appinterfaces.WPBContentService;
import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBFilesCache;
import com.webpagebytes.cms.cache.WBMessagesCache;
import com.webpagebytes.cms.cache.WBParametersCache;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.cache.WBWebPagesCache;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBLocaleException;

public class WPBCmsContentService implements WPBContentService {
	
	private WBCacheInstances cacheInstances;
	private ModelBuilder modelBuilder;
	private WBContentProvider contentProvider;
	
	public WPBCmsContentService()
	{
		WBCacheFactory cacheFactory = DefaultWBCacheFactory.getInstance();
		cacheInstances = new WBCacheInstances(cacheFactory);
		modelBuilder = new ModelBuilder(cacheInstances);
		
	}
	public WBModel createModel(String language, String country) throws WBException
	{
		WBModel model =  new WBModel();
		String lcid = language;
		if (country != null && country.length()>0)
		{
			country = country.toUpperCase();
			lcid = language.toLowerCase() + "_" + country;
		}
		if (! cacheInstances.getProjectCache().getSupportedLocales().contains(lcid))
		{
			throw new WBLocaleException("Not supported locale");
		}
		modelBuilder.populateLocale(language, country, model);
		modelBuilder.populateGlobalParameters(model);
		
		return model;
	}
	public WBModel createModel() throws WBException
	{
		WBModel model =  new WBModel();
		try
		{
			Pair<String, String> defaultLocale = cacheInstances.getProjectCache().getDefaultLocale();
			modelBuilder.populateLocale(defaultLocale.getFirst(), defaultLocale.getSecond(), model);	
			modelBuilder.populateGlobalParameters(model);
		} catch (WBIOException e)
		{
			throw e;
		}
		return model;
	}
	
	private void initializeContentProvider() throws WBException
	{
		PageContentBuilder pageContentBuilder = new PageContentBuilder(cacheInstances, modelBuilder);
		pageContentBuilder.initialize();
		FileContentBuilder fileContentBuilder = new FileContentBuilder(cacheInstances);
		fileContentBuilder.initialize();	
		
		contentProvider = new WBDefaultContentProvider(fileContentBuilder, pageContentBuilder);
	}
	
	public WBContentProvider getContentProvider() throws WBException
	{
		if (null == contentProvider)
		{
			initializeContentProvider();
		}
		return contentProvider;
	}

}
