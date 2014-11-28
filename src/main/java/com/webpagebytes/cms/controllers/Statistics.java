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

import java.util.HashMap;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.cmsdata.WPBWebPage;
import com.webpagebytes.cms.cmsdata.WPBWebPageModule;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.JSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WPBException;



public class Statistics extends Controller {

	private enum WBEntities
	{
		URIS,
		PAGES,
		MODULES,
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
	private WPBAdminDataStorage adminStorage;
	private JSONToFromObjectConverter jsonObjectConverter;
	
	public Statistics()
	{
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		jsonObjectConverter = new JSONToFromObjectConverter();
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
			returnEntity.put(ERROR_FIELD, WPBErrors.WB_CANT_GET_RECORDS);
		}
		payloadJson.put(entityName, returnEntity);		
	}

	private void getLanguagesStats(HttpServletRequest request, org.json.JSONObject payloadJson, String entityName) throws Exception
	{
		org.json.JSONObject returnEntity = new org.json.JSONObject();
		org.json.JSONObject languagesJson = new org.json.JSONObject();					
		try
		{
			WPBProject project = adminStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
			Set<String> languages = project.getSupportedLanguagesSet();
			languagesJson.put("languages", languages);
			languagesJson.put("defaultLanguage", project.getDefaultLanguage());
			returnEntity.put(DATA, languagesJson);
		} catch (Exception e)
		{
			returnEntity.put(ERROR_FIELD, WPBErrors.WB_CANT_GET_RECORDS);
		}
		payloadJson.put(entityName, returnEntity);		
	}

	
	public void getStatistics(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		String [] entities = request.getParameterValues(PARAM_ENTITY);
		org.json.JSONObject payloadJson = new org.json.JSONObject();
		
		try
		{
			if (entities != null)
			{
				for (String entity: entities)
				{
					entity = entity.toUpperCase();
					WBEntities paramEntity = WBEntities.valueOf(entity.toUpperCase());
					switch (paramEntity)
					{
						case URIS:
							getRecordsStats(request, WPBUri.class, payloadJson, entity);
							break;
						case PAGES:
							getRecordsStats(request, WPBWebPage.class, payloadJson, entity);
							break;
						case MODULES:
							getRecordsStats(request, WPBWebPageModule.class, payloadJson, entity);
							break;
						case ARTICLES:
							getRecordsStats(request, WPBArticle.class, payloadJson, entity);
							break;
						case FILES:
							getRecordsStats(request, WPBFile.class, payloadJson, entity);
							break;
						case LANGUAGES:
							getLanguagesStats(request, payloadJson, entity);
							break;
						case GLOBALPARAMS:
							break;
		
					}
				}
			}
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, payloadJson);	
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			

		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);				
		}
	}
}
