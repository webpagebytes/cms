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

public class WPBCacheInstances {
	private WPBUrisCache wbUriCache;
	private WPBWebPagesCache wbWebPageCache;
	private WPBWebPageModulesCache wbWebPageModuleCache;
	private WPBParametersCache wbParameterCache;
	private WPBFilesCache wbFilesCache;
	private WPBArticlesCache wbArticleCache;
	private WPBMessagesCache wbMessageCache;
	private WPBProjectCache wbProjectCache;
	public WPBCacheInstances(WPBUrisCache uriCache,
							WPBWebPagesCache webPageCache,
							WPBWebPageModulesCache webPageModuleCache,
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
		wbUriCache = cacheFactory.createWBUrisCacheInstance();
		wbWebPageCache = cacheFactory.createWBWebPagesCacheInstance();
		wbWebPageModuleCache = cacheFactory.createWBWebPageModulesCacheInstance();
		wbParameterCache = cacheFactory.createWBParametersCacheInstance();
		wbFilesCache = cacheFactory.createWBFilesCacheInstance();
		wbArticleCache = cacheFactory.createWBArticlesCacheInstance();
		wbMessageCache = cacheFactory.createWBMessagesCacheInstance();
		wbProjectCache = cacheFactory.createWBProjectCacheInstance();
	}
	public WPBMessagesCache getWBMessageCache()
	{
		return wbMessageCache;
	}
	public WPBWebPagesCache getWBWebPageCache()
	{
		return wbWebPageCache;
	}
	public WPBWebPageModulesCache getWBWebPageModuleCache()
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
