package com.webpagebytes.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cache.DefaultWBCacheFactory;
import com.webpagebytes.cache.WBCacheFactory;
import com.webpagebytes.cache.WBWebPagesCache;
import com.webpagebytes.cmsdata.WBParameter;
import com.webpagebytes.cmsdata.WBUri;
import com.webpagebytes.cmsdata.WBWebPage;
import com.webpagebytes.datautility.AdminDataStorage;
import com.webpagebytes.datautility.AdminDataStorageFactory;
import com.webpagebytes.datautility.AdminDataStorageListener;
import com.webpagebytes.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.datautility.AdminDataStorage.AdminSortOperator;
import com.webpagebytes.datautility.local.WBLocalAdminDataStorage;
import com.webpagebytes.exception.WBException;
import com.webpagebytes.exception.WBIOException;
import com.webpagebytes.utility.HttpServletToolbox;

public class WBPageController extends WBController implements AdminDataStorageListener<Object>{

	private static final Logger log = Logger.getLogger(WBLocalAdminDataStorage.class.getName());
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBPageValidator pageValidator;
	private WBWebPagesCache wbWebPageCache;
	
	public WBPageController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = AdminDataStorageFactory.getInstance();
		pageValidator = new WBPageValidator();
		WBCacheFactory wbCacheFactory = DefaultWBCacheFactory.getInstance();
		wbWebPageCache = wbCacheFactory.createWBWebPagesCacheInstance(); 
		
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o)
	{
		try
		{
			if (t instanceof WBWebPage)
			{
				log.log(Level.INFO, "WbWebPage datastore notification, going to refresh the cache");
				wbWebPageCache.Refresh();
			}
		} catch (WBIOException e)
		{
			// TBD
		}
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBWebPage webPage = (WBWebPage)jsonObjectConverter.objectFromJSONString(jsonRequest, WBWebPage.class);
			Map<String, String> errors = pageValidator.validateCreate(webPage);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			webPage.setHash( WBWebPage.crc32(webPage.getHtmlSource()));
			webPage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			webPage.setExternalKey(adminStorage.getUniqueId());
			WBWebPage newWebPage = adminStorage.add(webPage);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newWebPage));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);

		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
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
			List<WBWebPage> allRecords = null;
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allRecords = adminStorage.getAllRecords(WBWebPage.class, sortParamProp, AdminSortOperator.ASCENDING);					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					allRecords = adminStorage.getAllRecords(WBWebPage.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					allRecords = adminStorage.getAllRecords(WBWebPage.class);					
				}
			} else
			{
				allRecords = adminStorage.getAllRecords(WBWebPage.class);				
			}
					
			List<WBWebPage> result = filterPagination(request, allRecords, additionalInfo);
			
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
	
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBWebPage webPage = adminStorage.get(key, WBWebPage.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(webPage));			

			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				List<WBUri> uris = adminStorage.query(WBUri.class, "resourceExternalKey", AdminQueryOperator.EQUAL, webPage.getExternalKey());
				org.json.JSONArray arrayUris = jsonObjectConverter.JSONArrayFromListObjects(uris);
				org.json.JSONObject additionalData = new org.json.JSONObject();
				additionalData.put("uri_links", arrayUris);
				returnJson.put(ADDTIONAL_DATA, additionalData);			
			}

			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);

		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBWebPage tempPage = adminStorage.get(key, WBWebPage.class);
			adminStorage.delete(key, WBWebPage.class);
			
			// delete the owned parameters
			adminStorage.delete(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, tempPage.getExternalKey());

			WBWebPage page = new WBWebPage();
			page.setKey(key);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(page));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBWebPage webPage = (WBWebPage)jsonObjectConverter.objectFromJSONString(jsonRequest, WBWebPage.class);
			webPage.setKey(key);
			Map<String, String> errors = pageValidator.validateUpdate(webPage);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			CRC32 crc = new CRC32();
			crc.update(webPage.getHtmlSource().getBytes());
			webPage.setHash( crc.getValue() );

			webPage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBWebPage newWebPage = adminStorage.update(webPage);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newWebPage));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
		

	public void setPageValidator(WBPageValidator pageValidator) {
		this.pageValidator = pageValidator;
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
	public void setPageCache(WBWebPagesCache pageCache)
	{
		this.wbWebPageCache = pageCache;
	}
	
	
}
