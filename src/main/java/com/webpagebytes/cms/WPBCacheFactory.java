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

package com.webpagebytes.cms;

import java.util.Map;

import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Factory interface that provides access to all type of cache instances to be used by the CMS engine.
 * The CMS engine will get multiple times a cache instance and it's up to the implementation to make sure 
 * that Refresh() will have an impact on all instances created. One way to achieve this is to using singleton pattern.
 *
 */
public interface WPBCacheFactory {
	/**
	 * Initializes the cache factory with parameters from the CMS configuration xml file that corresponds to wpbcache section.
	 * @param params Map of keys and values representing configuration parameters
	 * @throws WPBIOException Exception
	 */
	public void initialize(Map<String, String> params) throws WPBIOException;

    /**
     * Access to WPBUrisCache instance
     * @return Returns WPBUrisCache instance
     */
	public WPBUrisCache getUrisCacheInstance();
	
	/**
	 * Access to WPBPagesCache instance
	 * @return Returns WPBPagesCache instance
	 */
	public WPBPagesCache getPagesCacheInstance();
	
	/**
	 * Access to WPBPageModulesCache instance
	 * @return Returns WPBPageModulesCache instance
	 */
	public WPBPageModulesCache getPageModulesCacheInstance();
	
	/**
	 * Access to WPBParametersCache instance
	 * @return Returns WPBParametersCache instance
	 */
	public WPBParametersCache getParametersCacheInstance();
	
	/**
	 * Access to WPBFilesCache instance
	 * @return Returns WPBFilesCache instance
	 */
	public WPBFilesCache getFilesCacheInstance();
	
	/**
	 * Access to WPBArticlesCache instance
	 * @return Returns WPBArticlesCache instance
	 */
	public WPBArticlesCache getArticlesCacheInstance();
	
	/**
	 * Access to WPBMessagesCache instance
	 * @return Returns WPBMessagesCache instance
	 */
	public WPBMessagesCache getMessagesCacheInstance();
	
	/**
	 * Access to WPBProjectCache instance
	 * @return Returns WPBProjectCache instance
	 */
	public WPBProjectCache getProjectCacheInstance();
	
}
