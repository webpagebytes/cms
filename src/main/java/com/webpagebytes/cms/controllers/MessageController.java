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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webpagebytes.cms.WPBAuthenticationResult;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.engine.WPBAdminDataStorageListener;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

public class MessageController extends Controller implements WPBAdminDataStorageListener {
	private MessageValidator validator;
	private WPBMessagesCache wbMessageCache;

	public MessageController()
	{
		validator = new MessageValidator();
		validator.setAdminStorage(adminStorage);
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		wbMessageCache = wbCacheFactory.getMessagesCacheInstance();
		adminStorage.addStorageListener(this);
	}
	
	public<T> void notify (T t, AdminDataStorageOperation o, Class<? extends Object> type)
	{
		try
		{
			if (type.equals(WPBMessage.class))
			{
				wbMessageCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// do nothing
		}
	}

	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBMessage record = (WPBMessage)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBMessage.class);
			Map<String, String> errors = validator.validateCreate(record);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);
				return;
			}
			record.setName(record.getName().trim());
			record.setLcid(record.getLcid().trim());
			record.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			record.setExternalKey(adminStorage.getUniqueId());
			record.setVersion(UUID.randomUUID().toString());
			WPBMessage newRecord = adminStorage.addWithKey(record);
			WPBResource resource = new WPBResource(newRecord.getName(), newRecord.getName(), WPBResource.MESSAGE_TYPE);
			try
			{
				adminStorage.addWithKey(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newRecord));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);
			List<WPBMessage> allRecords = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (request.getParameter("lcid") != null)
					{
						allRecords = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, request.getParameter("lcid"), sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						allRecords = adminStorage.getAllRecords(WPBMessage.class, sortParamProp, AdminSortOperator.ASCENDING);
					}
				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (request.getParameter("lcid") != null)
					{
						allRecords = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, request.getParameter("lcid"), sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						allRecords = adminStorage.getAllRecords(WPBMessage.class, sortParamProp, AdminSortOperator.DESCENDING);
					}
				} else
				{
					allRecords = adminStorage.getAllRecords(WPBMessage.class);
				}
			} else
			{
				allRecords = adminStorage.getAllRecords(WPBMessage.class);
			}
			
			List<WPBMessage> result = filterPagination(request, allRecords, additionalInfo);
			
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);

		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	private JSONObject jsonFromMessage(WPBMessage message) throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("name", message.getName());
		json.put("value", message.getValue());
		json.put("isTranslated", message.getIsTranslated());
		json.put("externalKey", message.getExternalKey());
		json.put("lcid", message.getLcid());
		json.put("lastModified", message.getLastModified().getTime());
		return json;
	}
	
	public void getByCompare(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		Map<String, String> errors = new HashMap<String, String>();		
		if (request.getParameter("lcid") == null || request.getParameter("dlcid") == null)
		{
			errors.put("", WPBErrors.WB_BAD_QUERY_PARAM);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
			return;
		}
		
		String lcid = request.getParameter("lcid");
		String dlcid = request.getParameter("dlcid");

		Map<String, Object> additionalInfo = new HashMap<String, Object> ();					
		String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
		String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);
		
		try
		{
			
			List<WPBMessage> defaultRecords = null;
			List<WPBMessage> records = null;

			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					defaultRecords = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid, sortParamProp, AdminSortOperator.ASCENDING);
					records = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid, sortParamProp, AdminSortOperator.ASCENDING);

				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					defaultRecords = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid, sortParamProp, AdminSortOperator.DESCENDING);
					records = adminStorage.queryWithSort(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					defaultRecords = adminStorage.query(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid);
					records = adminStorage.query(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
				}
			} else
			{
				defaultRecords = adminStorage.query(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid);
				records = adminStorage.query(WPBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
			}

			Map<String, WPBMessage> defaultRecordsMap = new HashMap<String, WPBMessage>();
			Set<String> bkDefaultNames = new HashSet<String>();
			
			for(WPBMessage message: defaultRecords)
			{
				defaultRecordsMap.put(message.getName(), message);
			}
			bkDefaultNames.addAll(defaultRecordsMap.keySet());
			
			JSONArray jsonArray = new JSONArray();
			for(WPBMessage message: records)
			{
				String name = message.getName();
				String diff = "both";
				if (!defaultRecordsMap.containsKey(name))
				{
					diff = "current";
				}
				JSONObject json = jsonFromMessage(message);
				json.put("diff", diff);
				jsonArray.put(json);
				bkDefaultNames.remove(name);
			}
			if (bkDefaultNames.size()>0)
			{
				for(WPBMessage message: defaultRecords)
				{
					if (bkDefaultNames.contains(message.getName())) {
						JSONObject json = jsonFromMessage(message);
						json.put("diff", "default");
						jsonArray.put(json);					
					}
				}
			}
			jsonArray = filterPagination(request, jsonArray, additionalInfo);
			
			returnJson.put(DATA, jsonArray);
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			WPBMessage record = adminStorage.get(key, WPBMessage.class);
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(record));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			WPBMessage record = adminStorage.get(key, WPBMessage.class);
			
			adminStorage.delete(key, WPBMessage.class);
			try
			{
				if (record != null)
				{
					adminStorage.delete(record.getName(), WPBResource.class);
				}
			} catch (Exception e)
			{
				// do not propagate further
			}
			
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(record));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBMessage record = (WPBMessage)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBMessage.class);
			record.setExternalKey(key);
			Map<String, String> errors = validator.validateUpdate(record);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			WPBMessage existingMessage = adminStorage.get(key, WPBMessage.class);
			existingMessage.setValue(record.getValue());
			existingMessage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			existingMessage.setVersion(UUID.randomUUID().toString());
			WPBMessage newRecord = adminStorage.update(existingMessage);
			
			WPBResource resource = new WPBResource(newRecord.getName(), newRecord.getName(), WPBResource.MESSAGE_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newRecord));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}


}
