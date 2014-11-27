package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WBWebPage;

class PageValidator {
	public final static int MAX_PAGENAME_LENGHT = 250;
	public final static int MAX_CONTROLLER_LENGTH = 250;
	
	public Map<String, String> validateCreate(WBWebPage webPage)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		
		if (webPage == null || webPage.getName().length() == 0)
		{
			errors.put("name", WBErrors.ERROR_PAGENAME_LENGTH);
		} else
		if (webPage.getName().length() > MAX_PAGENAME_LENGHT)
		{
			errors.put("name", WBErrors.ERROR_PAGENAME_LENGTH);
		} 
		if (webPage.getPrivkey() != null)
		{
			errors.put("key", WBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		if (webPage.getHash() != null)
		{
			errors.put("hash", WBErrors.ERROR_CANT_SPECIFY_HASH);
		}
		if (webPage.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		if (webPage.getPageModelProvider() != null)
		{
			if (webPage.getPageModelProvider().length()> MAX_CONTROLLER_LENGTH)
			{
				errors.put("pageModelProvider", WBErrors.ERROR_CONTROLLER_LENGTH);
			}
			if (! webPage.getPageModelProvider().matches("[0-9a-zA-Z_.]*"))
			{
				errors.put("pageModelProvider", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
			}
		}
		return errors;
		
	}
	
	public Map<String, String> validateUpdate(WBWebPage webPage)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		if (webPage == null || webPage.getName().length() == 0)
		{
			errors.put("name", WBErrors.ERROR_PAGENAME_LENGTH);
		} else
		if (webPage.getName().length() > MAX_PAGENAME_LENGHT)
		{
			errors.put("name", WBErrors.ERROR_PAGENAME_LENGTH);
		}
		if (webPage.getPrivkey() == null)
		{
			errors.put("key", WBErrors.ERROR_NO_KEY);
		}
		if (webPage.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		if (webPage.getHash() != null)
		{
			errors.put("hash", WBErrors.ERROR_CANT_SPECIFY_HASH);
		}
		
		if (webPage.getPageModelProvider() != null)
		{
			if (webPage.getPageModelProvider().length()> MAX_CONTROLLER_LENGTH)
			{
				errors.put("pageModelProvider", WBErrors.ERROR_CONTROLLER_LENGTH);
			}
			if (! webPage.getPageModelProvider().matches("[0-9a-zA-Z_.]*"))
			{
				errors.put("pageModelProvider", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
			}
		}

		return errors;
	}

}
