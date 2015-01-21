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

import com.webpagebytes.cms.exception.WPBException;

/**
 * WPBContentService is used in batch mode to create models to be used by WPBContentProvider to
 * fetch content from the CMS.
 * 
 */
public interface WPBContentService {
    /**
     * Creates a model for a specific locale mentioned by language and country. If this locale is not supported 
     * the call will throw WPBLocaleException. <br>
     * The caller can pass null for country if the locale requires only language parameter.
     * @param language Locale language
     * @param country Locale country (can be null)
     * @return Return a WPBModel that can be used to fetch content with WPBContentProvider interface.
     * @throws WPBException WPBLocaleException Exception
     */
	public WPBModel createModel(String language, String country) throws WPBException;
	
	/**
	 * Creates a model for the default project locale.
	 * @return Return a WPBModel that can be used to fetch content with WPBContentProvider interface
	 * @throws WPBException Exception
	 */
	public WPBModel createModel() throws WPBException;
	
	/**
	 * Returns an instance of WPBContentProvider that can be used to fetch content from the CMS.
	 * @return Returns an instance of WPBContentProvider that can be used to fetch content from the CMS.
	 * @throws WPBException Exception
	 */
	public WPBContentProvider getContentProvider() throws WPBException;
}
