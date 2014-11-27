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
