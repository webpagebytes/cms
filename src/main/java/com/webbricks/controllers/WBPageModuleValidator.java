package com.webbricks.controllers;

import java.util.HashMap;
import java.util.Map;

import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;

public class WBPageModuleValidator {

	public final static int MAX_PAGEMODULE_NAME_LENGHT = 250;

	public Map<String, String> validateCreate(WBWebPageModule webPageModule)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		
		if (webPageModule == null || webPageModule.getName().length() == 0)
		{
			errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		if (webPageModule.getName().length() > MAX_PAGEMODULE_NAME_LENGHT)
		{
			errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		{
			if (! webPageModule.getName().matches("[0-9 a-zA-Z_.-]*"))
			{
				errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_BAD_FORMAT);
			}
		}
		if (webPageModule.getKey() != null)
		{
			errors.put("key", WBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		if (webPageModule.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		return errors;
		
	}
	
	public Map<String, String> validateUpdate(WBWebPageModule webPageModule)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		if (webPageModule == null || webPageModule.getName().length() == 0)
		{
			errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		if (webPageModule.getName().length() > MAX_PAGEMODULE_NAME_LENGHT)
		{
			errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		{
			if (! webPageModule.getName().matches("[0-9 a-zA-Z_.-]*"))
			{
				errors.put("name", WBErrors.ERROR_PAGE_MODULENAME_BAD_FORMAT);
			}
		}
		if (webPageModule.getKey() == null)
		{
			errors.put("key", WBErrors.ERROR_NO_KEY);
		}
		if (webPageModule.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		return errors;
	}

}
