package com.webpagebytes.cms.controllers;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cache.WPBParametersCache;
import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBResource;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.datautility.AdminDataStorageListener;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class WBParameterController extends WBController implements AdminDataStorageListener<Object> {
	private AdminDataStorage adminStorage;
	private WBParameterValidator parameterValidator;
	private WPBParametersCache wbParameterCache;
	public WBParameterController() {
		adminStorage = AdminDataStorageFactory.getInstance();
		parameterValidator = new WBParameterValidator();
		
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		wbParameterCache = wbCacheFactory.createWBParametersCacheInstance(); 
		
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WBParameter.class))
			{
				wbParameterCache.Refresh();
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
			WBParameter wbparameter = adminStorage.get(key, WBParameter.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbparameter));			
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

			List<WBParameter> parameters = null;
			String keyOwner = request.getParameter("ownerExternalKey");
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);

					if (keyOwner != null)
					{
						parameters = adminStorage.queryWithSort(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner, sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						parameters = adminStorage.getAllRecords(WBParameter.class, sortParamProp, AdminSortOperator.ASCENDING);
					}
					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);

					if (keyOwner != null)
					{
						parameters = adminStorage.queryWithSort(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner, sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						parameters = adminStorage.getAllRecords(WBParameter.class, sortParamProp, AdminSortOperator.DESCENDING);
					}
					
				} else
				{
					if (keyOwner != null)
					{
						parameters = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner);
					} else
					{
						parameters = adminStorage.getAllRecords(WBParameter.class);
					}					
				}
			} else
			{
				if (keyOwner != null)
				{
					parameters = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner);
				} else
				{
					parameters = adminStorage.getAllRecords(WBParameter.class);
				}				
			}
			List<WBParameter> filteredParams = filterPagination(request, parameters, additionalInfo);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(filteredParams));	
			returnJson.put(ADDTIONAL_DATA, additionalInfo);
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
			WBParameter wbParameter = (WBParameter)jsonObjectConverter.objectFromJSONString(jsonRequest, WBParameter.class);
			wbParameter.setPrivkey(key);
			Map<String, String> errors = parameterValidator.validateUpdate(wbParameter);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbParameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBParameter newParameter = adminStorage.update(wbParameter);
			
			if (newParameter.getOwnerExternalKey() == null || newParameter.getOwnerExternalKey().length()==0)
			{
				WBResource resource = new WBResource(newParameter.getExternalKey(), newParameter.getName(), WBResource.ARTICLE_TYPE);
				try
				{
					adminStorage.update(resource);
				} catch (Exception e)
				{
					// do nothing
				}
			}
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
			WBParameter param = adminStorage.get(key, WBParameter.class);
			
			adminStorage.delete(key, WBParameter.class);
			if (param != null)
			{
				try
				{
					if (param != null)
					{
						adminStorage.delete(param.getExternalKey(), WBResource.class);
					}
				} catch (Exception e)
				{
					// do nothing
				}
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(param));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
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
			
			if (newParameter.getOwnerExternalKey() == null || newParameter.getOwnerExternalKey().length()==0)
			{
				WBResource resource = new WBResource(newParameter.getExternalKey(), newParameter.getName(), WBResource.ARTICLE_TYPE);
				try
				{
					adminStorage.addWithKey(resource);
				} catch (Exception e)
				{
					// do nothing
				}
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newParameter));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	
	public void createFromOwner(String fromOwnerExternalKey, String ownerExternalKey, HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
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
			List<WBParameter> ownerParams = adminStorage.query(WBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, fromOwnerExternalKey);			
			List<WBParameter> newParams = new ArrayList<WBParameter>();
			for(WBParameter parameter: ownerParams)
			{
				parameter.setOwnerExternalKey(ownerExternalKey);
				parameter.setPrivkey(null);
				parameter.setExternalKey(adminStorage.getUniqueId());
				parameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
				
				WBParameter newParam = adminStorage.add(parameter);
				newParams.add(newParam);
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(newParams));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
		
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		String ownerExternalKeyStr = request.getParameter("ownerExternalKey");
		String fromOwnerExternalKeyStr = request.getParameter("fromOwnerExternalKey");
		if (ownerExternalKeyStr != null || fromOwnerExternalKeyStr != null)
		{
			createFromOwner(fromOwnerExternalKeyStr, ownerExternalKeyStr, request, response, requestUri);
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
	public void setParameterCache(WPBParametersCache parameterCache)
	{
		this.wbParameterCache = parameterCache;
	}

}
