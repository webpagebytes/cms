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

package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.WPBPageModulesCache;
import com.webpagebytes.cms.WPBPagesCache;
import com.webpagebytes.cms.WPBParametersCache;
import com.webpagebytes.cms.WPBProjectCache;
import com.webpagebytes.cms.WPBUrisCache;

public class WPBCacheInstances {
	private WPBUrisCache wpbUriCache;
	private WPBPagesCache wpbPageCache;
	private WPBPageModulesCache wpbPageModuleCache;
	private WPBParametersCache wpbParameterCache;
	private WPBFilesCache wpbFilesCache;
	private WPBArticlesCache wpbArticleCache;
	private WPBMessagesCache wpbMessageCache;
	private WPBProjectCache wpbProjectCache;
	public WPBCacheInstances(WPBUrisCache uriCache,
							WPBPagesCache pageCache,
							WPBPageModulesCache pageModuleCache,
							WPBParametersCache parameterCache,
							WPBFilesCache filesCache,
							WPBArticlesCache articleCache,
							WPBMessagesCache messageCache,
							WPBProjectCache projectCache)
	{
		wpbArticleCache = articleCache;
		wpbUriCache = uriCache;
		wpbPageCache = pageCache;
		wpbPageModuleCache = pageModuleCache;
		wpbParameterCache = parameterCache;
		wpbFilesCache = filesCache;
		wpbMessageCache = messageCache;
		wpbProjectCache = projectCache;
	}
	public WPBCacheInstances(WPBCacheFactory cacheFactory)
	{
		wpbUriCache = cacheFactory.getUrisCacheInstance();
		wpbPageCache = cacheFactory.getWebPagesCacheInstance();
		wpbPageModuleCache = cacheFactory.getPageModulesCacheInstance();
		wpbParameterCache = cacheFactory.getParametersCacheInstance();
		wpbFilesCache = cacheFactory.getFilesCacheInstance();
		wpbArticleCache = cacheFactory.getArticlesCacheInstance();
		wpbMessageCache = cacheFactory.getMessagesCacheInstance();
		wpbProjectCache = cacheFactory.getProjectCacheInstance();
	}
	public WPBMessagesCache getMessageCache()
	{
		return wpbMessageCache;
	}
	public WPBPagesCache getPageCache()
	{
		return wpbPageCache;
	}
	public WPBPageModulesCache getPageModuleCache()
	{
		return wpbPageModuleCache;
	}
	public WPBFilesCache getFilesCache()
	{
		return wpbFilesCache;
	}
	public WPBUrisCache getUriCache()
	{
		return wpbUriCache;
	}
	public WPBArticlesCache getArticleCache()
	{
		return wpbArticleCache;
	}
	public WPBParametersCache getParameterCache()
	{
		return wpbParameterCache;
	}
	public WPBProjectCache getProjectCache()
	{
		return wpbProjectCache;
	}
}
