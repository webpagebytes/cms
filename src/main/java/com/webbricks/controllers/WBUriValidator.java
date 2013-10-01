package com.webbricks.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.webbricks.cmsdata.WBUri;

public class WBUriValidator {
	public final static int MAX_URI_LENGHT = 100;
	public final static int MAX_PAGENAME_LENGHT = 250;
	private HashSet<String> httpOperations;
	
	
	public WBUriValidator()
	{
		httpOperations = new HashSet<String>();
		httpOperations.add("PUT");
		httpOperations.add("POST");
		httpOperations.add("GET");
		httpOperations.add("DELETE");
	}
	public Map<String, String> validateUpdate(WBUri wbUri)
	{
		Map<String, String> errors = new HashMap<String, String>();
		if (wbUri.getUri() == null || wbUri.getUri().length() == 0)
		{
			errors.put("uri", WBErrors.ERROR_URI_LENGTH);
		} else
		if (wbUri.getUri().indexOf("/") != 0)
		{
			errors.put("uri", WBErrors.ERROR_URI_START_CHAR);
		} else
		if (wbUri.getUri().length() > MAX_URI_LENGHT)
		{
			errors.put("uri", WBErrors.ERROR_URI_LENGTH);
		} else
		{
			if (! wbUri.getUri().matches("/([0-9a-zA-Z_~.-]*(\\{[0-9a-zA-Z_.*-]+\\})*[0-9a-zA-Z_~.-]*/?)*"))
			{
				errors.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
			}
		}
		
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
		}
		if (wbUri.getControllerClass()!= null && !wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*"))
		{
			errors.put("controllerClass", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
		}
		
		if ((wbUri.getKey() == null) || (wbUri.getKey() == 0))
		{
			errors.put("key", WBErrors.ERROR_NO_KEY);
		}
		if (wbUri.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_TEXT)))
		{
			errors.put("resourceType", WBErrors.ERROR_BAD_RESOURCE_TYPE);
		}
				
		return errors;
	}
	
	public Map<String, String> validateCreate(WBUri wbUri)
	{
		Map<String, String> errors = new HashMap<String, String>();
		if (wbUri.getUri() == null || wbUri.getUri().length() == 0)
		{
			errors.put("uri", WBErrors.ERROR_URI_LENGTH);
		} else
		if (wbUri.getUri().indexOf("/") != 0)
		{
			errors.put("uri", WBErrors.ERROR_URI_START_CHAR);
		}else
		if (wbUri.getUri().length() > MAX_URI_LENGHT)
		{
			errors.put("uri", WBErrors.ERROR_URI_LENGTH);
		} else
		{
			if (! wbUri.getUri().matches("/([0-9a-zA-Z_~.-]*(\\{[0-9a-zA-Z_.*-]+\\})*[0-9a-zA-Z_~.-]*/?)*"))
			{
				errors.put("uri", WBErrors.ERROR_URI_BAD_FORMAT);
			}
		}
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_HTTP_OPERATION);
		}
		if (wbUri.getControllerClass()!= null && !wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*"))
		{
			errors.put("controllerClass", WBErrors.ERROR_CONTROLLER_BAD_FORMAT);
		}
		if (wbUri.getKey() != null)
		{
			errors.put("key", WBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		if (wbUri.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_TEXT)))
		{
			errors.put("resourceType", WBErrors.ERROR_BAD_RESOURCE_TYPE);
		}
				
		return errors;
	}
	
}
