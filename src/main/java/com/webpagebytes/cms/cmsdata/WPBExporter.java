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

package com.webpagebytes.cms.cmsdata;

import java.util.Map;

public class WPBExporter {

	public void export(WPBUri uri, Map<String, Object> properties)
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
			properties.put("enabled", uri.getEnabled().toString());
		else
			properties.put("enabled", "0");
		
		if (uri.getResourceExternalKey() != null)
			properties.put("resourceExternalKey", uri.getResourceExternalKey());
		else
			properties.put("resourceExternalKey", "");
		
		if (uri.getResourceType() != null)
			properties.put("resourceType", uri.getResourceType().toString());
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
			properties.put("lastModified", new Long(uri.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
	}

	public void export(WPBProject project, Map<String, Object> properties)
	{
		if (project.getDefaultLanguage() != null)
			properties.put("defaultLanguage", project.getDefaultLanguage());
		else
			properties.put("defaultLanguage", "");
		
		if (project.getSupportedLanguages() != null)
			properties.put("supportedLanguages", project.getSupportedLanguages());
		else
			properties.put("supportedLanguages", "");

		if (project.getLastModified() != null)
			properties.put("lastModified", new Long(project.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");

	}

	public void export(WPBParameter parameter, Map<String, Object> properties)
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
			properties.put("overwriteFromUrl", parameter.getOverwriteFromUrl().toString());
		else
			properties.put("overwriteFromUrl", "0");
		
		if (parameter.getLocaleType() != null)
			properties.put("localeType", parameter.getLocaleType().toString());
		else
			properties.put("localeType", "0");
		
		if (parameter.getLastModified() != null)
			properties.put("lastModified", new Long(parameter.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
			
		}
	
	public void export(WPBWebPage page, Map<String, Object> properties)
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
			properties.put("isTemplateSource", page.getIsTemplateSource().toString());
		else
			properties.put("isTemplateSource", "0");
		
		if (page.getName() != null)
			properties.put("name", page.getName());
		else
			properties.put("name", "");
		
		if (page.getPageModelProvider() != null)
			properties.put("pageModelProvider", page.getPageModelProvider());
		else
			properties.put("pageModelProvider", "");
		
		if (page.getLastModified() != null)
			properties.put("lastModified", new Long(page.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
		
	}

	public void export(WPBArticle article, Map<String, Object> properties)
	{
		if (article.getExternalKey() != null)
			properties.put("externalKey", article.getExternalKey());
		else
			properties.put("externalKey", "0");
			
		if (article.getTitle() != null)
			properties.put("title", article.getTitle());
		else
			properties.put("title", "");
			
		if (article.getLastModified() != null)
			properties.put("lastModified", new Long(article.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
		
	}

	public void export(WPBFile file, Map<String, Object> properties)
	{
		if (file.getExternalKey() != null)
			properties.put("externalKey", file.getExternalKey());
		else
			properties.put("externalKey", "0");
		
		if (file.getContentType() != null)
			properties.put("shortType", file.getShortType());
		else
			properties.put("shortType", "");
		
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
			properties.put("lastModified", new Long(file.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
		
		if (file.getFileName() != null)
			properties.put("fileName", file.getFileName());
		else
			properties.put("fileName", "");
			
		
	}

	public void export(WPBWebPageModule module, Map<String, Object> properties)
	{
		if (module.getExternalKey() != null)
			properties.put("externalKey", module.getExternalKey());
		else
			properties.put("externalKey", "0");
				
		if (module.getIsTemplateSource() != null)
			properties.put("isTemplateSource", module.getIsTemplateSource().toString());
		else
			properties.put("isTemplateSource", "0");
		
		if (module.getName() != null)
			properties.put("name", module.getName());
		else
			properties.put("name", "");
				
		if (module.getLastModified() != null)
			properties.put("lastModified", new Long(module.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
		
	}

	public void export(WPBMessage message, Map<String, Object> properties)
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
			properties.put("lastModified", new Long(message.getLastModified().getTime()).toString());
		else
			properties.put("lastModified", "0");
		
	}

}