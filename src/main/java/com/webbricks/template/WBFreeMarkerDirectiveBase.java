package com.webbricks.template;

import java.io.IOException;


import java.util.Map;

import com.webbricks.cms.ModelBuilder;
import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

public class WBFreeMarkerDirectiveBase implements TemplateDirectiveModel {

public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
{
}

public void copyParams(Environment env, Map params) throws TemplateModelException
{
	TemplateHashModel dataModel = env.getDataModel();

	TemplateModel localeCountry = dataModel.get(ModelBuilder.LOCALE_COUNTRY_KEY);
	TemplateModel localeLanguage = dataModel.get(ModelBuilder.LOCALE_LANGUAGE_KEY);
	params.put(ModelBuilder.LOCALE_LANGUAGE_KEY, localeLanguage);
	params.put(ModelBuilder.LOCALE_COUNTRY_KEY, localeCountry);
	
	TemplateModel resourceBundle = dataModel.get(ModelBuilder.LOCALE_MESSAGES);
	if (resourceBundle != null)
	{
		params.put(ModelBuilder.LOCALE_MESSAGES, resourceBundle);
	}
	TemplateModel pageParams = dataModel.get(ModelBuilder.PAGE_PARAMETERS_KEY);
	if (pageParams != null)
	{
		params.put(ModelBuilder.PAGE_PARAMETERS_KEY, pageParams);
	}
	TemplateModel uriParams = dataModel.get(ModelBuilder.URI_PARAMETERS_KEY);
	if (uriParams != null)
	{
		params.put(ModelBuilder.URI_PARAMETERS_KEY, uriParams);
	}

	TemplateModel urlParams = dataModel.get(ModelBuilder.URL_REQUEST_PARAMETERS_KEY);
	if (urlParams != null)
	{
		params.put(ModelBuilder.URL_REQUEST_PARAMETERS_KEY, urlParams);
	}
	
	params.put(ModelBuilder.FORMAT_TEXT_METHOD, dataModel.get(ModelBuilder.FORMAT_TEXT_METHOD));
	
	TemplateModel globals = dataModel.get(ModelBuilder.GLOBALS_KEY);
	if (globals != null) 
	{
		params.put(ModelBuilder.GLOBALS_KEY, globals);
	}
	TemplateModel locale = dataModel.get(ModelBuilder.LOCALE_KEY);
	if (locale != null) 
	{
		params.put(ModelBuilder.LOCALE_KEY, locale);
	}
	TemplateModel protocols = dataModel.get(ModelBuilder.GLOBAL_PROTOCOL);
	if (protocols != null) 
	{
		params.put(ModelBuilder.GLOBAL_PROTOCOL, protocols);
	}
	TemplateModel request = dataModel.get(ModelBuilder.REQUEST_KEY);
	if (request != null) 
	{
		params.put(ModelBuilder.REQUEST_KEY, request);
	}

}

}
