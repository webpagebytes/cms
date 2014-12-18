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

import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Cache interface to access CMS articles.
 */
public interface WPBArticlesCache extends WPBRefreshableCache {
    /**
     * Gets a WPBArticle from cache based on its externalKey
     * @param externalKey externalKey that identifies the record.
     * @return WPBArticle instance or null if there is no article with the provided externalKey. 
     * @throws WPBIOException
     */
	public WPBArticle getByExternalKey(String externalKey) throws WPBIOException;
	
}
