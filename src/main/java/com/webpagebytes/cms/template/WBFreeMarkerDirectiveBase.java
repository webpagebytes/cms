package com.webpagebytes.cms.template;

import java.io.IOException;



import java.util.Map;

import com.webpagebytes.cms.appinterfaces.WBModel;

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

	TemplateModel localeCountry = dataModel.get(WBModel.LOCALE_COUNTRY_KEY);
	TemplateModel localeLanguage = dataModel.get(WBModel.LOCALE_LANGUAGE_KEY);
	params.put(WBModel.LOCALE_LANGUAGE_KEY, localeLanguage);
	params.put(WBModel.LOCALE_COUNTRY_KEY, localeCountry);
	
	TemplateModel resourceBundle = dataModel.get(WBModel.LOCALE_MESSAGES);
	if (resourceBundle != null)
	{
		params.put(WBModel.LOCALE_MESSAGES, resourceBundle);
	}
	TemplateModel pageParams = dataModel.get(WBModel.PAGE_PARAMETERS_KEY);
	if (pageParams != null)
	{
		params.put(WBModel.PAGE_PARAMETERS_KEY, pageParams);
	}
	TemplateModel uriParams = dataModel.get(WBModel.URI_PARAMETERS_KEY);
	if (uriParams != null)
	{
		params.put(WBModel.URI_PARAMETERS_KEY, uriParams);
	}
	
	params.put(WBModel.FORMAT_TEXT_METHOD, dataModel.get(WBModel.FORMAT_TEXT_METHOD));
	
	TemplateModel globals = dataModel.get(WBModel.GLOBALS_KEY);
	if (globals != null) 
	{
		params.put(WBModel.GLOBALS_KEY, globals);
	}
	TemplateModel locale = dataModel.get(WBModel.LOCALE_KEY);
	if (locale != null) 
	{
		params.put(WBModel.LOCALE_KEY, locale);
	}

	TemplateModel request = dataModel.get(WBModel.REQUEST_KEY);
	if (request != null) 
	{
		params.put(WBModel.REQUEST_KEY, request);
	}

	TemplateModel appModel = dataModel.get(WBModel.APPLICATION_CONTROLLER_MODEL_KEY);
	if (appModel != null) 
	{
		params.put(WBModel.APPLICATION_CONTROLLER_MODEL_KEY, appModel);
	}

}

}
