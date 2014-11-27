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
import com.webpagebytes.cms.cmsdata.WPBParameter;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class ParameterController extends Controller implements WPBAdminDataStorageListener<Object> {
	private WPBAdminDataStorage adminStorage;
	private ParameterValidator parameterValidator;
	private WPBParametersCache wbParameterCache;
	public ParameterController() {
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		parameterValidator = new ParameterValidator();
		
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		wbParameterCache = wbCacheFactory.createWBParametersCacheInstance(); 
		
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WPBParameter.class))
			{
				wbParameterCache.Refresh();
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
			WPBParameter wbparameter = adminStorage.get(key, WPBParameter.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbparameter));			
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

			List<WPBParameter> parameters = null;
			String keyOwner = request.getParameter("ownerExternalKey");
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);

					if (keyOwner != null)
					{
						parameters = adminStorage.queryWithSort(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner, sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						parameters = adminStorage.getAllRecords(WPBParameter.class, sortParamProp, AdminSortOperator.ASCENDING);
					}
					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);

					if (keyOwner != null)
					{
						parameters = adminStorage.queryWithSort(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner, sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						parameters = adminStorage.getAllRecords(WPBParameter.class, sortParamProp, AdminSortOperator.DESCENDING);
					}
					
				} else
				{
					if (keyOwner != null)
					{
						parameters = adminStorage.query(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner);
					} else
					{
						parameters = adminStorage.getAllRecords(WPBParameter.class);
					}					
				}
			} else
			{
				if (keyOwner != null)
				{
					parameters = adminStorage.query(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, keyOwner);
				} else
				{
					parameters = adminStorage.getAllRecords(WPBParameter.class);
				}				
			}
			List<WPBParameter> filteredParams = filterPagination(request, parameters, additionalInfo);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(filteredParams));	
			returnJson.put(ADDTIONAL_DATA, additionalInfo);
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
			WPBParameter wbParameter = (WPBParameter)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBParameter.class);
			wbParameter.setPrivkey(key);
			Map<String, String> errors = parameterValidator.validateUpdate(wbParameter);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbParameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WPBParameter newParameter = adminStorage.update(wbParameter);
			
			if (newParameter.getOwnerExternalKey() == null || newParameter.getOwnerExternalKey().length()==0)
			{
				WPBResource resource = new WPBResource(newParameter.getExternalKey(), newParameter.getName(), WPBResource.ARTICLE_TYPE);
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
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}				
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBParameter param = adminStorage.get(key, WPBParameter.class);
			
			adminStorage.delete(key, WPBParameter.class);
			if (param != null)
			{
				try
				{
					if (param != null)
					{
						adminStorage.delete(param.getExternalKey(), WPBResource.class);
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
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	
	public void createSingle(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBParameter wbParameter = (WPBParameter)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBParameter.class);
			Map<String, String> errors = parameterValidator.validateCreate(wbParameter);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			wbParameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			wbParameter.setExternalKey(adminStorage.getUniqueId());
			WPBParameter newParameter = adminStorage.add(wbParameter);
			
			if (newParameter.getOwnerExternalKey() == null || newParameter.getOwnerExternalKey().length()==0)
			{
				WPBResource resource = new WPBResource(newParameter.getExternalKey(), newParameter.getName(), WPBResource.ARTICLE_TYPE);
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
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	
	public void createFromOwner(String fromOwnerExternalKey, String ownerExternalKey, HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Map<String, String> errors = new HashMap<String, String>();		
			if (ownerExternalKey == null || ownerExternalKey.equals(0L))
			{
				errors.put("", WPBErrors.WBPARAMETER_NO_OWNER_KEY);
			}
			if (fromOwnerExternalKey == null || fromOwnerExternalKey.equals(0L))
			{
				errors.put("", WPBErrors.WBPARAMETER_NO_FROMOWNER_KEY);
			}
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);									
			}
			List<WPBParameter> ownerParams = adminStorage.query(WPBParameter.class, "ownerExternalKey", AdminQueryOperator.EQUAL, fromOwnerExternalKey);			
			List<WPBParameter> newParams = new ArrayList<WPBParameter>();
			for(WPBParameter parameter: ownerParams)
			{
				parameter.setOwnerExternalKey(ownerExternalKey);
				parameter.setPrivkey(null);
				parameter.setExternalKey(adminStorage.getUniqueId());
				parameter.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
				
				WPBParameter newParam = adminStorage.add(parameter);
				newParams.add(newParam);
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(newParams));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
		
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
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
			JSONToFromObjectConverter jsonObjectConverter) {
		this.jsonObjectConverter = jsonObjectConverter;
	}

	public void setAdminStorage(WPBAdminDataStorage adminStorage) {
		this.adminStorage = adminStorage;
	}

	public void setParameterValidator(ParameterValidator parameterValidator) {
		this.parameterValidator = parameterValidator;
	}
	public void setParameterCache(WPBParametersCache parameterCache)
	{
		this.wbParameterCache = parameterCache;
	}

}
