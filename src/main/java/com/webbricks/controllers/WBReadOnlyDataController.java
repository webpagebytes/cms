package com.webbricks.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.exception.WBException;
import com.webbricks.utility.HttpServletToolbox;

public class WBReadOnlyDataController  extends WBController {

	public static final String DATA_FILES = "data_files";
	public static final String DATA_PAGES = "data_pages";
	
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;


	public WBReadOnlyDataController() 
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
	}
	
	public void getShortDataOnFilesAndPages(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		List<WBFile> files = adminStorage.getAllRecords(WBFile.class);
		List<WBWebPage> pages = adminStorage.getAllRecords(WBWebPage.class);

		
		try 
		{
			org.json.JSONObject returnJson = new org.json.JSONObject();
			org.json.JSONArray jsonFilesArray = new org.json.JSONArray(); 
			for(WBFile file : files)
			{
				JSONObject object = new JSONObject();
				object.put("externalKey", file.getExternalKey());
				object.put("name", file.getName());
				jsonFilesArray.put(object);
			}
			returnJson.put(DATA_FILES, jsonFilesArray);
			
			org.json.JSONArray jsonPagesArray = new org.json.JSONArray(); 
			for(WBWebPage page : pages)
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
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}
		
	}
	
	public void search(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBWebPage> allRecords = null;
			
			String property = request.getParameter("externalKey");
			String entityName = request.getParameter("class");
			if (property!=null && property.length()>0)
			{
				if (entityName != null && entityName.equals("wbpage"))
				{
					allRecords = adminStorage.query(WBWebPage.class, "externalKey", AdminQueryOperator.EQUAL, property);
				}
				if (entityName != null && entityName.equals("wbfile"))
				{
					allRecords = adminStorage.query(WBFile.class, "externalKey", AdminQueryOperator.EQUAL, property);
				}				
			} else
			{
				allRecords = new ArrayList<WBWebPage>();
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(allRecords));
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	
}
