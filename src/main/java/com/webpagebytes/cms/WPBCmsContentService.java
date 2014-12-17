/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms;

import com.webpagebytes.cms.appinterfaces.WPBCacheFactory;

import com.webpagebytes.cms.appinterfaces.WPBContentService;
import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBLocaleException;
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
	public WPBModel createModel(String language, String country) throws WPBException
	{
		InternalModel model =  new InternalModel();
		String lcid = language.toLowerCase();
		language = language.toLowerCase();
		if (country != null && country.length()>0)
		{
			country = country.toUpperCase();
			lcid = language.toLowerCase() + "_" + country;
		}
		if (! cacheInstances.getProjectCache().getSupportedLocales().contains(lcid))
		{
			throw new WPBLocaleException("Not supported locale");
		}
		modelBuilder.populateLocale(language, country, model);
		modelBuilder.populateGlobalParameters(model);
		
		return model;
	}
	public WPBModel createModel() throws WPBException
	{
		InternalModel model =  new InternalModel();
		Pair<String, String> defaultLocale = cacheInstances.getProjectCache().getDefaultLocale();
		modelBuilder.populateLocale(defaultLocale.getFirst(), defaultLocale.getSecond(), model);	
		modelBuilder.populateGlobalParameters(model);
		return model;
	}
	
	private void initializeContentProvider() throws WPBException
	{
		PageContentBuilder pageContentBuilder = createPageContentBuilder(cacheInstances, modelBuilder);
		pageContentBuilder.initialize();
		FileContentBuilder fileContentBuilder = createFileContentBuilder(cacheInstances);
		fileContentBuilder.initialize();	
		
		contentProvider = new WPBDefaultContentProvider(fileContentBuilder, pageContentBuilder);
	}
	
	public WPBContentProvider getContentProvider() throws WPBException
	{
		if (null == contentProvider)
		{
			initializeContentProvider();
		}
		return contentProvider;
	}

}
