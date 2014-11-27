package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WPBWebPageModule;

public class PageModuleValidator {

	public final static int MAX_PAGEMODULE_NAME_LENGHT = 250;

	public Map<String, String> validateCreate(WPBWebPageModule webPageModule)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		
		if (webPageModule == null || webPageModule.getName().length() == 0)
		{
			errors.put("name", WPBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		if (webPageModule.getName().length() > MAX_PAGEMODULE_NAME_LENGHT)
		{
			errors.put("name", WPBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		}
		if (webPageModule.getPrivkey() != null)
		{
			errors.put("key", WPBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		if (webPageModule.getLastModified() != null)
		{
			errors.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		return errors;
		
	}
	
	public Map<String, String> validateUpdate(WPBWebPageModule webPageModule)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		if (webPageModule == null || webPageModule.getName().length() == 0)
		{
			errors.put("name", WPBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		} else
		if (webPageModule.getName().length() > MAX_PAGEMODULE_NAME_LENGHT)
		{
			errors.put("name", WPBErrors.ERROR_PAGE_MODULENAME_LENGTH);
		}
		if (webPageModule.getPrivkey() == null)
		{
			errors.put("key", WPBErrors.ERROR_NO_KEY);
		}
		if (webPageModule.getLastModified() != null)
		{
			errors.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		return errors;
	}

}
