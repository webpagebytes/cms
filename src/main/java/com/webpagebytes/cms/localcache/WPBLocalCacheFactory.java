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

package com.webpagebytes.cms.localcache;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;

public class WPBLocalCacheFactory implements WPBCacheFactory {
	
	private static WPBUrisCache uriCacheInstance;
	private static WPBPagesCache pageCacheInstance;
	private static WPBParametersCache parametersCacheInstance;
	private static WPBPageModulesCache pageModulesCacheInstance;
	private static WPBFilesCache filesCacheInstance;
	private static WPBArticlesCache articlesCacheInstance;
	private static WPBMessagesCache messagesCacheInstance;
	private static WPBProjectCache projectCacheInstance;
	
	public WPBUrisCache getUrisCacheInstance()
	{
		if (null == uriCacheInstance)
		{
			uriCacheInstance = new WPBLocalUrisCache();
		}
		return uriCacheInstance;
	}
	public WPBPagesCache getWebPagesCacheInstance()
	{
		if (null == pageCacheInstance)
		{
			pageCacheInstance = new WPBLocalWebPagesCache();
		}
		return pageCacheInstance;
	}
	public WPBParametersCache getParametersCacheInstance()
	{
		if (parametersCacheInstance == null)
		{
			parametersCacheInstance = new WPBLocalParametersCache();
		}
		return parametersCacheInstance;
	}
	
	public WPBPageModulesCache getPageModulesCacheInstance()
	{
		if (pageModulesCacheInstance == null)
		{
			pageModulesCacheInstance = new WPBLocalWebPageModulesCache();
		}
		return pageModulesCacheInstance;
	}
	public WPBFilesCache getFilesCacheInstance()
	{
		if (filesCacheInstance == null)
		{
			filesCacheInstance = new WPBLocalFilesCache();
		}
		return filesCacheInstance;
	}
	public WPBArticlesCache getArticlesCacheInstance()
	{
		if (articlesCacheInstance == null)
		{
			articlesCacheInstance = new WPBLocalArticlesCache();
		}
		return articlesCacheInstance;
	}
	public WPBMessagesCache getMessagesCacheInstance()
	{
		if (messagesCacheInstance == null)
		{
			messagesCacheInstance = new WPBLocalMessagesCache(); 
		}
		return messagesCacheInstance;
	}
	public WPBProjectCache getProjectCacheInstance()
	{
		if (projectCacheInstance == null)
		{
			projectCacheInstance = new WPBLocalProjectCache();
		}
		return projectCacheInstance;
	}
}