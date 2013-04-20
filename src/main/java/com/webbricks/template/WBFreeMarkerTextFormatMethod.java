package com.webbricks.template;

import java.text.MessageFormat;

import freemarker.template.TemplateMethodModel;
import freemarker.template.TemplateModelException;

public class WBFreeMarkerTextFormatMethod implements TemplateMethodModel {

	public Object exec(java.util.List arguments) throws TemplateModelException
    {
		if (arguments.size() == 0) return "";
		
		String pattern = (String) arguments.get(0);
		arguments.remove(0);
		return (new MessageFormat(pattern)).format(arguments.toArray(), new StringBuffer(), null).toString();
    }
}
