package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBResource;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.exception.WBException;

public class ResourcesController extends WBController {
	private AdminDataStorage adminStorage;
	
	public ResourcesController() {
		adminStorage = AdminDataStorageFactory.getInstance();
	}
	
	public void getAllResources(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBResource> allResources = adminStorage.getAllRecords(WBResource.class);			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(allResources));
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void refreshResources(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			adminStorage.deleteAllRecords(WBResource.class);
			List<WBUri> uris = adminStorage.getAllRecords(WBUri.class);
			for( WBUri uri: uris)
			{
				WBResource res = new WBResource(uri.getExternalKey(), uri.getUri(), WBResource.URI_TYPE);
				adminStorage.addWithKey(res);
			}
			List<WBWebPage> pages = adminStorage.getAllRecords(WBWebPage.class);
			for( WBWebPage page: pages)
			{
				WBResource res = new WBResource(page.getExternalKey(), page.getName(), WBResource.PAGE_TYPE);
				adminStorage.addWithKey(res);
			}
			List<WBWebPageModule> modules = adminStorage.getAllRecords(WBWebPageModule.class);
			for( WBWebPageModule module: modules)
			{
				WBResource res = new WBResource(module.getExternalKey(), module.getName(), WBResource.PAGE_MODULE_TYPE);
				adminStorage.addWithKey(res);
			}
					
			List<WBArticle> articles = adminStorage.getAllRecords(WBArticle.class);
			for( WBArticle article: articles)
			{
				WBResource res = new WBResource(article.getExternalKey(), article.getTitle(), WBResource.ARTICLE_TYPE);
				adminStorage.addWithKey(res);
			}

			List<WBFile> files = adminStorage.getAllRecords(WBFile.class);
			for( WBFile file: files)
			{
				WBResource res = new WBResource(file.getExternalKey(), file.getName(), WBResource.FILE_TYPE);
				adminStorage.addWithKey(res);
			}
			
			List<WBMessage> messages = adminStorage.getAllRecords(WBMessage.class);
			Set<String> setMessages = new HashSet<String>();
			for( WBMessage message: messages)
			{
				setMessages.add(message.getName());
			}
			for(String item: setMessages)
			{
				WBResource res = new WBResource(item, item, WBResource.MESSAGE_TYPE);
				adminStorage.addWithKey(res);
			}

			List<WBParameter> parameters = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, "");
			for(WBParameter param: parameters)
			{
				WBResource res = new WBResource(param.getExternalKey(), param.getName(), WBResource.GLOBAL_PARAMETER_TYPE);
				adminStorage.addWithKey(res);
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
}
