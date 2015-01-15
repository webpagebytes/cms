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

package com.webpagebytes.cms.template;

import java.io.IOException;



import java.util.Map;

import com.webpagebytes.cms.WPBModel;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

class FreeMarkerDirectiveBase implements TemplateDirectiveModel {

public void execute(Environment env,
            Map params, TemplateModel[] loopVars,
            TemplateDirectiveBody body)
            throws TemplateException, IOException 
{
}

public void copyParams(Environment env, Map params) throws TemplateModelException
{
	TemplateHashModel dataModel = env.getDataModel();

	TemplateModel localeCountry = dataModel.get(WPBModel.LOCALE_COUNTRY_KEY);
	TemplateModel localeLanguage = dataModel.get(WPBModel.LOCALE_LANGUAGE_KEY);
	params.put(WPBModel.LOCALE_LANGUAGE_KEY, localeLanguage);
	params.put(WPBModel.LOCALE_COUNTRY_KEY, localeCountry);
	
	TemplateModel resourceBundle = dataModel.get(WPBModel.LOCALE_MESSAGES);
	if (resourceBundle != null)
	{
		params.put(WPBModel.LOCALE_MESSAGES, resourceBundle);
	}
	TemplateModel pageParams = dataModel.get(WPBModel.PAGE_PARAMETERS_KEY);
	if (pageParams != null)
	{
		params.put(WPBModel.PAGE_PARAMETERS_KEY, pageParams);
	}
	TemplateModel uriParams = dataModel.get(WPBModel.URI_PARAMETERS_KEY);
	if (uriParams != null)
	{
		params.put(WPBModel.URI_PARAMETERS_KEY, uriParams);
	}
	
	params.put(WPBModel.FORMAT_TEXT_METHOD, dataModel.get(WPBModel.FORMAT_TEXT_METHOD));
	
	TemplateModel globals = dataModel.get(WPBModel.GLOBALS_KEY);
	if (globals != null) 
	{
		params.put(WPBModel.GLOBALS_KEY, globals);
	}
	TemplateModel locale = dataModel.get(WPBModel.LOCALE_KEY);
	if (locale != null) 
	{
		params.put(WPBModel.LOCALE_KEY, locale);
	}

	TemplateModel request = dataModel.get(WPBModel.REQUEST_KEY);
	if (request != null) 
	{
		params.put(WPBModel.REQUEST_KEY, request);
	}

	TemplateModel appModel = dataModel.get(WPBModel.APPLICATION_CONTROLLER_MODEL_KEY);
	if (appModel != null) 
	{
		params.put(WPBModel.APPLICATION_CONTROLLER_MODEL_KEY, appModel);
	}
	
}

}
