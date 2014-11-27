package com.webpagebytes.cms.controllers;

import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBArticlesCache;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBFilesCache;
import com.webpagebytes.cms.cache.WPBMessagesCache;
import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cache.WPBProjectCache;
import com.webpagebytes.cms.cache.WPBUrisCache;
import com.webpagebytes.cms.cache.WPBWebPageModulesCache;
import com.webpagebytes.cms.cache.WPBWebPagesCache;
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
				WPBUrisCache urisCache = cacheFactory.createWBUrisCacheInstance();
				urisCache.Refresh();
			}
			if (type.equals(WPBWebPage.class))
			{
				WPBWebPagesCache pagesCache = cacheFactory.createWBWebPagesCacheInstance();
				pagesCache.Refresh();
			}
			if (type.equals(WPBWebPageModule.class))
			{
				WPBWebPageModulesCache modulesCache = cacheFactory.createWBWebPageModulesCacheInstance();
				modulesCache.Refresh();
			}
			if (type.equals(WPBMessage.class))
			{
				WPBMessagesCache messagesCache = cacheFactory.createWBMessagesCacheInstance();
				messagesCache.Refresh();
			}
			if (type.equals(WPBArticle.class))
			{
				WPBArticlesCache articlesCache = cacheFactory.createWBArticlesCacheInstance();
				articlesCache.Refresh();
			}
			if (type.equals(WPBFile.class))
			{
				WPBFilesCache filesCache = cacheFactory.createWBFilesCacheInstance();
				filesCache.Refresh();
			}
			if (type.equals(WPBParameter.class))
			{
				WPBParametersCache parametersCache = cacheFactory.createWBParametersCacheInstance();
				parametersCache.Refresh();
			}
			if (type.equals(WPBProject.class))
			{
				WPBProjectCache projectCache = cacheFactory.createWBProjectCacheInstance();
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
