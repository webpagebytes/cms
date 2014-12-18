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

package com.webpagebytes.cms.template;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import com.webpagebytes.cms.WPBMessagesCache;
import com.webpagebytes.cms.exception.WPBIOException;

public class CmsResourceBundle extends ResourceBundle {

	private WPBMessagesCache messageCache;
	private Long fingerPrint;
	private Map<String, String> messages;
	
	CmsResourceBundle(WPBMessagesCache messageCache, Locale locale)
	{
		this.messageCache = messageCache;
		fingerPrint = 0L;
		messages = new HashMap<String, String>();
		Refresh(locale);
	}
	
	public void Refresh(Locale locale)
	{
		try {
			Long aFingerPrint = messageCache.getFingerPrint(locale); 
			if (aFingerPrint.equals(0L) || !aFingerPrint.equals(fingerPrint))
			{
				messages = messageCache.getAllMessages(locale);
				aFingerPrint = messageCache.getFingerPrint(locale); 
				fingerPrint = aFingerPrint;
			}
		} catch (WPBIOException e)
		{
			fingerPrint = 0L;
		}
	}
	
	@Override
	public Enumeration<String> getKeys() {
		return Collections.enumeration(messages.keySet());
	}

	@Override
	protected Object handleGetObject(String arg0) {
		return messages.get(arg0);
	}
	
	
	public Long getFingerPrint() {
		return fingerPrint;
	}

	public void setFingerPrint(Long fingerPrint) {
		this.fingerPrint = fingerPrint;
	}


}
