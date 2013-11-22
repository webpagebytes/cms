package com.webbricks.cmsdata;

import java.util.Map;

public class WBExporter {

	public void export(WBUri uri, Map<String, Object> properties)
	{
		if (uri.getExternalKey() != null)
			properties.put("externalKey", uri.getExternalKey());
		else
			properties.put("externalKey", "");
		
		if (uri.getUri() != null)
			properties.put("uri", uri.getUri());
		else
			properties.put("uri", "");
		
		if (uri.getEnabled() != null)
			properties.put("enabled", uri.getEnabled());
		else
			properties.put("enabled", "0");
		
		if (uri.getResourceExternalKey() != null)
			properties.put("resourceExternalKey", uri.getResourceExternalKey());
		else
			properties.put("resourceExternalKey", "");
		
		if (uri.getResourceType() != null)
			properties.put("resourceType", uri.getResourceType());
		else
			properties.put("resourceType", "0");
		
		if (uri.getHttpOperation() != null)
			properties.put("httpOperation", uri.getHttpOperation());
		else
			properties.put("httpOperation", "");
		
		if (uri.getControllerClass() != null)
			properties.put("controllerClass", uri.getControllerClass());
		else
			properties.put("controllerClass", "");
		
		if (uri.getLastModified() != null)
			properties.put("lastModified", uri.getLastModified().getTime());
		else
			properties.put("lastModified", "0");
	}

	public void export(WBParameter parameter, Map<String, Object> properties)
	{
		if (parameter.getExternalKey() != null)
			properties.put("externalKey", parameter.getExternalKey());
		else
			properties.put("externalKey", "");

		if (parameter.getName() != null)
			properties.put("name", parameter.getName());
		else
			properties.put("name", "");

		if (parameter.getValue() != null)
			properties.put("value", parameter.getValue());
		else
			properties.put("value", "");
		
		if (parameter.getOwnerExternalKey() != null)
			properties.put("ownerExternalKey", parameter.getOwnerExternalKey());
		else
			properties.put("ownerExternalKey", "");

		if (parameter.getOverwriteFromUrl() != null)
			properties.put("overwriteFromUrl", parameter.getOverwriteFromUrl());
		else
			properties.put("overwriteFromUrl", "0");
		
		if (parameter.getLocaleType() != null)
			properties.put("localeType", parameter.getLocaleType());
		else
			properties.put("localeType", "0");
			
		}
	
	public void export(WBWebPage page, Map<String, Object> properties)
	{
		if (page.getExternalKey() != null)
			properties.put("externalKey", page.getExternalKey());
		else
			properties.put("externalKey", "0");
		
		if (page.getContentType() != null)
			properties.put("contentType", page.getContentType());
		else
			properties.put("contentType", "");
		
		if (page.getIsTemplateSource() != null)
			properties.put("isTemplateSource", page.getIsTemplateSource());
		else
			properties.put("isTemplateSource", "0");
		
		if (page.getName() != null)
			properties.put("name", page.getName());
		else
			properties.put("name", "");
		
		if (page.getPageModelProvider() != null)
			properties.put("pageModelprovider", page.getPageModelProvider());
		else
			properties.put("pageModelprovider", "");
		
		if (page.getLastModified() != null)
			properties.put("lastModified", page.getLastModified());
		else
			properties.put("lastModified", "0");
		
	}

	public void export(WBFile file, Map<String, Object> properties)
	{
		if (file.getExternalKey() != null)
			properties.put("externalKey", file.getExternalKey());
		else
			properties.put("externalKey", "0");
		
		if (file.getContentType() != null)
			properties.put("contentType", file.getContentType());
		else
			properties.put("contentType", "");

		if (file.getAdjustedContentType() != null)
			properties.put("adjustedContentType", file.getAdjustedContentType());
		else
			properties.put("adjustedContentType", "");

		if (file.getName() != null)
			properties.put("name", file.getName());
		else
			properties.put("name", "");
				
		if (file.getLastModified() != null)
			properties.put("lastModified", file.getLastModified());
		else
			properties.put("lastModified", "0");
		
		if (file.getFileName() != null)
			properties.put("fileName", file.getFileName());
		else
			properties.put("fileName", "");
			
		
	}

	public void export(WBWebPageModule module, Map<String, Object> properties)
	{
		if (module.getExternalKey() != null)
			properties.put("externalKey", module.getExternalKey());
		else
			properties.put("externalKey", "0");
				
		if (module.getIsTemplateSource() != null)
			properties.put("isTemplateSource", module.getIsTemplateSource());
		else
			properties.put("isTemplateSource", "0");
		
		if (module.getName() != null)
			properties.put("name", module.getName());
		else
			properties.put("name", "");
				
		if (module.getLastModified() != null)
			properties.put("lastModified", module.getLastModified());
		else
			properties.put("lastModified", "0");
		
	}

	public void export(WBMessage message, Map<String, Object> properties)
	{
		if (message.getExternalKey() != null)
			properties.put("externalKey", message.getExternalKey());
		else
			properties.put("externalKey", "0");
						
		if (message.getName() != null)
			properties.put("name", message.getName());
		else
			properties.put("name", "");

		if (message.getValue() != null)
			properties.put("value", message.getValue());
		else
			properties.put("value", "");

		if (message.getLastModified() != null)
			properties.put("lastModified", message.getLastModified());
		else
			properties.put("lastModified", "0");
		
	}

}