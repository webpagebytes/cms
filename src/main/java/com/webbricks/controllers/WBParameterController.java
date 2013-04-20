package com.webbricks.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBParameterCache;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBDefaultGaeDataFactory;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

public class WBParameterController extends WBController implements AdminDataStorageListener<WBParameter> {
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBParameterValidator parameterValidator;
	private WBParameterCache wbParameterCache;
	public WBParameterController() {
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
		parameterValidator = new WBParameterValidator();
		
		WBCacheFactory wbCacheFactory = new DefaultWBCacheFactory();
		wbParameterCache = wbCacheFactory.createWBParameterCacheInstance(); 
		
		adminStorage.addStorageListener(this);
	}
	
	public void notify (WBParameter t, AdminDataStorageOperation o)
	{
		try
		{
			wbParameterCache.Refresh();
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
			WBParameter wbparameter = adminStorage.get(key, WBParameter.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(wbparameter, null);
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
			List<WBParameter> parameters = null;
			String keyOwner = request.getParameter("ownerExternalKey");
			if (keyOwner != null)
			{
				parameters = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, Long.valueOf(keyOwner));
			} else
			{
				parameters = adminStorage.getAllRecords(WBParameter.class);
			}
			
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(parameters);
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
			WBParameter wbParameter = (WBParameter)jsonObjectConverter.objectFromJSONString(jsonRequest, WBParameter.class);
			wbParameter.setKey(key);
			Map<String, String> errors = parameterValidator.validateUpdate(wbParameter);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbParameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBParameter newParameter = adminStorage.update(wbParameter);
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
			adminStorage.delete(key, WBParameter.class);
			WBParameter param = new WBParameter();
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
	
	public void createSingle(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBParameter wbParameter = (WBParameter)jsonObjectConverter.objectFromJSONString(jsonRequest, WBParameter.class);
			Map<String, String> errors = parameterValidator.validateCreate(wbParameter);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbParameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbParameter.setExternalKey(adminStorage.getUniqueId());
			WBParameter newParameter = adminStorage.add(wbParameter);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newParameter, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}
	
	public void createFromOwner(Long fromOwnerExternalKey, Long ownerExternalKey, HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Map<String, String> errors = new HashMap<String, String>();		
			if (ownerExternalKey == null || ownerExternalKey.equals(0L))
			{
				errors.put("", WBErrors.WBPARAMETER_NO_OWNER_KEY);
			}
			if (fromOwnerExternalKey == null || fromOwnerExternalKey.equals(0L))
			{
				errors.put("", WBErrors.WBPARAMETER_NO_FROMOWNER_KEY);
			}
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);									
			}
			List<WBParameter> ownerParams = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, Long.valueOf(fromOwnerExternalKey));			
			List<WBParameter> newParams = new ArrayList<WBParameter>();
			for(WBParameter parameter: ownerParams)
			{
				parameter.setOwnerExternalKey(ownerExternalKey);
				parameter.setKey(null);
				parameter.setExternalKey(adminStorage.getUniqueId());
				parameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
				
				WBParameter newParam = adminStorage.add(parameter);
				newParams.add(newParam);
			}
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(newParams);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}
		
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		String ownerExternalKeyStr = request.getParameter("ownerExternalKey");
		String fromOwnerExternalKeyStr = request.getParameter("fromOwnerExternalKey");
		if (ownerExternalKeyStr != null || fromOwnerExternalKeyStr != null)
		{
			Long ownerExternalKey = null;
			Long fromOwnerExternalKey = null;
			try
			{
				ownerExternalKey = Long.valueOf(ownerExternalKeyStr);
				fromOwnerExternalKey = Long.valueOf(fromOwnerExternalKeyStr);
			} catch (Exception e)
			{
				Map<String, String> errors = new HashMap<String, String>();		
				errors.put("", WBErrors.WB_INVALID_KEY);
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);							
				return;
			}
			createFromOwner(fromOwnerExternalKey, ownerExternalKey, request, response, requestUri);
			return;
		}
		
		createSingle(request, response, requestUri);
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

	public void setParameterValidator(WBParameterValidator parameterValidator) {
		this.parameterValidator = parameterValidator;
	}
	public void setParameterCache(WBParameterCache parameterCache)
	{
		this.wbParameterCache = parameterCache;
	}

}
