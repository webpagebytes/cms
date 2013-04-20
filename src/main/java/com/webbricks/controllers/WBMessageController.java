package com.webbricks.controllers;

import java.util.ArrayList;
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
import com.webbricks.cache.WBMessageCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBImage;
import com.webbricks.cmsdata.WBMessage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

public class WBMessageController extends WBController implements AdminDataStorageListener<WBMessage> {
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBMessageValidator validator;
	private WBMessageCache wbMessageCache;

	public WBMessageController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
		validator = new WBMessageValidator();
		validator.setAdminStorage(adminStorage);
		WBCacheFactory wbCacheFactory = new DefaultWBCacheFactory();
		wbMessageCache = wbCacheFactory.createWBMessageCacheInstance();
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
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newRecord, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBMessage> allRecords = null;
			if (request.getParameter("lcid") != null)
			{
				allRecords = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, request.getParameter("lcid"));
			} else
			{
				allRecords = adminStorage.getAllRecords(WBMessage.class);
			}
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(allRecords);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
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
			
		}
		String lcid = request.getParameter("lcid");
		String dlcid = request.getParameter("dlcid");
		try
		{
			List<WBMessage> defaultRecords = null;
			defaultRecords = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, dlcid);
			List<WBMessage> records = adminStorage.query(WBMessage.class, "lcid", AdminQueryOperator.EQUAL, lcid);
			
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
			
			for(String name: bkDefaultNames)
			{
				WBMessage message = defaultRecordsMap.get(name); 
				JSONObject json = jsonFromMessage(message);
				json.put("diff", "default");
				jsonArray.put(json);
			}
			
			String jsonReturn = jsonArray.toString();
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}
	}

	
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBMessage record = adminStorage.get(key, WBMessage.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(record, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
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
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(record, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
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
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newRecord, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}		
	}


}
