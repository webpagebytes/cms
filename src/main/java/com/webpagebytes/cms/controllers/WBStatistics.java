package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.cmsdata.WBParameter;
import com.webpagebytes.cms.cmsdata.WBWebPageModule;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WBException;



public class WBStatistics extends WBController {

	private enum WBEntities
	{
		URIS,
		PAGES,
		MODULES,
		MESSAGES,
		ARTICLES,
		FILES,
		LANGUAGES,
		GLOBALPARAMS		
	};
	private static final String PARAM_ENTITY = "entity"; 
	private static final String PARAM_HISTORY_COUNT = "count"; 
	
	private static final String SORT_PARAM = "lastModified";
	private static final String ERROR_FIELD = "error";
	
	private static final int HISTORY_COUNT = 3;
	private AdminDataStorage adminStorage;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	
	public WBStatistics()
	{
		adminStorage = AdminDataStorageFactory.getInstance();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
	}
	
	private void getRecordsStats(HttpServletRequest request, Class entityClass, org.json.JSONObject payloadJson, String entityName) throws Exception
	{
		org.json.JSONObject returnEntity = new org.json.JSONObject();					
		try
		{
			List<Object> records = adminStorage.getAllRecords(entityClass, SORT_PARAM, AdminSortOperator.DESCENDING);
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();
			List<Object> filteredRecords = filterPagination(request, records, additionalInfo);
			returnEntity.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(filteredRecords));
			returnEntity.put(ADDTIONAL_DATA, additionalInfo);
		} catch (Exception e)
		{
			returnEntity.put(ERROR_FIELD, WBErrors.WB_CANT_GET_RECORDS);
		}
		payloadJson.put(entityName, returnEntity);		
	}

	
	public void getStatistics(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		String [] entities = request.getParameterValues(PARAM_ENTITY);
		org.json.JSONObject payloadJson = new org.json.JSONObject();
		
		try
		{
			for (String entity: entities)
			{
				entity = entity.toUpperCase();
				WBEntities paramEntity = WBEntities.valueOf(entity.toUpperCase());
				switch (paramEntity)
				{
					case URIS:
						getRecordsStats(request, WBUri.class, payloadJson, entity);
						break;
					case PAGES:
						getRecordsStats(request, WBUri.class, payloadJson, entity);
						break;
					case MODULES:
						getRecordsStats(request, WBWebPageModule.class, payloadJson, entity);
						break;
					case ARTICLES:
						getRecordsStats(request, WBArticle.class, payloadJson, entity);
						break;
					case MESSAGES:
						getRecordsStats(request, WBMessage.class, payloadJson, entity);
						break;
					case FILES:
						getRecordsStats(request, WBFile.class, payloadJson, entity);
						break;
					case LANGUAGES:
						break;
					case GLOBALPARAMS:
						break;
	
				}
			}
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, payloadJson);	
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			

		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);				
		}
	}
}
