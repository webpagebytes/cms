package com.webpagebytes.cms;

import com.webpagebytes.cms.appinterfaces.WPBContentService;
import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;
import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBLocaleException;

public class WPBCmsContentService implements WPBContentService {
	
	private WBCacheInstances cacheInstances;
	private ModelBuilder modelBuilder;
	private WBContentProvider contentProvider;
	
	private WBCacheInstances createCacheInstances(WBCacheFactory cacheFactory)
	{
		return new WBCacheInstances(cacheFactory);
	}
	private ModelBuilder createModelBuilder(WBCacheInstances cacheInstances)
	{
		return new ModelBuilder(cacheInstances);
	}
	private WBCacheFactory createCacheFactory()
	{
		return  DefaultWBCacheFactory.getInstance();
	}
	private PageContentBuilder createPageContentBuilder(WBCacheInstances cacheInstances, ModelBuilder modelBuilder)
	{
		return new PageContentBuilder(cacheInstances, modelBuilder);
	}
	private FileContentBuilder createFileContentBuilder(WBCacheInstances cacheInstances)
	{
		return new FileContentBuilder(cacheInstances);
	}
	public WPBCmsContentService()
	{
		WBCacheFactory cacheFactory = createCacheFactory();
		cacheInstances = createCacheInstances(cacheFactory);
		modelBuilder = createModelBuilder(cacheInstances);
		
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
		PageContentBuilder pageContentBuilder = createPageContentBuilder(cacheInstances, modelBuilder);
		pageContentBuilder.initialize();
		FileContentBuilder fileContentBuilder = createFileContentBuilder(cacheInstances);
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
