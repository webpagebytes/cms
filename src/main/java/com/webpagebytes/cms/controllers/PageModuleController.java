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

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class PageModuleController extends Controller implements WPBAdminDataStorageListener<Object>{
	private WPBAdminDataStorage adminStorage;
	private PageModuleValidator validator;
	private WPBPageModulesCache wbPageModuleCache;
	public PageModuleController()
	{
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		validator = new PageModuleValidator();
		
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		wbPageModuleCache = wbCacheFactory.getPageModulesCacheInstance(); 
		
		adminStorage.addStorageListener(this);

	}

	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WPBWebPageModule.class))
			{
				wbPageModuleCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// TBD
		}
	}

	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBWebPageModule wbmodule = adminStorage.get(key, WPBWebPageModule.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbmodule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}

	public void getExt(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String extKey = (String)request.getAttribute("key");
			List<WPBWebPageModule> wbModules = adminStorage.query(WPBWebPageModule.class, "externalKey", AdminQueryOperator.EQUAL, extKey);
			WPBWebPageModule wbmodule = (wbModules.size()>0) ? wbModules.get(0) : null;
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbmodule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);

			List<WPBWebPageModule> modules = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					modules = adminStorage.getAllRecords(WPBWebPageModule.class, sortParamProp, AdminSortOperator.ASCENDING);					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					modules = adminStorage.getAllRecords(WPBWebPageModule.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					modules = adminStorage.getAllRecords(WPBWebPageModule.class);					
				}
			} else
			{
				modules = adminStorage.getAllRecords(WPBWebPageModule.class);				
			}

			List<WPBWebPageModule> result = filterPagination(request, modules, additionalInfo);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));	
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBWebPageModule wbmodule = (WPBWebPageModule)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBWebPageModule.class);
			wbmodule.setPrivkey(key);
			Map<String, String> errors = validator.validateUpdate(wbmodule);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WPBWebPageModule newModule = adminStorage.update(wbmodule);
			
			WPBResource resource = new WPBResource(newModule.getExternalKey(), newModule.getName(), WPBResource.PAGE_MODULE_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newModule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);	
		}				
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBWebPageModule pageModule = adminStorage.get(key, WPBWebPageModule.class);
			
			adminStorage.delete(key, WPBWebPageModule.class);
			try
			{
				if (pageModule != null)
				{
					adminStorage.delete(pageModule.getExternalKey(), WPBResource.class);
				}
			} catch (Exception e)
			{
				// do not propagate further
			}
			
			WPBWebPageModule param = new WPBWebPageModule();
			param.setPrivkey(key);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(returnJson));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBWebPageModule wbmodule = (WPBWebPageModule)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBWebPageModule.class);
			Map<String, String> errors = validator.validateCreate(wbmodule);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbmodule.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbmodule.setExternalKey(adminStorage.getUniqueId());
			WPBWebPageModule newModule = adminStorage.add(wbmodule);
			
			WPBResource resource = new WPBResource(newModule.getExternalKey(), newModule.getName(), WPBResource.PAGE_MODULE_TYPE);
			try
			{
				adminStorage.addWithKey(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newModule));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}
	}

	public void setHttpServletToolbox(HttpServletToolbox httpServletToolbox) {
		this.httpServletToolbox = httpServletToolbox;
	}

	public void setJsonObjectConverter(
			JSONToFromObjectConverter jsonObjectConverter) {
		this.jsonObjectConverter = jsonObjectConverter;
	}

	public void setAdminStorage(WPBAdminDataStorage adminStorage) {
		this.adminStorage = adminStorage;
	}

	public void setValidator(PageModuleValidator validator) {
		this.validator = validator;
	}
	
	public void setPageModuleCache(WPBPageModulesCache cache)
	{
		this.wbPageModuleCache = cache;
	}
	
	
}
