package com.webbricks.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBMessagesCache;
import com.webbricks.cmsdata.WBMessage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorage.AdminSortOperator;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

public class WBMessageController extends WBController implements AdminDataStorageListener<WBMessage> {
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBMessageValidator validator;
	private WBMessagesCache wbMessageCache;

	public WBMessageController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = AdminDataStorageFactory.getInstance();
		validator = new WBMessageValidator();
		validator.setAdminStorage(adminStorage);
		WBCacheFactory wbCacheFactory = new DefaultWBCacheFactory();
		wbMessageCache = wbCacheFactory.createWBMessagesCacheInstance();
	}
	
	public void notify (WBMessage t, AdminDataStorageOperation o)
	{
		try
		{
			wbMessageCache.Refresh();
		} catch (WBIOException e)
		{
			// do nothing
		}
	}

	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBMessage record = (WBMessage)jsonObjectConverter.objectFromJSONString(jsonRequest, WBMessage.class);
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
			WBMessage newRecord = adminStorage.add(record);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newRecord));			
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
			List<WBMessage> allRecords = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (request.getParameter("lcid") != null)
					{
						allRecords = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, request.getParameter("lcid"), sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						allRecords = adminStorage.getAllRecords(WBMessage.class, sortParamProp, AdminSortOperator.ASCENDING);
					}
				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (request.getParameter("lcid") != null)
					{
						allRecords = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, request.getParameter("lcid"), sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						allRecords = adminStorage.getAllRecords(WBMessage.class, sortParamProp, AdminSortOperator.DESCENDING);
					}
				} else
				{
					allRecords = adminStorage.getAllRecords(WBMessage.class);
				}
			} else
			{
				allRecords = adminStorage.getAllRecords(WBMessage.class);
			}
			
			List<WBMessage> result = filterPagination(request, allRecords, additionalInfo);
			
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

	private JSONObject jsonFromMessage(WBMessage message) throws JSONException
	{
		JSONObject json = new JSONObject();
		json.put("name", message.getName());
		json.put("value", message.getValue());
		json.put("isTranslated", message.getIsTranslated());
		json.put("key", message.getKey());
		json.put("externalKey", message.getExternalKey());
		json.put("lcid", message.getLcid());
		json.put("lastModified", message.getLastModified().getTime());
		return json;
	}
	
	public void getByCompare(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		Map<String, String> errors = new HashMap<String, String>();		
		if (request.getParameter("lcid") == null || request.getParameter("dlcid") == null)
		{
			errors.put("", WBErrors.WB_BAD_QUERY_PARAM);
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
			
			List<WBMessage> defaultRecords = null;
			List<WBMessage> records = null;

			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					defaultRecords = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid, sortParamProp, AdminSortOperator.ASCENDING);
					records = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid, sortParamProp, AdminSortOperator.ASCENDING);

				} else if (sortParamDir.equalsIgnoreCase(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					defaultRecords = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid, sortParamProp, AdminSortOperator.DESCENDING);
					records = adminStorage.queryWithSort(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					defaultRecords = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid);
					records = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
				}
			} else
			{
				defaultRecords = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid);
				records = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
			}

			Map<String, WBMessage> defaultRecordsMap = new HashMap<String, WBMessage>();
			Set<String> bkDefaultNames = new HashSet<String>();
			
			for(WBMessage message: defaultRecords)
			{
				defaultRecordsMap.put(message.getName(), message);
			}
			bkDefaultNames.addAll(defaultRecordsMap.keySet());
			
			JSONArray jsonArray = new JSONArray();
			for(WBMessage message: records)
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
				for(WBMessage message: defaultRecords)
				{
					if (bkDefaultNames.contains(message.getName())) {
						JSONObject json = jsonFromMessage(message);
						json.put("diff", "default");
						jsonArray.put(json);					
					}
				}
			}
			jsonArray = filterPagination(request, jsonArray, additionalInfo);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonArray);
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBMessage record = adminStorage.get(key, WBMessage.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(record));			
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
			adminStorage.delete(key, WBMessage.class);
			
			WBMessage record = new WBMessage();
			record.setKey(key);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(record));			
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
			WBMessage record = (WBMessage)jsonObjectConverter.objectFromJSONString(jsonRequest, WBMessage.class);
			record.setKey(key);
			Map<String, String> errors = validator.validateUpdate(record);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			WBMessage existingMessage = adminStorage.get(key, WBMessage.class);
			existingMessage.setValue(record.getValue());
			existingMessage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBMessage newRecord = adminStorage.update(existingMessage);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newRecord));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}


}
