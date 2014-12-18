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

import java.util.Set;

import com.webpagebytes.cms.cmsdata.WPBProject;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.Pair;

/**
 * Cache interface to access CMS project related data.
 *
 */
public interface WPBProjectCache extends WPBRefreshableCache {
    
    /**
     * Returns the project default language in the format language_Country or just language if the Country not set. 
     * @return Returns the project default language
     * @throws WPBIOException
     */
	public String getDefaultLanguage() throws WPBIOException;
	
	/**
	 * Returns the default locale as a Pair of String 
	 * @return Returns the default locale as a Pair of String
	 * @throws WPBIOException
	 */
	public Pair<String, String> getDefaultLocale() throws WPBIOException;
	
	/**
	 * Returns the locales enabled in the project, the values are represented as language_Country 
	 * @return Returns the locales enabled in the project, the values are represented as language_Country
	 * @throws WPBIOException
	 */
	public Set<String> getSupportedLocales() throws WPBIOException;	
	
	/**
	 * Returns the WPBProject instance
	 * @return Returns the WPBProject instance
	 * @throws WPBIOException
	 */
	public WPBProject getProject() throws WPBIOException;
}
