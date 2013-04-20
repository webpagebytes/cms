package com.webbricks.template;

import java.io.IOException;
import java.util.Map;

import com.webbricks.cms.PageContentBuilder;

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
	TemplateModel urlParams = dataModel.get(PageContentBuilder.URL_PARAMETERS_KEY);
	if (urlParams != null)
	{
		params.put(PageContentBuilder.URL_PARAMETERS_KEY, urlParams);
	}
	
	params.put(PageContentBuilder.FORMAT_TEXT_METHOD, dataModel.get(PageContentBuilder.FORMAT_TEXT_METHOD));
	
}

}
