package com.webpagebytes.cms.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cache.WBWebPageModulesCache;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.datautility.AdminDataStorageListener;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class WBPageModuleController extends WBController implements AdminDataStorageListener<Object>{
	private AdminDataStorage adminStorage;
	private WBPageModuleValidator validator;
	private WBWebPageModulesCache wbPageModuleCache;
	public WBPageModuleController()
	{
		adminStorage = AdminDataStorageFactory.getInstance();
		validator = new WBPageModuleValidator();
		
		WBCacheFactory wbCacheFactory = DefaultWBCacheFactory.getInstance();
		wbPageModuleCache = wbCacheFactory.createWBWebPageModulesCacheInstance(); 
		
		adminStorage.addStorageListener(this);

	}

	public void notify (Object t, AdminDataStorageOperation o)
	{
		try
		{
			if (t instanceof WBWebPageModule)
			{
				wbPageModuleCache.Refresh();
			}
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
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbmodule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}
	
	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);

			List<WBWebPageModule> modules = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					modules = adminStorage.getAllRecords(WBWebPageModule.class, sortParamProp, AdminSortOperator.ASCENDING);					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					modules = adminStorage.getAllRecords(WBWebPageModule.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					modules = adminStorage.getAllRecords(WBWebPageModule.class);					
				}
			} else
			{
				modules = adminStorage.getAllRecords(WBWebPageModule.class);				
			}

			List<WBWebPageModule> result = filterPagination(request, modules, additionalInfo);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));	
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
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
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBWebPageModule newParameter = adminStorage.update(wbmodule);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newParameter));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);	
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
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(returnJson));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
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
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbmodule.setExternalKey(adminStorage.getUniqueId());
			WBWebPageModule newModule = adminStorage.add(wbmodule);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newModule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
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
	
	public void setPageModuleCache(WBWebPageModulesCache cache)
	{
		this.wbPageModuleCache = cache;
	}
	
	
}
