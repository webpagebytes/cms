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

import java.text.MessageFormat;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class FreeMarkerTextFormatMethod implements TemplateMethodModelEx {

	public Object exec(java.util.List arguments) throws TemplateModelException
    {
		if (arguments.size() == 0) return "";
		
		String pattern = arguments.get(0).toString();
		arguments.remove(0);
		return (new MessageFormat(pattern)).format(arguments.toArray(), new StringBuffer(), null).toString();
    }
}
