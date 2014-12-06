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

import javax.servlet.http.HttpServletRequest;




import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBCacheFactory;
import com.webpagebytes.cms.appinterfaces.WPBUrisCache;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UriController extends Controller implements WPBAdminDataStorageListener<Object> {
	private WPBAdminDataStorage adminStorage;
	private UriValidator uriValidator;
	private WPBUrisCache wbUriCache;
	private static final Logger log = Logger.getLogger(UriController.class.getName());
	
	public UriController() {
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		uriValidator = new UriValidator();
		WPBCacheFactory cacheFactory = DefaultWPBCacheFactory.getInstance();
		wbUriCache = cacheFactory.getUrisCacheInstance();	
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WPBUri.class))
			{
				wbUriCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// TBD
		}
	}

	public void setUriValidator(UriValidator uriValidator) {
		this.uriValidator = uriValidator;
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
	
	public void setWbUriCache(WPBUrisCache wbUriCache) {
		this.wbUriCache = wbUriCache;
	}

	public void createWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBUri wbUri = (WPBUri)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBUri.class);
			Map<String, String> errors = uriValidator.validateCreate(wbUri);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbUri.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbUri.setExternalKey(adminStorage.getUniqueId());
			WPBUri newUri = adminStorage.add(wbUri);
			
			WPBResource resource = new WPBResource(newUri.getExternalKey(), newUri.getUri(), WPBResource.URI_TYPE);
			try
			{
				adminStorage.addWithKey(resource);
			} catch (Exception e)
			{
				//just log error and do not consider the operation as failure
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newUri));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
		} catch (Exception e)
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	public void getAllWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);
			List<WPBUri> allUri = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allUri = adminStorage.getAllRecords(WPBUri.class, sortParamProp, AdminSortOperator.ASCENDING);
				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allUri = adminStorage.getAllRecords(WPBUri.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					allUri = adminStorage.getAllRecords(WPBUri.class);
				}
			} else
			{
				allUri = adminStorage.getAllRecords(WPBUri.class);
			}
			
			List<WPBUri> result = filterPagination(request, allUri, additionalInfo);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	public void getWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBUri wburi = adminStorage.get(key, WPBUri.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wburi));
			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_FILE)
				{
					List<WPBWebPage> pages = adminStorage.query(WPBWebPage.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayPages = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("pages_links", arrayPages);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT)
				{
					List<WPBFile> pages = adminStorage.query(WPBFile.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayFiles = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("files_links", arrayFiles);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_URL_CONTROLLER)
				{
					org.json.JSONObject additionalData = new org.json.JSONObject();
					returnJson.put(ADDTIONAL_DATA, additionalData);
				}
			}

			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	private org.json.JSONObject getWBUri(HttpServletRequest request, HttpServletResponse response, WPBUri wburi) throws WPBException
	{
		try
		{
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wburi));
			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_FILE)
				{
					List<WPBWebPage> pages = adminStorage.query(WPBWebPage.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayPages = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("pages_links", arrayPages);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT)
				{
					List<WPBFile> pages = adminStorage.query(WPBFile.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayFiles = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("files_links", arrayFiles);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WPBUri.RESOURCE_TYPE_URL_CONTROLLER)
				{
					org.json.JSONObject additionalData = new org.json.JSONObject();
					returnJson.put(ADDTIONAL_DATA, additionalData);
				}
			}
			return returnJson;
			
		} catch (Exception e)		
		{
			throw new WPBException("Cannot fetch additional data for uri "  ,e);
		}		
	}
	public void getWBUriExt(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String extKey = (String)request.getAttribute("key");
			List<WPBUri> wburis = adminStorage.query(WPBUri.class, "externalKey", AdminQueryOperator.EQUAL, extKey);			
			WPBUri wburi = (wburis.size()>0)? wburis.get(0): null;
			org.json.JSONObject returnJson = getWBUri(request, response, wburi);
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
		} catch (Exception e)		
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void deleteWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBUri tempUri = adminStorage.get(key, WPBUri.class);
			
			adminStorage.delete(key, WPBUri.class);
			
			// delete the owned parameters
			adminStorage.delete(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, tempUri.getExternalKey());
			
			try
			{
				adminStorage.delete(tempUri.getUri(), WPBResource.class);
			} catch (Exception e)
			{
				// do not propagate further
			}

			WPBUri wburi = new WPBUri();
			wburi.setPrivkey(key);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wburi));						
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void updateWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBUri wbUri = (WPBUri)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBUri.class);
			wbUri.setPrivkey(key);
			Map<String, String> errors = uriValidator.validateUpdate(wbUri);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbUri.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WPBUri newUri = adminStorage.update(wbUri);
			
			WPBResource resource = new WPBResource(newUri.getUri(), newUri.getUri(), WPBResource.URI_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newUri));						
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	
}
