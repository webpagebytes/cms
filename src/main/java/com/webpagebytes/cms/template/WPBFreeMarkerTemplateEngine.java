package com.webpagebytes.cms.template;
import java.io.IOException;

import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.datautility.WPBCloudFileStorage;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.exception.WPBTemplateException;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;

public class WPBFreeMarkerTemplateEngine implements WPBTemplateEngine {
	private static final Logger log = Logger.getLogger(WPBFreeMarkerTemplateEngine.class.getName());
	private FreeMarkerResourcesFactory wbFreeMarkerFactory;
	private Configuration configuration;
	private FreeMarkerTemplateLoader templateLoader;
	private WPBCacheInstances cacheInstances;
	private WPBCloudFileStorage cloudFileStorage;
	
	public WPBFreeMarkerTemplateEngine(WPBCacheInstances cacheInstances)
	{
		wbFreeMarkerFactory = new FreeMarkerResourcesFactory();
		this.cacheInstances = cacheInstances;
	}
	
	public void initialize() throws WPBIOException
	{
		log.log(Level.INFO, "WBFreeMarkerTemplateEngine initialize");
		
		configuration = wbFreeMarkerFactory.createConfiguration();
		configuration.setDefaultEncoding("UTF-8");
		configuration.setOutputEncoding("UTF-8");
		templateLoader = wbFreeMarkerFactory.createWBFreeMarkerTemplateLoader(cacheInstances);
 
		// TBD
		//blobHandler = new WBGaeBlobHandler();
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
		
		configuration.setLocalizedLookup(false);
		configuration.setTemplateLoader( templateLoader );	
		
		FreeMarkerModuleDirective moduleDirective = wbFreeMarkerFactory.createWBFreeMarkerModuleDirective();
		moduleDirective.initialize(this, cacheInstances);
		configuration.setSharedVariable(WPBModel.MODULE_DIRECTIVE, moduleDirective);
		
		FreeMarkerImageDirective imageDirective = wbFreeMarkerFactory.createWBFreeMarkerImageDirective();
		imageDirective.initialize(cloudFileStorage, cacheInstances);
		configuration.setSharedVariable(WPBModel.IMAGE_DIRECTIVE, imageDirective);
		
		FreeMarkerArticleDirective articleDirective = wbFreeMarkerFactory.createWBFreeMarkerArticleDirective();
		articleDirective.initialize(this, cacheInstances);
		configuration.setSharedVariable(WPBModel.ARTICLE_DIRECTIVE, articleDirective);
				
	}
	public void process(String templateName, Map<String, Object> rootMap, Writer out) throws WPBException
	{
		try {
			log.log(Level.INFO, "call WBFreeMarkerTemplateEngine process for " + templateName);
					
			Template t = configuration.getTemplate(templateName);
			
			Object textFormatMethod = rootMap.get(WPBModel.FORMAT_TEXT_METHOD);
			if (textFormatMethod == null)
			{
				textFormatMethod = new FreeMarkerTextFormatMethod();
				rootMap.put(WPBModel.FORMAT_TEXT_METHOD, textFormatMethod);
			}
					
			if (null == rootMap.get(WPBModel.LOCALE_MESSAGES))
			{
				Locale locale = null;
				String localeLanguage = (String) rootMap.get(WPBModel.LOCALE_LANGUAGE_KEY);
				String localeCountry = (String) rootMap.get(WPBModel.LOCALE_COUNTRY_KEY);

				if (localeCountry !=null && localeCountry.length()>0)
				{
					locale = new Locale(localeLanguage, localeCountry);
				} else
				{
					locale = new Locale(localeLanguage);
				}
				log.log(Level.INFO, "WBFreeMarkerTemplateEngine process create resource bundle for " + locale.toString());	
				CmsResourceBundle r = wbFreeMarkerFactory.createResourceBundle(cacheInstances.getWBMessageCache(), locale);
				ResourceBundleModel fmBundle = new ResourceBundleModel(r, new DefaultObjectWrapper()); 
				rootMap.put(WPBModel.LOCALE_MESSAGES, fmBundle);
			} else
			{
				log.log(Level.INFO, "WBFreeMarkerTemplateEngine process found wbmessages in root " + templateName);	
			}
			
			Set<String> rootKeys = rootMap.keySet();
			for(String key: rootKeys)
			{
				Object params = rootMap.get(key);
				if (params instanceof Map)
				{
					TemplateHashModel hashModel = new SimpleMapModel((Map)params, new DefaultObjectWrapper());
					rootMap.put(key, hashModel);			
				} else
				if (params instanceof String)
				{
					// leave this as it is for now
				}
			}

			Environment env = t.createProcessingEnvironment(rootMap, out);
			env.process();
		} 
		catch (TemplateException e)
		{
			throw new WPBTemplateException("Freemarker Template Exception " + e.getMessage(), e);
		}		
		catch (ParseException e)
		{
			throw new WPBTemplateException("Freemarker Template Exception " + e.getMessage(), e);
		}
		catch (IOException e)
		{
			throw (new WPBIOException("IO Exception", e));
		}
	}
	
}
