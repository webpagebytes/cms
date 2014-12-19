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

import java.util.Date;
import java.util.Map;

public class WPBImporter {

	public WPBParameter buildParameter(Map<Object, Object> properties)
	{
		WPBParameter parameter = new WPBParameter();
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
	public WPBUri buildUri(Map<Object, Object> properties)
	{
		WPBUri uri = new WPBUri();
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

	public WPBPage buildWebPage(Map<Object, Object> properties)
	{
		WPBPage page = new WPBPage();
		if (properties.get("externalKey") != null)
			page.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;
		
		if (properties.get("contentType") != null)
		{
			page.setContentType(properties.get("contentType").toString().trim());
		}

		if (properties.get("name") != null)
		{
			page.setName(properties.get("name").toString().trim());
		} else
		{
			page.setName("");
		}

		if (properties.get("pageModelProvider") != null)
		{
			page.setPageModelProvider(properties.get("pageModelProvider").toString().trim());
		} else
		{
			page.setPageModelProvider("");
		}

		if (properties.get("htmlSource") != null)
		{
			page.setHtmlSource(properties.get("htmlSource").toString().trim());
		} else
		{
			page.setHtmlSource("");
		}
		page.setHash( WPBPage.crc32(page.getHtmlSource()));
		
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		page.setLastModified(new Date(lastModified));

		String isTemplateSourceStr = (String) properties.get("isTemplateSource");
		Integer isTemplateSource = isTemplateSourceStr != null && !isTemplateSourceStr.equals("0") ? 1: 0;
		page.setIsTemplateSource(isTemplateSource);

		return page;
	}

	public WPBFile buildFile(Map<Object, Object> properties)
	{
		WPBFile file = new WPBFile();
		if (properties.get("externalKey") != null)
			file.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;
		
		if (properties.get("contentType") != null)
		{
			file.setContentType(properties.get("contentType").toString().trim());
		}

		if (properties.get("adjustedContentType") != null)
		{
			file.setAdjustedContentType(properties.get("adjustedContentType").toString().trim());
		}

		if (properties.get("fileName") != null)
		{
			file.setFileName(properties.get("fileName").toString().trim());
		}

		if (properties.get("name") != null)
		{
			file.setName(properties.get("name").toString().trim());
		}
		if (properties.get("ownerExtKey") != null)
		{
		    file.setOwnerExtKey(properties.get("name").toString().trim());
		} else
		{
		    file.setOwnerExtKey("");
		}
		if (properties.get("directoryFlag") != null)
		{
		    file.setDirectoryFlag(properties.get("directoryFlag").toString().trim().equals("1") ? 1 : 0 );
		} else
		{
		    file.setDirectoryFlag(0);
		}
		
		file.setSize(0L);
		file.setBlobKey("");
		file.setHash(0L);
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		file.setLastModified(new Date(lastModified));

		return file;
	}

	public WPBPageModule buildWebPageModule(Map<Object, Object> properties)
	{
		WPBPageModule pageModule = new WPBPageModule();
		if (properties.get("externalKey") != null)
			pageModule.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;

		if (properties.get("name") != null)
		{
			pageModule.setName(properties.get("name").toString().trim());
		} else
		{
			pageModule.setName("");
		}

		if (properties.get("htmlSource") != null)
		{
			pageModule.setHtmlSource(properties.get("htmlSource").toString().trim());
		} else
		{
			pageModule.setHtmlSource("");
		}
		
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		pageModule.setLastModified(new Date(lastModified));

		String isTemplateSourceStr = (String) properties.get("isTemplateSource");
		Integer isTemplateSource = isTemplateSourceStr != null && !isTemplateSourceStr.equals("0") ? 1: 0;
		pageModule.setIsTemplateSource(isTemplateSource);

		return pageModule;
	}

	public WPBProject buildProject(Map<Object, Object> properties)
	{
		WPBProject project = new WPBProject();
		if (properties.get("defaultLanguage") != null)
			project.setDefaultLanguage(properties.get("defaultLanguage").toString().trim());
		else
			project.setDefaultLanguage("");

		if (properties.get("supportedLanguages") != null)
			project.setSupportedLanguages(properties.get("supportedLanguages").toString().trim());
		else
			project.setSupportedLanguages("");
		
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		project.setLastModified(new Date(lastModified));

		project.setPrivkey(WPBProject.PROJECT_KEY);
		return project;
	}

	public WPBArticle buildArticle(Map<Object, Object> properties)
	{
		WPBArticle article = new WPBArticle();
		if (properties.get("externalKey") != null)
			article.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;

		if (properties.get("title") != null)
		{
			article.setTitle(properties.get("title").toString().trim());
		} else
		{
			article.setTitle("");
		}

		if (properties.get("htmlSource") != null)
		{
			article.setHtmlSource(properties.get("htmlSource").toString().trim());
		} else
		{
			article.setHtmlSource("");
		}
		
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		article.setLastModified(new Date(lastModified));

		return article;
	}

	public WPBMessage buildMessage(Map<Object, Object> properties)
	{
		WPBMessage message = new WPBMessage();
		if (properties.get("externalKey") != null)
			message.setExternalKey(properties.get("externalKey").toString().trim());
		else
			return null;

		if (properties.get("name") != null)
		{
			message.setName(properties.get("name").toString().trim());
		}
		if (properties.get("value") != null)
		{
			message.setValue(properties.get("value").toString().trim());
		}
		if (properties.get("lcid") != null)
		{
			message.setLcid(properties.get("lcid").toString().trim());
		}
		if (properties.get("isTranslated") != null)
		{
			message.setIsTranslated( properties.get("isTranslated").equals("1") ? 1: 0);
		} else
		{
			message.setIsTranslated(0);
		}
	
		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		message.setLastModified(new Date(lastModified));

		return message;
	}

}
