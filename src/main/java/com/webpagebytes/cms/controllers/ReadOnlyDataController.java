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

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage;
import com.webpagebytes.cms.appinterfaces.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.exception.WPBException;

public class ReadOnlyDataController  extends Controller {

	public static final String DATA_FILES = "data_files";
	public static final String DATA_PAGES = "data_pages";
	
	private WPBAdminDataStorage adminStorage;


	public ReadOnlyDataController() 
	{
		adminStorage = WPBAdminDataStorageFactory.getInstance();
	}
	
	public void getShortDataOnFilesAndPages(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		List<WPBFile> files = adminStorage.getAllRecords(WPBFile.class);
		List<WPBPage> pages = adminStorage.getAllRecords(WPBPage.class);

		
		try 
		{
			org.json.JSONObject returnJson = new org.json.JSONObject();
			org.json.JSONArray jsonFilesArray = new org.json.JSONArray(); 
			for(WPBFile file : files)
			{
				JSONObject object = new JSONObject();
				object.put("externalKey", file.getExternalKey());
				object.put("name", file.getName());
				jsonFilesArray.put(object);
			}
			returnJson.put(DATA_FILES, jsonFilesArray);
			
			org.json.JSONArray jsonPagesArray = new org.json.JSONArray(); 
			for(WPBPage page : pages)
			{
				JSONObject object = new JSONObject();
				object.put("externalKey", page.getExternalKey());
				object.put("name", page.getName());
				jsonPagesArray.put(object);
			}
			returnJson.put(DATA_PAGES, jsonPagesArray);
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (JSONException e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}
		
	}
	
	public void search(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			List allRecords = null;
			
			String property = request.getParameter("externalKey");
			String entityName = request.getParameter("class");
			if (property!=null && property.length()>0)
			{
				if (entityName != null && entityName.equals("wbpage"))
				{
					allRecords = adminStorage.query(WPBPage.class, "externalKey", AdminQueryOperator.EQUAL, property);
				}
				if (entityName != null && entityName.equals("wbfile"))
				{
					allRecords = adminStorage.query(WPBFile.class, "externalKey", AdminQueryOperator.EQUAL, property);
				}				
			} else
			{
				allRecords = new ArrayList<WPBPage>();
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(allRecords));
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
}
