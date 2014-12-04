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

package com.webpagebytes.cms.cache;

import com.webpagebytes.cms.appinterfaces.WPBArticlesCache;
import com.webpagebytes.cms.appinterfaces.WPBFilesCache;
import com.webpagebytes.cms.appinterfaces.WPBMessagesCache;
import com.webpagebytes.cms.appinterfaces.WPBParametersCache;
import com.webpagebytes.cms.appinterfaces.WPBProjectCache;
import com.webpagebytes.cms.appinterfaces.WPBUrisCache;
import com.webpagebytes.cms.appinterfaces.WPBPageModulesCache;
import com.webpagebytes.cms.appinterfaces.WPBWebPagesCache;

public class WPBCacheInstances {
	private WPBUrisCache wbUriCache;
	private WPBWebPagesCache wbWebPageCache;
	private WPBPageModulesCache wbWebPageModuleCache;
	private WPBParametersCache wbParameterCache;
	private WPBFilesCache wbFilesCache;
	private WPBArticlesCache wbArticleCache;
	private WPBMessagesCache wbMessageCache;
	private WPBProjectCache wbProjectCache;
	public WPBCacheInstances(WPBUrisCache uriCache,
							WPBWebPagesCache webPageCache,
							WPBPageModulesCache webPageModuleCache,
							WPBParametersCache parameterCache,
							WPBFilesCache filesCache,
							WPBArticlesCache articleCache,
							WPBMessagesCache messageCache,
							WPBProjectCache projectCache)
	{
		wbArticleCache = articleCache;
		wbUriCache = uriCache;
		wbWebPageCache = webPageCache;
		wbWebPageModuleCache = webPageModuleCache;
		wbParameterCache = parameterCache;
		wbFilesCache = filesCache;
		wbMessageCache = messageCache;
		wbProjectCache = projectCache;
	}
	public WPBCacheInstances(WPBCacheFactory cacheFactory)
	{
		wbUriCache = cacheFactory.getUrisCacheInstance();
		wbWebPageCache = cacheFactory.getWebPagesCacheInstance();
		wbWebPageModuleCache = cacheFactory.getPageModulesCacheInstance();
		wbParameterCache = cacheFactory.getParametersCacheInstance();
		wbFilesCache = cacheFactory.getFilesCacheInstance();
		wbArticleCache = cacheFactory.getArticlesCacheInstance();
		wbMessageCache = cacheFactory.getMessagesCacheInstance();
		wbProjectCache = cacheFactory.getProjectCacheInstance();
	}
	public WPBMessagesCache getWBMessageCache()
	{
		return wbMessageCache;
	}
	public WPBWebPagesCache getWBWebPageCache()
	{
		return wbWebPageCache;
	}
	public WPBPageModulesCache getWBWebPageModuleCache()
	{
		return wbWebPageModuleCache;
	}
	public WPBFilesCache getWBFilesCache()
	{
		return wbFilesCache;
	}
	public WPBUrisCache getWBUriCache()
	{
		return wbUriCache;
	}
	public WPBArticlesCache getWBArticleCache()
	{
		return wbArticleCache;
	}
	public WPBParametersCache getWBParameterCache()
	{
		return wbParameterCache;
	}
	public WPBProjectCache getProjectCache()
	{
		return wbProjectCache;
	}
}
