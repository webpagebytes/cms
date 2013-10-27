package com.webbricks.template;

import java.io.IOException;
import java.util.Map;

import com.webbricks.cms.BaseModelProvider;
import com.webbricks.cms.PageContentBuilder;
import com.webbricks.cmsdata.WBPredefinedParameters;

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

	TemplateModel localeCountry = dataModel.get(BaseModelProvider.LOCALE_COUNTRY_KEY);
	TemplateModel localeLanguage = dataModel.get(BaseModelProvider.LOCALE_LANGUAGE_KEY);
	params.put(BaseModelProvider.LOCALE_LANGUAGE_KEY, localeLanguage);
	params.put(BaseModelProvider.LOCALE_COUNTRY_KEY, localeCountry);
	
	TemplateModel resourceBundle = dataModel.get(PageContentBuilder.LOCALE_MESSAGES);
	if (resourceBundle != null)
	{
		params.put(PageContentBuilder.LOCALE_MESSAGES, resourceBundle);
	}
	TemplateModel pageParams = dataModel.get(PageContentBuilder.PAGE_PARAMETERS_KEY);
	if (pageParams != null)
	{
		params.put(PageContentBuilder.PAGE_PARAMETERS_KEY, pageParams);
	}
	TemplateModel uriParams = dataModel.get(BaseModelProvider.URI_PARAMETERS_KEY);
	if (uriParams != null)
	{
		params.put(BaseModelProvider.URI_PARAMETERS_KEY, uriParams);
	}

	TemplateModel urlParams = dataModel.get(PageContentBuilder.URL_REQUEST_PARAMETERS_KEY);
	if (urlParams != null)
	{
		params.put(PageContentBuilder.URL_REQUEST_PARAMETERS_KEY, urlParams);
	}
	
	params.put(PageContentBuilder.FORMAT_TEXT_METHOD, dataModel.get(PageContentBuilder.FORMAT_TEXT_METHOD));
	
	TemplateModel globals = dataModel.get(BaseModelProvider.GLOBALS_KEY);
	if (globals != null) 
	{
		params.put(BaseModelProvider.GLOBALS_KEY, globals);
	}
	TemplateModel locale = dataModel.get(BaseModelProvider.LOCALE_KEY);
	if (locale != null) 
	{
		params.put(BaseModelProvider.LOCALE_KEY, locale);
	}
	TemplateModel protocols = dataModel.get(BaseModelProvider.GLOBAL_PROTOCOL);
	if (protocols != null) 
	{
		params.put(BaseModelProvider.GLOBAL_PROTOCOL, protocols);
	}

}

}
