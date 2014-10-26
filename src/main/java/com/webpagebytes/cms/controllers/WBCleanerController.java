package com.webpagebytes.cms.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBArticlesCache;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBFilesCache;
import com.webpagebytes.cms.cache.WBMessagesCache;
import com.webpagebytes.cms.cache.WBParametersCache;
import com.webpagebytes.cms.cache.WBProjectCache;
import com.webpagebytes.cms.cache.WBUrisCache;
import com.webpagebytes.cms.cache.WBWebPageModulesCache;
import com.webpagebytes.cms.cache.WBWebPagesCache;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBProject;
import com.webpagebytes.cms.cmsdata.WBResource;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.datautility.AdminDataStorageListener;
import com.webpagebytes.cms.datautility.WBCloudFile;
import com.webpagebytes.cms.datautility.WBCloudFileStorage;
import com.webpagebytes.cms.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class WBCleanerController extends WBController implements AdminDataStorageListener<Object>{
	private AdminDataStorage adminStorage;
	private WBCacheFactory cacheFactory;
	private WBCloudFileStorage cloudFileStorage;
	
	public WBCleanerController()
	{
		cloudFileStorage = WBCloudFileStorageFactory.getInstance();

		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = AdminDataStorageFactory.getInstance();
		cacheFactory = DefaultWBCacheFactory.getInstance();
		adminStorage.addStorageListener(this);
	}
	@Override
	public void notify(
			Object t,
			AdminDataStorageOperation operation,
			Class type) {	
		try
		{
			if (type.equals(WBUri.class))
			{
				WBUrisCache urisCache = cacheFactory.createWBUrisCacheInstance();
				urisCache.Refresh();
			}
			if (type.equals(WBWebPage.class))
			{
				WBWebPagesCache pagesCache = cacheFactory.createWBWebPagesCacheInstance();
				pagesCache.Refresh();
			}
			if (type.equals(WBWebPageModule.class))
			{
				WBWebPageModulesCache modulesCache = cacheFactory.createWBWebPageModulesCacheInstance();
				modulesCache.Refresh();
			}
			if (type.equals(WBMessage.class))
			{
				WBMessagesCache messagesCache = cacheFactory.createWBMessagesCacheInstance();
				messagesCache.Refresh();
			}
			if (type.equals(WBArticle.class))
			{
				WBArticlesCache articlesCache = cacheFactory.createWBArticlesCacheInstance();
				articlesCache.Refresh();
			}
			if (type.equals(WBFile.class))
			{
				WBFilesCache filesCache = cacheFactory.createWBFilesCacheInstance();
				filesCache.Refresh();
			}
			if (type.equals(WBParameter.class))
			{
				WBParametersCache parametersCache = cacheFactory.createWBParametersCacheInstance();
				parametersCache.Refresh();
			}
			if (type.equals(WBProject.class))
			{
				WBProjectCache projectCache = cacheFactory.createWBProjectCacheInstance();
				projectCache.Refresh();
			}
			
		} catch (WBException e)
		{
			// do nothing
		}
	}
	private void deleteFile(WBFile file) throws IOException
	{
		if (file.getBlobKey() != null)
		{
			WBCloudFile cloudFile = new WBCloudFile(WBFileControllerEx.PUBLIC_BUCKET, file.getBlobKey());
			cloudFileStorage.deleteFile(cloudFile);
		}
		if (file.getThumbnailBlobKey() != null)
		{
			WBCloudFile cloudThumbnailFile = new WBCloudFile(WBFileControllerEx.PUBLIC_BUCKET, file.getThumbnailBlobKey());
			cloudFileStorage.deleteFile(cloudThumbnailFile);
		}						
	}
	public void deleteAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
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
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	
	
}
