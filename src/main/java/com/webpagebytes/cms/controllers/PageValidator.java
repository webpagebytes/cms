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
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WPBWebPage;

class PageValidator {
	public final static int MAX_PAGENAME_LENGHT = 250;
	public final static int MAX_CONTROLLER_LENGTH = 250;
	
	public Map<String, String> validateCreate(WPBWebPage webPage)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		
		if (webPage == null || webPage.getName().length() == 0)
		{
			errors.put("name", WPBErrors.ERROR_PAGENAME_LENGTH);
		} else
		if (webPage.getName().length() > MAX_PAGENAME_LENGHT)
		{
			errors.put("name", WPBErrors.ERROR_PAGENAME_LENGTH);
		} 
		if (webPage.getPrivkey() != null)
		{
			errors.put("key", WPBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		if (webPage.getHash() != null)
		{
			errors.put("hash", WPBErrors.ERROR_CANT_SPECIFY_HASH);
		}
		if (webPage.getLastModified() != null)
		{
			errors.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}

		if (webPage.getPageModelProvider() != null)
		{
			if (webPage.getPageModelProvider().length()> MAX_CONTROLLER_LENGTH)
			{
				errors.put("pageModelProvider", WPBErrors.ERROR_CONTROLLER_LENGTH);
			}
			if (! webPage.getPageModelProvider().matches("[0-9a-zA-Z_.]*"))
			{
				errors.put("pageModelProvider", WPBErrors.ERROR_CONTROLLER_BAD_FORMAT);
			}
		}
		return errors;
		
	}
	
	public Map<String, String> validateUpdate(WPBWebPage webPage)
	{
		HashMap<String, String> errors = new HashMap<String, String>();
		if (webPage == null || webPage.getName().length() == 0)
		{
			errors.put("name", WPBErrors.ERROR_PAGENAME_LENGTH);
		} else
		if (webPage.getName().length() > MAX_PAGENAME_LENGHT)
		{
			errors.put("name", WPBErrors.ERROR_PAGENAME_LENGTH);
		}
		if (webPage.getPrivkey() == null)
		{
			errors.put("key", WPBErrors.ERROR_NO_KEY);
		}
		if (webPage.getLastModified() != null)
		{
			errors.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		if (webPage.getHash() != null)
		{
			errors.put("hash", WPBErrors.ERROR_CANT_SPECIFY_HASH);
		}
		
		if (webPage.getPageModelProvider() != null)
		{
			if (webPage.getPageModelProvider().length()> MAX_CONTROLLER_LENGTH)
			{
				errors.put("pageModelProvider", WPBErrors.ERROR_CONTROLLER_LENGTH);
			}
			if (! webPage.getPageModelProvider().matches("[0-9a-zA-Z_.]*"))
			{
				errors.put("pageModelProvider", WPBErrors.ERROR_CONTROLLER_BAD_FORMAT);
			}
		}

		return errors;
	}

}
