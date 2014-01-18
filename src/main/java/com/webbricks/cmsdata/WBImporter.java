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

	public WBWebPage buildWebPage(Map<Object, Object> properties)
	{
		WBWebPage page = new WBWebPage();
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

		if (properties.get("htmlSource") != null)
		{
			page.setHtmlSource(properties.get("htmlSource").toString().trim());
		} else
		{
			page.setHtmlSource("");
		}
		page.setHash( page.crc32(page.getHtmlSource()));
		
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

	public WBFile buildFile(Map<Object, Object> properties)
	{
		WBFile file = new WBFile();
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

		if (properties.get("shortType") != null)
		{
			file.setShortType(properties.get("shortType").toString().trim());
		}

		if (properties.get("fileName") != null)
		{
			file.setFileName(properties.get("fileName").toString().trim());
		}

		if (properties.get("name") != null)
		{
			file.setName(properties.get("name").toString().trim());
		}

		String lastModifiedStr = (String) properties.get("lastModified");
		Long lastModified = 0L;
		try
		{
			lastModified = lastModifiedStr != null ? Long.valueOf(lastModifiedStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		file.setLastModified(new Date(lastModified));

		String sizeStr = (String) properties.get("size");
		Long size = 0L;
		try
		{
			size = sizeStr != null ?  Long.valueOf(sizeStr): 0;
		} catch (NumberFormatException e)
		{
			
		}
		file.setSize(size);
		return file;
	}

	public WBWebPageModule buildWebPageModule(Map<Object, Object> properties)
	{
		WBWebPageModule pageModule = new WBWebPageModule();
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

	public WBProject buildProject(Map<Object, Object> properties)
	{
		WBProject project = new WBProject();
		if (properties.get("defaultLanguage") != null)
			project.setDefaultLanguage(properties.get("defaultLanguage").toString().trim());
		else
			project.setDefaultLanguage("");

		if (properties.get("supportedLanguages") != null)
			project.setSupportedLanguages(properties.get("supportedLanguages").toString().trim());
		else
			project.setSupportedLanguages("");
		
		project.setKey(WBProject.PROJECT_KEY);
		return project;
	}

	public WBArticle buildArticle(Map<Object, Object> properties)
	{
		WBArticle article = new WBArticle();
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

	public WBMessage buildMessage(Map<Object, Object> properties)
	{
		WBMessage message = new WBMessage();
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
