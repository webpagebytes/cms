package com.webbricks.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBWebPageModuleCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

public class WBPageModuleController extends WBController implements AdminDataStorageListener<WBWebPageModule>{
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBPageModuleValidator validator;
	private WBWebPageModuleCache wbPageModuleCache;
	public WBPageModuleController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
		validator = new WBPageModuleValidator();
		
		WBCacheFactory wbCacheFactory = new DefaultWBCacheFactory();
		wbPageModuleCache = wbCacheFactory.createWBWebPageModuleCacheInstance(); 
		
		adminStorage.addStorageListener(this);

	}

	public void notify (WBWebPageModule t, AdminDataStorageOperation o)
	{
		try
		{
			wbPageModuleCache.Refresh();
		} catch (WBIOException e)
		{
			// TBD
		}
	}

	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBWebPageModule wbmodule = adminStorage.get(key, WBWebPageModule.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(wbmodule, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}
	
	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBWebPageModule> modules = null;
			modules = adminStorage.getAllRecords(WBWebPageModule.class);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(modules);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBWebPageModule wbmodule = (WBWebPageModule)jsonObjectConverter.objectFromJSONString(jsonRequest, WBWebPageModule.class);
			wbmodule.setKey(key);
			Map<String, String> errors = validator.validateUpdate(wbmodule);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBWebPageModule newParameter = adminStorage.update(wbmodule);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newParameter, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}				
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			adminStorage.delete(key, WBWebPageModule.class);
			
			WBWebPageModule param = new WBWebPageModule();
			param.setKey(key);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(param, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBWebPageModule wbmodule = (WBWebPageModule)jsonObjectConverter.objectFromJSONString(jsonRequest, WBWebPageModule.class);
			Map<String, String> errors = validator.validateCreate(wbmodule);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbmodule.setExternalKey(adminStorage.getUniqueId());
			WBWebPageModule newModule = adminStorage.add(wbmodule);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newModule, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}
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

	public void setValidator(WBPageModuleValidator validator) {
		this.validator = validator;
	}
	
	public void setPageModuleCache(WBWebPageModuleCache cache)
	{
		this.wbPageModuleCache = cache;
	}
	
	
}
