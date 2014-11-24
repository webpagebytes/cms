package com.webpagebytes.cms.controllers;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.webpagebytes.cms.cmsdata.WBUri;

public class WBUriValidator {
	public final static int MAX_URI_LENGHT = 255;
	public final static int MAX_CONTROLLER_LENGHT = 255;
	
	public final static int MAX_EXTERNAL_KEY = 100;
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
				errors.put("uri", WBErrors.ERROR_INVALID_VALUE);
			}
		}
		
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_VALUE);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_VALUE);
		}
		if (wbUri.getControllerClass()!= null && !wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*") && wbUri.getControllerClass().length() > MAX_CONTROLLER_LENGHT)
		{
			errors.put("controllerClass", WBErrors.ERROR_INVALID_VALUE);
		}
		
		if ((wbUri.getPrivkey() == null) || (wbUri.getPrivkey() == 0))
		{
			errors.put("key", WBErrors.ERROR_NO_KEY);
		}
		if (wbUri.getLastModified() != null)
		{
			errors.put("lastModified", WBErrors.ERROR_CANT_SPECIFY_LAST_MODIFIED);
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_TEXT || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_URL_CONTROLLER)))
		{
			errors.put("resourceType", WBErrors.ERROR_INVALID_VALUE);
		}
			
		if (null == wbUri.getExternalKey() || 0 == wbUri.getExternalKey().length() || (wbUri.getExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("externalKey", WBErrors.ERROR_INVALID_VALUE);
		}
		if (null != wbUri.getResourceExternalKey() && (wbUri.getResourceExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("resourceExternalKey", WBErrors.ERROR_INVALID_VALUE);
		}
		if ((null == wbUri.getEnabled()) || (0 != wbUri.getEnabled() && 1 != wbUri.getEnabled()))
		{
			errors.put("enabled", WBErrors.ERROR_INVALID_VALUE);
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
				errors.put("uri", WBErrors.ERROR_INVALID_VALUE);
			}
		}
		if (wbUri.getHttpOperation() == null || wbUri.getHttpOperation().length() == 0)
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_VALUE);
		} else if (! httpOperations.contains( wbUri.getHttpOperation().toUpperCase()))
		{
			errors.put("httpOperation", WBErrors.ERROR_INVALID_VALUE);
		}
		if (wbUri.getControllerClass()!= null)
		{
			if (!wbUri.getControllerClass().matches("[0-9a-zA-Z_.-]*") || wbUri.getControllerClass().length()> MAX_CONTROLLER_LENGHT)
			{
				errors.put("controllerClass", WBErrors.ERROR_INVALID_VALUE);
			}
		}
		if (wbUri.getPrivkey() != null)
		{
			errors.put("key", WBErrors.ERROR_CANT_SPECIFY_KEY);
		}
		
		if (null == wbUri.getResourceType() || (!(wbUri.getResourceType() == WBUri.RESOURCE_TYPE_FILE || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_TEXT || wbUri.getResourceType() == WBUri.RESOURCE_TYPE_URL_CONTROLLER)))
		{
			errors.put("resourceType", WBErrors.ERROR_INVALID_VALUE);
		}
		if (null == wbUri.getExternalKey())
		{
			errors.put("externalKey", WBErrors.ERROR_INVALID_VALUE);
		}
		if (null != wbUri.getResourceExternalKey() && (wbUri.getResourceExternalKey().length() > MAX_EXTERNAL_KEY))
		{
			errors.put("resourceExternalKey", WBErrors.ERROR_INVALID_VALUE);
		}
		if ((null == wbUri.getEnabled()) || (0 != wbUri.getEnabled() && 1 != wbUri.getEnabled()))
		{
			errors.put("enabled", WBErrors.ERROR_INVALID_VALUE);
		}
	
		return errors;
	}
	
}
