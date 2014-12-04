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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBException;

public class ResourcesController extends Controller {
	private WPBAdminDataStorage adminStorage;
	
	public ResourcesController() {
		adminStorage = WPBAdminDataStorageFactory.getInstance();
	}
	
	public void getAllResources(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			List<WPBResource> allResources = adminStorage.getAllRecords(WPBResource.class);			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(allResources));
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void refreshResources(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			adminStorage.deleteAllRecords(WPBResource.class);
			List<WPBUri> uris = adminStorage.getAllRecords(WPBUri.class);
			for( WPBUri uri: uris)
			{
				WPBResource res = new WPBResource(uri.getExternalKey(), uri.getUri(), WPBResource.URI_TYPE);
				adminStorage.addWithKey(res);
			}
			List<WPBWebPage> pages = adminStorage.getAllRecords(WPBWebPage.class);
			for( WPBWebPage page: pages)
			{
				WPBResource res = new WPBResource(page.getExternalKey(), page.getName(), WPBResource.PAGE_TYPE);
				adminStorage.addWithKey(res);
			}
			List<WPBWebPageModule> modules = adminStorage.getAllRecords(WPBWebPageModule.class);
			for( WPBWebPageModule module: modules)
			{
				WPBResource res = new WPBResource(module.getExternalKey(), module.getName(), WPBResource.PAGE_MODULE_TYPE);
				adminStorage.addWithKey(res);
			}
					
			List<WPBArticle> articles = adminStorage.getAllRecords(WPBArticle.class);
			for( WPBArticle article: articles)
			{
				WPBResource res = new WPBResource(article.getExternalKey(), article.getTitle(), WPBResource.ARTICLE_TYPE);
				adminStorage.addWithKey(res);
			}

			List<WPBFile> files = adminStorage.getAllRecords(WPBFile.class);
			for( WPBFile file: files)
			{
				WPBResource res = new WPBResource(file.getExternalKey(), file.getName(), WPBResource.FILE_TYPE);
				adminStorage.addWithKey(res);
			}
			
			List<WPBMessage> messages = adminStorage.getAllRecords(WPBMessage.class);
			Set<String> setMessages = new HashSet<String>();
			for( WPBMessage message: messages)
			{
				setMessages.add(message.getName());
			}
			for(String item: setMessages)
			{
				WPBResource res = new WPBResource(item, item, WPBResource.MESSAGE_TYPE);
				adminStorage.addWithKey(res);
			}

			List<WPBParameter> parameters = adminStorage.query(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, "");
			for(WPBParameter param: parameters)
			{
				WPBResource res = new WPBResource(param.getExternalKey(), param.getName(), WPBResource.GLOBAL_PARAMETER_TYPE);
				adminStorage.addWithKey(res);
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
}
