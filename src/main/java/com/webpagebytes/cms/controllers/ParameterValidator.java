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

import com.webpagebytes.cms.cmsdata.WPBParameter;

class ParameterValidator {
	public Map<String, String> validateCreate(WPBParameter wbParameter)
	{
		Map<String, String> errors = new HashMap<String, String>();
		String name = wbParameter.getName();
		if (name==null || name.length()==0)
		{
			errors.put("name", WPBErrors.WBPARAMETER_EMPTY_NAME);
		}
		
		return errors;
	}

	public Map<String, String> validateUpdate(WPBParameter wbParameter)
	{
		Map<String, String> errors = new HashMap<String, String>();
		String name = wbParameter.getName();
		if (name==null || name.length()==0)
		{
			errors.put("name", WPBErrors.WBPARAMETER_EMPTY_NAME);
		}

		return errors;
	}

}
