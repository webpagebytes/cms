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

package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.cmsdata.WPBPageModule;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.engine.WPBAdminDataStorageListener;
import com.webpagebytes.cms.exception.WPBException;

public class CleanerController extends Controller implements WPBAdminDataStorageListener{
	
	private WPBCacheFactory cacheFactory;
	
	public CleanerController()
	{
		cacheFactory = DefaultWPBCacheFactory.getInstance();
		adminStorage.addStorageListener(this);
	}
	
	public<T> void notify(
			T t,
			AdminDataStorageOperation operation,
			Class<? extends Object> type) {	
		try
		{
			if (type.equals(WPBUri.class))
			{
				WPBUrisCache urisCache = cacheFactory.getUrisCacheInstance();
				urisCache.Refresh();
			}
			if (type.equals(WPBPage.class))
			{
				WPBPagesCache pagesCache = cacheFactory.getWebPagesCacheInstance();
				pagesCache.Refresh();
			}
			if (type.equals(WPBPageModule.class))
			{
				WPBPageModulesCache modulesCache = cacheFactory.getPageModulesCacheInstance();
				modulesCache.Refresh();
			}
			if (type.equals(WPBMessage.class))
			{
				WPBMessagesCache messagesCache = cacheFactory.getMessagesCacheInstance();
				messagesCache.Refresh();
			}
			if (type.equals(WPBArticle.class))
			{
				WPBArticlesCache articlesCache = cacheFactory.getArticlesCacheInstance();
				articlesCache.Refresh();
			}
			if (type.equals(WPBFile.class))
			{
				WPBFilesCache filesCache = cacheFactory.getFilesCacheInstance();
				filesCache.Refresh();
			}
			if (type.equals(WPBParameter.class))
			{
				WPBParametersCache parametersCache = cacheFactory.getParametersCacheInstance();
				parametersCache.Refresh();
			}
			if (type.equals(WPBProject.class))
			{
				WPBProjectCache projectCache = cacheFactory.getProjectCacheInstance();
				projectCache.Refresh();
			}
			
		} catch (WPBException e)
		{
			// do nothing
		}
	}
	
	public void deleteAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			deleteAll();
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, "{}");			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);

		}
		catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	
	
}
