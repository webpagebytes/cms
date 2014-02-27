package com.webbricks.controllers;

import javax.servlet.http.HttpServletRequest;



import javax.servlet.http.HttpServletResponse;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBUrisCache;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorage.AdminSortOperator;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

import java.util.Calendar;
import java.util.Map;
import java.util.HashMap;
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
		adminStorage = AdminDataStorageFactory.getInstance();
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
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newUri));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	public void getAllWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);
			List<WBUri> allUri = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allUri = adminStorage.getAllRecords(WBUri.class, sortParamProp, AdminSortOperator.ASCENDING);
				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allUri = adminStorage.getAllRecords(WBUri.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					allUri = adminStorage.getAllRecords(WBUri.class);
				}
			} else
			{
				allUri = adminStorage.getAllRecords(WBUri.class);
			}
			
			List<WBUri> result = filterPagination(request, allUri, additionalInfo);
			
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
	public void getWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBUri wburi = adminStorage.get(key, WBUri.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wburi));
			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				if (wburi.getResourceType() == WBUri.RESOURCE_TYPE_FILE)
				{
					List<WBWebPage> pages = adminStorage.query(WBWebPage.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayPages = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("pages_links", arrayPages);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WBUri.RESOURCE_TYPE_TEXT)
				{
					List<WBFile> pages = adminStorage.query(WBFile.class, "externalKey", AdminQueryOperator.EQUAL, wburi.getResourceExternalKey());
					org.json.JSONArray arrayFiles = jsonObjectConverter.JSONArrayFromListObjects(pages);
					org.json.JSONObject additionalData = new org.json.JSONObject();
					additionalData.put("files_links", arrayFiles);
					returnJson.put(ADDTIONAL_DATA, additionalData);
				} else if (wburi.getResourceType() == WBUri.RESOURCE_TYPE_URL_CONTROLLER)
				{
					org.json.JSONObject additionalData = new org.json.JSONObject();
					returnJson.put(ADDTIONAL_DATA, additionalData);
				}
			}

			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	public void deleteWBUri(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBUri tempUri = adminStorage.get(key, WBUri.class);
			
			adminStorage.delete(key, WBUri.class);
			
			// delete the owned parameters
			adminStorage.delete(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, tempUri.getExternalKey());
			
			WBUri wburi = new WBUri();
			wburi.setKey(key);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wburi));						
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
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
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newUri));						
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	
}
