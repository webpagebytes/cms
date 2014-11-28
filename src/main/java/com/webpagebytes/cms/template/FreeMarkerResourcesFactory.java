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

import java.util.Locale;

import com.webpagebytes.cms.cache.WPBCacheInstances;
import com.webpagebytes.cms.cache.WPBMessagesCache;

import freemarker.template.Configuration;

class FreeMarkerResourcesFactory {

	public CmsResourceBundle createResourceBundle(WPBMessagesCache messageCache, Locale locale)
	{
		return new CmsResourceBundle(messageCache, locale);
	}
	public Configuration createConfiguration()
	{
		return new Configuration();
	}
	
	public FreeMarkerTemplateLoader createWBFreeMarkerTemplateLoader(WPBCacheInstances cacheInstances)
	{
		return new FreeMarkerTemplateLoader(cacheInstances);
	}
	
	public FreeMarkerModuleDirective createWBFreeMarkerModuleDirective()
	{
		return new FreeMarkerModuleDirective();
	}
	public FreeMarkerImageDirective createWBFreeMarkerImageDirective()
	{
		return new FreeMarkerImageDirective();
	}
	
	public FreeMarkerArticleDirective createWBFreeMarkerArticleDirective()
	{
		return new FreeMarkerArticleDirective();
	}

}
