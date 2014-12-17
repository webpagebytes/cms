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

public class WPBPageModule {
	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private String name;

	@WPBAdminFieldStore
	private Date lastModified;

	@WPBAdminFieldTextStore
	private String htmlSource;
	
	@WPBAdminFieldStore
	private Integer isTemplateSource;

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

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getHtmlSource() {
		return htmlSource;
	}

	public void setHtmlSource(String htmlSource) {
		this.htmlSource = htmlSource;
	}

	public Integer getIsTemplateSource() {
		return isTemplateSource;
	}

	public void setIsTemplateSource(Integer isTemplateSource) {
		this.isTemplateSource = isTemplateSource;
	}

	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

}
