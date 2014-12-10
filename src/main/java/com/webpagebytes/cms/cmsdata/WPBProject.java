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

package com.webpagebytes.cms.cmsdata;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class WPBProject {
	public static final String PROJECT_KEY = "wbprojectid";
	
	@WPBAdminFieldKey
	private String privkey;

	@WPBAdminFieldStore
	private Date lastModified;
	
	@WPBAdminFieldStore
	private String supportedLanguages;

	@WPBAdminFieldStore
	private String defaultLanguage;
	
	public String getPrivkey() {
		return privkey;
	}

	public void setPrivkey(String key) {
		this.privkey = key;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getSupportedLanguages()
	{
		return supportedLanguages;
	}
	
	public Set<String> getSupportedLanguagesSet() {
		if (supportedLanguages != null)
		{
			String[] langs = supportedLanguages.split(",");
			Set<String> supportedLanguagesSet = new HashSet<String>();
			for(String lang: langs)
			{
				if (lang.length()>0) supportedLanguagesSet.add(lang);
			}
			return supportedLanguagesSet;
		} 
		return null;

	}

	public void setSupportedLanguages(String supportedLanguages) {
		this.supportedLanguages = supportedLanguages;
	}

	public String getDefaultLanguage() {
		return defaultLanguage;
	}

	public void setDefaultLanguage(String defaultLanguage) {
		this.defaultLanguage = defaultLanguage;
	}
	
	
	
}
