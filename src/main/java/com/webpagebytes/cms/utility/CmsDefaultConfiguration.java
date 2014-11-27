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
