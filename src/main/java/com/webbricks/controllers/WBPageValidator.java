package com.webbricks.controllers;

import java.util.HashMap;
import java.util.Map;

import com.webbricks.cmsdata.WBWebPage;

public class WBPageValidator {
	public final static int MAX_PAGENAME_LENGHT = 250;

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
		} else
		{
			if (! webPage.getName().matches("[0-9 a-zA-Z_,+.-]*"))
			{
				errors.put("name", WBErrors.ERROR_PAGE_BAD_FORMAT);
			}
		}
		if (webPage.getKey() != null)
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
		} else
		{
			if (! webPage.getName().matches("[0-9 a-zA-Z_,+.-]*"))
			{
				errors.put("name", WBErrors.ERROR_PAGE_BAD_FORMAT);
			}
		}
		if (webPage.getKey() == null)
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
		return errors;
	}

}
