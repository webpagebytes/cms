package com.webpagebytes.cms;

import com.webpagebytes.cms.appinterfaces.WPBContentService;
import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.exception.WBLocaleException;
import com.webpagebytes.cms.utility.Pair;

public class WPBCmsContentService implements WPBContentService {
	
	private WPBCacheInstances cacheInstances;
	private ModelBuilder modelBuilder;
	private WPBContentProvider contentProvider;
	
	private WPBCacheInstances createCacheInstances(WPBCacheFactory cacheFactory)
	{
		return new WPBCacheInstances(cacheFactory);
	}
	private ModelBuilder createModelBuilder(WPBCacheInstances cacheInstances)
	{
		return new ModelBuilder(cacheInstances);
	}
	private WPBCacheFactory createCacheFactory()
	{
		return  DefaultWPBCacheFactory.getInstance();
	}
	private PageContentBuilder createPageContentBuilder(WPBCacheInstances cacheInstances, ModelBuilder modelBuilder)
	{
		return new PageContentBuilder(cacheInstances, modelBuilder);
	}
	private FileContentBuilder createFileContentBuilder(WPBCacheInstances cacheInstances)
	{
		return new FileContentBuilder(cacheInstances);
	}
	public WPBCmsContentService()
	{
		WPBCacheFactory cacheFactory = createCacheFactory();
		cacheInstances = createCacheInstances(cacheFactory);
		modelBuilder = createModelBuilder(cacheInstances);
		
	}
	public WPBModel createModel(String language, String country) throws WBException
	{
		WPBModel model =  new WPBModel();
		String lcid = language.toLowerCase();
		language = language.toLowerCase();
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
	public WPBModel createModel() throws WBException
	{
		WPBModel model =  new WPBModel();
		Pair<String, String> defaultLocale = cacheInstances.getProjectCache().getDefaultLocale();
		modelBuilder.populateLocale(defaultLocale.getFirst(), defaultLocale.getSecond(), model);	
		modelBuilder.populateGlobalParameters(model);
		return model;
	}
	
	private void initializeContentProvider() throws WBException
	{
		PageContentBuilder pageContentBuilder = createPageContentBuilder(cacheInstances, modelBuilder);
		pageContentBuilder.initialize();
		FileContentBuilder fileContentBuilder = createFileContentBuilder(cacheInstances);
		fileContentBuilder.initialize();	
		
		contentProvider = new WPBDefaultContentProvider(fileContentBuilder, pageContentBuilder);
	}
	
	public WPBContentProvider getContentProvider() throws WBException
	{
		if (null == contentProvider)
		{
			initializeContentProvider();
		}
		return contentProvider;
	}

}
