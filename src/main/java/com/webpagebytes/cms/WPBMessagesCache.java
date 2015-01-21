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

import java.util.Locale;
import java.util.Map;
import java.util.Set;

import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Cache interface to access CMS site messages.
 *
 */
public interface WPBMessagesCache extends WPBRefreshableCache {
	/**
	 * Gets all messages from cache that belongs to a locale. 
	 * @param locale Locale identifier 
	 * @return Returns a map with all messages that belong to a locale. 
	 * @throws WPBIOException Exception
	 */
    public Map<String, String> getAllMessages(Locale locale) throws WPBIOException;
    
    /**
     * Gets all messages from cache that belongs to a locale specified by a string with format language_country. 
     * For example en_GB or en in case country is not specified. 
     * @param lcid Locale identifier 
     * @return Returns a map with all messages that belong to a locale. 
     * @throws WPBIOException Exception
     */    
	public Map<String, String> getAllMessages(String lcid) throws WPBIOException;
	
	/**
	 * Returns all locales enabled in the project. The values are formated as language_country. 
	 * @return A set of enabled project languages.
	 */
	public Set<String> getSupportedLocales();
	
	/**
	 * Everytime the cache is refreshed it will generate a new fingerprint. A client that had a fingerprint
	 * can check if in the meantime the cache was refreshed.
	 * @param locale Locale identifier
	 * @return The cache current fingerprint.
	 */
	public Long getFingerPrint(Locale locale);
}
