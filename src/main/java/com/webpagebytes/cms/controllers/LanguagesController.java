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
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.webpagebytes.cms.LanguageLocaleManager;
import com.webpagebytes.cms.appinterfaces.WPBProjectCache;
import com.webpagebytes.cms.cache.DefaultWPBCacheFactory;
import com.webpagebytes.cms.cache.WPBCacheFactory;
import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

public class LanguagesController extends Controller implements WPBAdminDataStorageListener<Object> {

	private LanguageLocaleManager localeManager;
	private WPBAdminDataStorage adminStorage;
	private ArrayList<String> sortedLanguages;
	private Map<String, Locale> allLocales;
	private WPBProjectCache projectCache;
	
	private WPBProject getProject() throws WPBIOException
	{
		WPBProject project = adminStorage.get(WPBProject.PROJECT_KEY, WPBProject.class);
		if (null == project)
		{
			project = new WPBProject();
			project.setPrivkey("wbprojectid");
			project.setDefaultLanguage("en");
			project.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			project.setSupportedLanguages("en");
			project = adminStorage.addWithKey(project);
		}
		return project;
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WPBProject.class))
			{
				projectCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// TBD
		}
	}

	public LanguagesController()
	{
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		localeManager = LanguageLocaleManager.getInstance();
		sortedLanguages = new ArrayList<String>();
		
		allLocales = localeManager.getSupportedLanguagesAndCountries();
		Set<String> keyset = allLocales.keySet();
		sortedLanguages.addAll(keyset);
		Collections.sort(sortedLanguages);
	
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		projectCache = wbCacheFactory.getProjectCacheInstance();
		
		adminStorage.addStorageListener(this);

	}

	public void getAllLanguages(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		JSONArray result = new JSONArray();		
		try
		{
			for(String str: sortedLanguages)
			{
				JSONObject item = new JSONObject();
				item.put("lcid", str);
				item.put("name", allLocales.get(str).getDisplayName());
				result.put(item);
			}
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, result);
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);	
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("WBErrors.WB_CANT_GET_RECORDS", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "{}", errors);			
		}
		
	}
	
	public void getSupportedLanguages(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		WPBProject project = getProject();
		Set<String> projectLanguages = project.getSupportedLanguagesSet();
		Set<String> supportedlanguages = new HashSet<String>();
		for(String language: projectLanguages)
		{
			if (allLocales.containsKey(language))
			{
				supportedlanguages.add(language);
			}			
		}

		JSONArray result = new JSONArray();		
		try
		{
			for(String str: sortedLanguages)
			{
				if (supportedlanguages.contains(str))
				{
					JSONObject item = new JSONObject();
					item.put("lcid", str);
					item.put("name", allLocales.get(str).getDisplayName());
					if (str.compareTo(project.getDefaultLanguage()) == 0)
					{
						item.put("default", "true");
					} else
					{
						item.put("default", "false");
					}
					result.put(item);
				}
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, result);
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);	
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("WBErrors.WB_CANT_GET_RECORDS", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	
	public void setSupportedLanguages(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String requestBody = httpServletToolbox.getBodyText(request);
			JSONArray array = new JSONArray(requestBody);
			Set<String> inputLanguages = new HashSet<String>();
			String defaultLanguage = "";
			Map<String, String> errors = new HashMap<String, String>();					
			for(int i=0; i< array.length(); i++)
			{
				JSONObject aJson = array.getJSONObject(i);
				String aLanguage = aJson.getString("lcid");
				if (inputLanguages.contains(aLanguage))
				{
					errors.put(WPBErrors.WB_TWO_IDENTICAL_LANGUAGES, WPBErrors.WB_TWO_IDENTICAL_LANGUAGES);
				}
				if (!allLocales.containsKey(aLanguage))
				{
					errors.put(WPBErrors.WB_INVALID_LANGUAGE, WPBErrors.WB_INVALID_LANGUAGE);
				}
				
				String def = aJson.getString("default");
				if (def.equals("true"))
				{
					if (defaultLanguage.length()>0)
					{
						errors.put(WPBErrors.WB_TWO_DEFAULT_LANGUAGES, WPBErrors.WB_TWO_DEFAULT_LANGUAGES);
					}
					defaultLanguage = aLanguage;
				} 	
				inputLanguages.add(aLanguage);
			}
			if (array.length() == 0)
			{
				errors.put(WPBErrors.WB_NO_LANGUAGES, WPBErrors.WB_NO_LANGUAGES);				
			}
		
			if (array.length()> 0 && defaultLanguage.length() == 0)
			{
				errors.put(WPBErrors.WB_NO_DEFAULT_LANGUAGES, WPBErrors.WB_NO_DEFAULT_LANGUAGES);				
			}
			if (errors.size() > 0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);						
				return;
			}
			String selections = "";
			for(String str: inputLanguages)
			{
				if (selections.length()>0)
				{
					selections += ",";
				}
				selections += str;
			}
			
			WPBProject project = getProject();
			project.setDefaultLanguage(defaultLanguage);
			project.setSupportedLanguages(selections);
			project.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());			
			adminStorage.update(project);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put(WPBErrors.WB_CANT_UPDATE_RECORD, WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);						
		}
		
	}
	
	
}
