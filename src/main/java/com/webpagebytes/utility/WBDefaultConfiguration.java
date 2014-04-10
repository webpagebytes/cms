package com.webpagebytes.utility;

import java.util.HashMap;
import java.util.Map;

public class WBDefaultConfiguration implements WBConfiguration {
	Map<SECTION, String> mapSectionClassFactories = new HashMap<SECTION, String>();
	Map<SECTION, Map<String, String>> mapSectionParams = new HashMap<SECTION, Map<String, String>>();
	
	public String setSectionClassFactory(SECTION section, String classfactory)
	{
		return mapSectionClassFactories.put(section, classfactory);
	}

	public String getSectionClassFactory(SECTION section)
	{
		return mapSectionClassFactories.get(section);
	}
	
	public Map<String,String> getSectionParams(SECTION section)
	{
		return mapSectionParams.get(section);
	}
	public void addParamToSection(SECTION section, String paramName, String paramValue)
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
