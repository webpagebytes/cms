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

public class WPBParameter {
	public static final int PARAMETER_NO_TYPE = 0;
	public static final int PARAMETER_LOCALE_LANGUAGE = 1;
	public static final int PARAMETER_LOCALE_COUNTRY = 2;
	
	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private String name;
	
	@WPBAdminFieldStore
	private String value;

	@WPBAdminFieldStore
	private Date lastModified;

	@WPBAdminFieldStore
	private String ownerExternalKey;
	
	@WPBAdminFieldStore
	private Integer overwriteFromUrl;
	
	@WPBAdminFieldStore
	private Integer localeType;
	
	@WPBAdminFieldStore
	private String externalKey;
	
	public Long getPrivkey() {
		return privkey;
	}

	public void setPrivkey(Long key) {
		this.privkey = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getOwnerExternalKey() {
		return ownerExternalKey;
	}

	public void setOwnerExternalKey(String keyOwner) {
		this.ownerExternalKey = keyOwner;
	}

	public Integer getOverwriteFromUrl() {
		return overwriteFromUrl;
	}

	public void setOverwriteFromUrl(Integer overwriteFromUrl) {
		this.overwriteFromUrl = overwriteFromUrl;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public Integer getLocaleType() {
		return localeType;
	}

	public void setLocaleType(Integer localeType) {
		this.localeType = localeType;
	}

	
	
}
