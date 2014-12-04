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

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBArticlesCache;
import com.webpagebytes.cms.appinterfaces.WPBFilesCache;
import com.webpagebytes.cms.appinterfaces.WPBMessagesCache;
import com.webpagebytes.cms.appinterfaces.WPBParametersCache;
import com.webpagebytes.cms.appinterfaces.WPBProjectCache;
import com.webpagebytes.cms.appinterfaces.WPBUrisCache;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.appinterfaces.WPBWebPagesCache;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.WPBCloudFile;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class CleanerController extends Controller implements WPBAdminDataStorageListener<Object>{
	private WPBAdminDataStorage adminStorage;
	private WPBCacheFactory cacheFactory;
	private WPBCloudFileStorage cloudFileStorage;
	
	public CleanerController()
	{
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();

		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new JSONToFromObjectConverter();
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		cacheFactory = DefaultWPBCacheFactory.getInstance();
		adminStorage.addStorageListener(this);
	}
	
	public void notify(
			Object t,
			AdminDataStorageOperation operation,
			Class type) {	
		try
		{
			if (type.equals(WPBUri.class))
			{
				WPBUrisCache urisCache = cacheFactory.getUrisCacheInstance();
				urisCache.Refresh();
			}
			if (type.equals(WPBWebPage.class))
			{
				WPBWebPagesCache pagesCache = cacheFactory.getWebPagesCacheInstance();
				pagesCache.Refresh();
			}
			if (type.equals(WPBWebPageModule.class))
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
	private void deleteFile(WPBFile file) throws IOException
	{
		if (file.getBlobKey() != null)
		{
			WPBCloudFile cloudFile = new WPBCloudFile(FileController.PUBLIC_BUCKET, file.getBlobKey());
			cloudFileStorage.deleteFile(cloudFile);
		}
		if (file.getThumbnailBlobKey() != null)
		{
			WPBCloudFile cloudThumbnailFile = new WPBCloudFile(FileController.PUBLIC_BUCKET, file.getThumbnailBlobKey());
			cloudFileStorage.deleteFile(cloudThumbnailFile);
		}						
	}
	public void deleteAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			adminStorage.deleteAllRecords(WPBUri.class);
			adminStorage.deleteAllRecords(WPBWebPage.class);
			adminStorage.deleteAllRecords(WPBWebPageModule.class);
			adminStorage.deleteAllRecords(WPBArticle.class);
			adminStorage.deleteAllRecords(WPBMessage.class);
			adminStorage.deleteAllRecords(WPBParameter.class);
			adminStorage.deleteAllRecords(WPBProject.class);
			List<WPBFile> files = adminStorage.getAllRecords(WPBFile.class);
			for(WPBFile file: files)
			{
				deleteFile(file);
			}
			adminStorage.deleteAllRecords(WPBFile.class);

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
