package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.cmsdata.WBMessage;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.exception.WBIOException;

public class WBMessageValidator {
	
	AdminDataStorage adminStorage;
	
	public WBMessageValidator()
	{
		
	}
	public Map<String, String> validateCreate(WBMessage message)
	{
		Map<String, String> errors = new HashMap<String, String>();
		String name = message.getName().trim();
		if (name.length() == 0)
		{
			errors.put("name", WBErrors.WBMESSAGE_EMPTY_NAME);
			return errors;
		}
		String lcid = message.getLcid().trim();
		if (lcid.length() == 0)
		{
			errors.put("lcid", WBErrors.WBMESSAGE_EMPTY_LCID);
			return errors;
		}
		
		try
		{
			Set<String> properties = new HashSet<String>();
			properties.add("name");
			properties.add("lcid");
			
			Map<String, AdminQueryOperator> operators = new HashMap<String, AdminQueryOperator>();
			operators.put("name", AdminQueryOperator.EQUAL);
			operators.put("lcid", AdminQueryOperator.EQUAL);

			Map<String, Object> values = new HashMap<String, Object>();
			values.put("name", name);
			values.put("lcid", lcid);

			
			List<WBMessage> records = adminStorage.queryEx(WBMessage.class, properties, operators, values);
			if (records != null && records.size() > 0)
			{
				errors.put("name", WBErrors.WBMESSAGE_DUPLICATE_NAME);
			}
		} catch (WBIOException e)
		{
			errors.put("general", WBErrors.WB_UNKNOWN_ERROR);
		}
		return errors;
	}
	public Map<String, String> validateUpdate(WBMessage message)
	{
		return new HashMap<String, String>();
	}
	
	public void setAdminStorage(AdminDataStorage adminStorage) {
		this.adminStorage = adminStorage;
	}

	
}
