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

import java.util.Locale;

import java.util.Map;
import java.util.Set;
import com.webpagebytes.cms.exception.WPBIOException;

public interface WPBMessagesCache extends WPBRefreshableCache {
	public Map<String, String> getAllMessages(Locale locale) throws WPBIOException;
	public Map<String, String> getAllMessages(String lcid) throws WPBIOException;
	public Set<String> getSupportedLocales();
	public Long getFingerPrint(Locale locale);
}
