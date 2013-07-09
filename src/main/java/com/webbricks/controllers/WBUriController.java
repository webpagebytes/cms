package com.webbricks.controllers;

import javax.servlet.http.HttpServletRequest;


import javax.servlet.http.HttpServletResponse;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBUrisCache;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.TimeZone;
public class WBUriController extends WBController implements AdminDataStorageListener<WBUri> {
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBUriValidator uriValidator;
	private WBUrisCache wbUriCache;
	
	public WBUriController() {
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
		uriValidator = new WBUriValidator();
		WBCacheFactory cacheFactory = new DefaultWBCacheFactory();
		wbUriCache = cacheFactory.createWBUrisCacheInstance();	
		adminStorage.addStorageListener(this);
	}
	
	public void notify (WBUri t, AdminDataStorageOperation o)
	{
		try
		{
			wbUriCache.Refresh();
		} catch (WBIOException e)
		{
			// TBD
		}
	}

	public void setUriValidator(WBUriValidator uriValidator) {
		this.uriValidator = uriValidator;
	}

	public void setHttpServletToolbox(HttpServletToolbox httpServletToolbox) {
		this.httpServletToolbox = httpServletToolbox;
	}

	public void setJsonObjectConverter(
			WBJSONToFromObjectConverter jsonObjectConverter) {
		this.jsonObjectConverter = jsonObjectConverter;
	}

	public void setAdminStorage(AdminDataStorage adminStorage) {
		this.adminStorage = adminStorage;
	}
	
	public void setWbUriCache(WBUrisCache wbUriCache) {
		this.wbUriCache = wbUriCache;
	}

	public void createWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBUri wbUri = (WBUri)jsonObjectConverter.objectFromJSONString(jsonRequest, WBUri.class);
			Map<String, String> errors = uriValidator.validateCreate(wbUri);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbUri.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbUri.setExternalKey(adminStorage.getUniqueId());
			WBUri newUri = adminStorage.add(wbUri);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newUri, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}
	}
	public void getAllWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBUri> allUri = adminStorage.getAllRecords(WBUri.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(allUri);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}
	}
	public void getWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBUri wburi = adminStorage.get(key, WBUri.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(wburi, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}
	public void deleteWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			adminStorage.delete(key, WBUri.class);
			
			WBUri uri = new WBUri();
			uri.setKey(key);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(uri, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}		
	}

	public void updateWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBUri wbUri = (WBUri)jsonObjectConverter.objectFromJSONString(jsonRequest, WBUri.class);
			wbUri.setKey(key);
			Map<String, String> errors = uriValidator.validateUpdate(wbUri);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbUri.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBUri newUri = adminStorage.update(wbUri);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newUri, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}		
	}

	
}
