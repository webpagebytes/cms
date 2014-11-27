package com.webpagebytes.cms.controllers;

import javax.servlet.http.HttpServletRequest;




import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBUrisCache;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBResource;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBWebPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminSortOperator;
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
		wbUriCache = cacheFactory.createWBUrisCacheInstance();	
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WBUri.class))
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
			
			WBResource resource = new WBResource(newUri.getExternalKey(), newUri.getUri(), WBResource.URI_TYPE);
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
			log.log(Level.SEVERE, e.getMessage(), e);
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	private org.json.JSONObject getWBUri(HttpServletRequest request, HttpServletResponse response, WBUri wburi) throws WPBException
	{
		try
		{
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
			List<WBUri> wburis = adminStorage.query(WBUri.class, "externalKey", AdminQueryOperator.EQUAL, extKey);			
			WBUri wburi = (wburis.size()>0)? wburis.get(0): null;
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
			WBUri tempUri = adminStorage.get(key, WBUri.class);
			
			adminStorage.delete(key, WBUri.class);
			
			// delete the owned parameters
			adminStorage.delete(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, tempUri.getExternalKey());
			
			try
			{
				adminStorage.delete(tempUri.getUri(), WBResource.class);
			} catch (Exception e)
			{
				// do not propagate further
			}

			WBUri wburi = new WBUri();
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
			WBUri wbUri = (WBUri)jsonObjectConverter.objectFromJSONString(jsonRequest, WBUri.class);
			wbUri.setPrivkey(key);
			Map<String, String> errors = uriValidator.validateUpdate(wbUri);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			wbUri.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBUri newUri = adminStorage.update(wbUri);
			
			WBResource resource = new WBResource(newUri.getUri(), newUri.getUri(), WBResource.URI_TYPE);
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
