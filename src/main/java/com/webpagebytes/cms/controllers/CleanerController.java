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
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
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
			if (type.equals(WBUri.class))
			{
				WPBUrisCache urisCache = cacheFactory.createWBUrisCacheInstance();
				urisCache.Refresh();
			}
			if (type.equals(WBWebPage.class))
			{
				WPBWebPagesCache pagesCache = cacheFactory.createWBWebPagesCacheInstance();
				pagesCache.Refresh();
			}
			if (type.equals(WBWebPageModule.class))
			{
				WPBWebPageModulesCache modulesCache = cacheFactory.createWBWebPageModulesCacheInstance();
				modulesCache.Refresh();
			}
			if (type.equals(WBMessage.class))
			{
				WPBMessagesCache messagesCache = cacheFactory.createWBMessagesCacheInstance();
				messagesCache.Refresh();
			}
			if (type.equals(WBArticle.class))
			{
				WPBArticlesCache articlesCache = cacheFactory.createWBArticlesCacheInstance();
				articlesCache.Refresh();
			}
			if (type.equals(WBFile.class))
			{
				WPBFilesCache filesCache = cacheFactory.createWBFilesCacheInstance();
				filesCache.Refresh();
			}
			if (type.equals(WBParameter.class))
			{
				WPBParametersCache parametersCache = cacheFactory.createWBParametersCacheInstance();
				parametersCache.Refresh();
			}
			if (type.equals(WBProject.class))
			{
				WPBProjectCache projectCache = cacheFactory.createWBProjectCacheInstance();
				projectCache.Refresh();
			}
			
		} catch (WPBException e)
		{
			// do nothing
		}
	}
	private void deleteFile(WBFile file) throws IOException
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
			adminStorage.deleteAllRecords(WBUri.class);
			adminStorage.deleteAllRecords(WBWebPage.class);
			adminStorage.deleteAllRecords(WBWebPageModule.class);
			adminStorage.deleteAllRecords(WBArticle.class);
			adminStorage.deleteAllRecords(WBMessage.class);
			adminStorage.deleteAllRecords(WBParameter.class);
			adminStorage.deleteAllRecords(WBProject.class);
			List<WBFile> files = adminStorage.getAllRecords(WBFile.class);
			for(WBFile file: files)
			{
				deleteFile(file);
			}
			adminStorage.deleteAllRecords(WBFile.class);

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
