package com.webbricks.template;
import java.io.IOException;
import java.io.Writer;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBCacheInstances;
import com.webbricks.cache.WBMessagesCache;
import com.webbricks.cache.WBParametersCache;
import com.webbricks.cms.BaseModelProvider;
import com.webbricks.cms.PageContentBuilder;
import com.webbricks.datautility.WBBlobHandler;
import com.webbricks.datautility.WBGaeBlobHandler;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.exception.WBTemplateException;

import freemarker.core.Environment;
import freemarker.core.ParseException;
import freemarker.ext.beans.BeansWrapper;
import freemarker.ext.beans.ResourceBundleModel;
import freemarker.ext.beans.SimpleMapModel;
import freemarker.ext.beans.StringModel;
import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;

public class WBFreeMarkerTemplateEngine implements WBTemplateEngine {
	private static final Logger log = Logger.getLogger(WBFreeMarkerTemplateEngine.class.getName());
	private WBFreeMarkerFactory wbFreeMarkerFactory;
	private Configuration configuration;
	private WBFreeMarkerTemplateLoader templateLoader;
	private WBCacheInstances cacheInstances;
	private WBBlobHandler blobHandler;
	
	public WBFreeMarkerTemplateEngine(WBCacheInstances cacheInstances)
	{
		wbFreeMarkerFactory = new WBFreeMarkerFactory();
		this.cacheInstances = cacheInstances;
	}
	
	public void initialize() throws WBIOException
	{
		log.log(Level.INFO, "WBFreeMarkerTemplateEngine initialize");
		
		configuration = wbFreeMarkerFactory.createConfiguration();
		configuration.setDefaultEncoding("UTF-8");
		configuration.setOutputEncoding("UTF-8");
		templateLoader = wbFreeMarkerFactory.createWBFreeMarkerTemplateLoader(cacheInstances);
 
		// TBD
		blobHandler = new WBGaeBlobHandler();
		
		configuration.setLocalizedLookup(false);
		configuration.setTemplateLoader( templateLoader );	
		
		WBFreeMarkerModuleDirective moduleDirective = wbFreeMarkerFactory.createWBFreeMarkerModuleDirective();
		moduleDirective.initialize(this, cacheInstances);
		configuration.setSharedVariable(PageContentBuilder.MODULE_DIRECTIVE, moduleDirective);
		
		WBFreeMarkerImageDirective imageDirective = wbFreeMarkerFactory.createWBFreeMarkerImageDirective();
		imageDirective.initialize(blobHandler, cacheInstances);
		configuration.setSharedVariable(PageContentBuilder.IMAGE_DIRECTIVE, imageDirective);
		
		WBFreeMarkerArticleDirective articleDirective = wbFreeMarkerFactory.createWBFreeMarkerArticleDirective();
		articleDirective.initialize(this, cacheInstances);
		configuration.setSharedVariable(PageContentBuilder.ARTICLE_DIRECTIVE, articleDirective);
				
	}
	public void process(String templateName, Map<String, Object> rootMap, Writer out) throws WBException
	{
		try {
			log.log(Level.INFO, "call WBFreeMarkerTemplateEngine process for " + templateName);
					
			Template t = configuration.getTemplate(templateName);
			
			Object textFormatMethod = rootMap.get(PageContentBuilder.FORMAT_TEXT_METHOD);
			if (textFormatMethod == null)
			{
				textFormatMethod = new WBFreeMarkerTextFormatMethod();
				rootMap.put(PageContentBuilder.FORMAT_TEXT_METHOD, textFormatMethod);
			}
					
			if (null == rootMap.get(PageContentBuilder.LOCALE_MESSAGES))
			{
				Locale locale = null;
				String localeLanguage = (String) rootMap.get(PageContentBuilder.LOCALE_LANGUAGE_KEY);
				String localeCountry = (String) rootMap.get(PageContentBuilder.LOCALE_COUNTRY_KEY);

				if (localeCountry !=null && localeCountry.length()>0)
				{
					locale = new Locale(localeLanguage, localeCountry);
				} else
				{
					locale = new Locale(localeLanguage);
				}
				log.log(Level.INFO, "WBFreeMarkerTemplateEngine process create resource bundle for " + locale.toString());	
				WBResourceBundle r = wbFreeMarkerFactory.createResourceBundle(cacheInstances.getWBMessageCache(), locale);
				ResourceBundleModel fmBundle = new ResourceBundleModel(r, new DefaultObjectWrapper()); 
				rootMap.put(PageContentBuilder.LOCALE_MESSAGES, fmBundle);
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
			throw new WBTemplateException("Freemarker Template Exception " + e.getMessage(), e);
		}		
		catch (ParseException e)
		{
			throw new WBTemplateException("Freemarker Template Exception " + e.getMessage(), e);
		}
		catch (IOException e)
		{
			throw (new WBIOException("IO Exception", e));
		}
	}
	
}
