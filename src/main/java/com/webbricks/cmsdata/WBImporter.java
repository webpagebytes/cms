package com.webbricks.cmsdata;

import java.util.Date;
import java.util.Map;

public class WBImporter {

	public WBParameter buildParameter(Map<Object, Object> properties)
	{
		WBParameter parameter = new WBParameter();
		if (properties.get("externalKey") != null)
			parameter.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;

		if (properties.get("name") != null)
		{
			parameter.setName(properties.get("name").toString().trim());
		}
		if (properties.get("value") != null)
		{
			parameter.setValue(properties.get("value").toString().trim());
		}
		if (properties.get("ownerExternalKey") != null)
		{
			parameter.setOwnerExternalKey(properties.get("ownerExternalKey").toString().trim());
		}

		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		parameter.setLastModified(new Date(lastModified));

		String localeTypeStr = (String) properties.get("localeType");
		Integer localeType = 0;
		try
		{
			localeType= localeTypeStr != null ? Integer.valueOf(localeTypeStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		parameter.setLocaleType(localeType);
		
		String overwriteFromUrlStr = (String) properties.get("overwriteFromUrl");
		Integer overwriteFromUrl = 0;
		try
		{
			overwriteFromUrl = overwriteFromUrl != null ? Integer.valueOf(overwriteFromUrlStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		parameter.setOverwriteFromUrl(overwriteFromUrl);
		
		return parameter;
	}
	public WBUri buildUri(Map<Object, Object> properties)
	{
		WBUri uri = new WBUri();
		if (properties.get("externalKey") != null)
			uri.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;
		
		if (properties.get("uri") != null)
		{
			uri.setUri(properties.get("uri").toString().trim());
		}

		if (properties.get("httpOperation") != null)
		{
			uri.setHttpOperation(properties.get("httpOperation").toString().trim());
		}

		if (properties.get("resourceExternalKey") != null)
			uri.setResourceExternalKey(properties.get("resourceExternalKey").toString().trim());

		
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		uri.setLastModified(new Date(lastModified));

		String controllerClassStr = (String) properties.get("controllerClass");
		uri.setControllerClass(controllerClassStr);

		String enabledStr = (String) properties.get("enabled");
		Integer enabled = enabledStr != null && !enabledStr.equals("0") ? 1: 0;
		uri.setEnabled(enabled);

		String resourceTypeStr = (String) properties.get("resourceType");
		Integer resourceType = resourceTypeStr != null ? Integer.valueOf(resourceTypeStr): 0;
		uri.setResourceType(resourceType);

		return uri;
	}

}
