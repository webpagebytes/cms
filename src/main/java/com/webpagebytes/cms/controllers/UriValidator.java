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
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WPBUri;

class UriValidator {
	public final static int MAX_URI_LENGHT = 255;
	public final static int MAX_CONTROLLER_LENGHT = 255;
	
	public final static int MAX_EXTERNAL_KEY = 100;
	private HashSet<String> httpOperations;
	
	
	public UriValidator()
	{
		httpOperations = new HashSet<String>();
		httpOperations.add("PUT");
		httpOperations.add("POST");
		httpOperations.add("GET");
		httpOperations.add("DELETE");
		httpOperations.add("PATCH");
		httpOperations.add("OPTIONS");
		httpOperations.add("HEAD");
	}
	public Map<String, String> validateUpdate(WPBUri wbUri)
	{
		Map<String, String> errors = new HashMap<String, String>();
		if (wbUri.getUri() == null || wbUri.getUri().length() == 0)
		{
			errors.put("uri", WPBErrors.ERROR_URI_LENGTH);
		} else
		if (wbUri.getUri().indexOf("/") != 0)
		{
			errors.put("uri", WPBErrors.ERROR_URI_START_CHAR);
		} else
		if (wbUri.getUri().length() > MAX_URI_LENGHT)
		{
			errors.put("uri", WPBErrors.ERROR_URI_LENGTH);
		} else
		{
			if (! wbUri.getUri().matches("/([0-9a-zA-Z_~.-]*(\\{[0-9a-zA-Z_.*-]+\\})*[0-9a-zA-Z_~.-]*/?)*"))
			{
				errors.put("uri", WPBErrors.ERROR_INVALID_VALUE);
			}
		}
		
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
		}
		if (wbUri.getControllerClass()!= null && !wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*") && wbUri.getControllerClass().length() > MAX_CONTROLLER_LENGHT)
		{
			errors.put("controllerClass", WPBErrors.ERROR_INVALID_VALUE);
		}
		
		if (wbUri.getLastModified() != null)
		{
			errors.put("lastModified", WPBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT || wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_URL_CONTROLLER)))
		{
			errors.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
		}
			
		if (null == wbUri.getExternalKey() || 0 == wbUri.getExternalKey().length() || (wbUri.getExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("externalKey", WPBErrors.ERROR_NO_KEY);
		}
		if (null != wbUri.getResourceExternalKey() && (wbUri.getResourceExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("resourceExternalKey", WPBErrors.ERROR_INVALID_VALUE);
		}
		if ((null == wbUri.getEnabled()) || (0 != wbUri.getEnabled() && 1 != wbUri.getEnabled()))
		{
			errors.put("enabled", WPBErrors.ERROR_INVALID_VALUE);
		}
		return errors;
		
	}
	
	public Map<String, String> validateCreateWithExternalKey(WPBUri wbUri)
	{
		Map<String, String> errors = validateCreate(wbUri);
		if (null == wbUri.getExternalKey() || 0 == wbUri.getExternalKey().length() || (wbUri.getExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("externalKey", WPBErrors.ERROR_INVALID_VALUE);
		}
		return errors;		
	}
	
	public Map<String, String> validateCreate(WPBUri wbUri)
	{
		Map<String, String> errors = new HashMap<String, String>();
		if (wbUri.getUri() == null || wbUri.getUri().length() == 0)
		{
			errors.put("uri", WPBErrors.ERROR_URI_LENGTH);
		} else
		if (wbUri.getUri().indexOf("/") != 0)
		{
			errors.put("uri", WPBErrors.ERROR_URI_START_CHAR);
		}else
		if (wbUri.getUri().length() > MAX_URI_LENGHT)
		{
			errors.put("uri", WPBErrors.ERROR_URI_LENGTH);
		} else
		{
			if (! wbUri.getUri().matches("/([0-9a-zA-Z_~.-]*(\\{[0-9a-zA-Z_.*-]+\\})*[0-9a-zA-Z_~.-]*/?)*"))
			{
				errors.put("uri", WPBErrors.ERROR_INVALID_VALUE);
			}
		}
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WPBErrors.ERROR_INVALID_VALUE);
		}
		if (wbUri.getControllerClass()!= null)
		{
			if (!wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*") || wbUri.getControllerClass().length()> MAX_CONTROLLER_LENGHT)
			{
				errors.put("controllerClass", WPBErrors.ERROR_INVALID_VALUE);
			}
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_TEXT || wbUri.getResourceType() == WPBUri.RESOURCE_TYPE_URL_CONTROLLER)))
		{
			errors.put("resourceType", WPBErrors.ERROR_INVALID_VALUE);
		}
		if (null != wbUri.getResourceExternalKey() && (wbUri.getResourceExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("resourceExternalKey", WPBErrors.ERROR_INVALID_VALUE);
		}
		if ((null == wbUri.getEnabled()) || (0 != wbUri.getEnabled() && 1 != wbUri.getEnabled()))
		{
			errors.put("enabled", WPBErrors.ERROR_INVALID_VALUE);
		}	
				
		return errors;
	}
	
}
