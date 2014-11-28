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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.cmsdata.WPBMessage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage;
import com.webpagebytes.cms.datautility.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.exception.WPBIOException;

public class MessageValidator {
	
	WPBAdminDataStorage adminStorage;
	
	public MessageValidator()
	{
		
	}
	public Map<String, String> validateCreate(WPBMessage message)
	{
		Map<String, String> errors = new HashMap<String, String>();
		String name = message.getName().trim();
		if (name.length() == 0)
		{
			errors.put("name", WPBErrors.WBMESSAGE_EMPTY_NAME);
			return errors;
		}
		String lcid = message.getLcid().trim();
		if (lcid.length() == 0)
		{
			errors.put("lcid", WPBErrors.WBMESSAGE_EMPTY_LCID);
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

			
			List<WPBMessage> records = adminStorage.queryEx(WPBMessage.class, properties, operators, values);
			if (records != null && records.size() > 0)
			{
				errors.put("name", WPBErrors.WBMESSAGE_DUPLICATE_NAME);
			}
		} catch (WPBIOException e)
		{
			errors.put("general", WPBErrors.WB_UNKNOWN_ERROR);
		}
		return errors;
	}
	public Map<String, String> validateUpdate(WPBMessage message)
	{
		return new HashMap<String, String>();
	}
	
	public void setAdminStorage(WPBAdminDataStorage adminStorage) {
		this.adminStorage = adminStorage;
	}

	
}
