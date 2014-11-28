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

package com.webpagebytes.cms.utility;

import java.util.HashMap;
import java.util.Map;

public class CmsDefaultConfiguration implements CmsConfiguration {
	Map<WPBSECTION, String> mapSectionClassFactories = new HashMap<WPBSECTION, String>();
	Map<WPBSECTION, Map<String, String>> mapSectionParams = new HashMap<WPBSECTION, Map<String, String>>();
	
	public String setSectionClassFactory(WPBSECTION section, String classfactory)
	{
		return mapSectionClassFactories.put(section, classfactory);
	}

	public String getSectionClassFactory(WPBSECTION section)
	{
		return mapSectionClassFactories.get(section);
	}
	
	public Map<String,String> getSectionParams(WPBSECTION section)
	{
		return mapSectionParams.get(section);
	}
	public void addParamToSection(WPBSECTION section, String paramName, String paramValue)
	{
		Map<String, String> params = mapSectionParams.get(section);
		if (params == null)
		{
			params = new HashMap<String, String>();
			mapSectionParams.put(section, params);
		}
		params.put(paramName, paramValue);
	}
	
}
