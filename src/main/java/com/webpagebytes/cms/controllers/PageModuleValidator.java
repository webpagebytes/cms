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
