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

import com.webpagebytes.cms.datautility.*;

import java.io.Serializable;
import java.util.Date;

public class WPBUri implements Serializable {
	public final static int RESOURCE_TYPE_TEXT = 1;
	public final static int RESOURCE_TYPE_FILE = 2;
	public final static int RESOURCE_TYPE_URL_CONTROLLER = 3;
	

	@WPBAdminFieldKey
	private Long privkey;

	@WPBAdminFieldStore
	private Integer enabled;

	@WPBAdminFieldStore
	private String uri;
	
	@WPBAdminFieldStore
	private Date lastModified;
	
	@WPBAdminFieldStore
	private String httpOperation;
	
	@WPBAdminFieldStore
	private String controllerClass;
	
	@WPBAdminFieldStore
	private Integer resourceType;
	
	@WPBAdminFieldStore
	private String resourceExternalKey;
			
	@WPBAdminFieldStore
	private String externalKey;
	
	public Long getPrivkey() {
		return privkey;
	}

	public void setPrivkey(Long key) {
		this.privkey = key;
	}

	public Integer getEnabled() {
		return enabled;
	}

	public void setEnabled(Integer enabled) {
		this.enabled = enabled;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getHttpOperation() {
		return httpOperation;
	}

	public void setHttpOperation(String httpOperation) {
		this.httpOperation = httpOperation;
	}

	public String getControllerClass() {
		return controllerClass;
	}

	public void setControllerClass(String controllerClass) {
		this.controllerClass = controllerClass;
	}
	
	public String getExternalKey() {
		return externalKey;
	}

	public void setExternalKey(String externalKey) {
		this.externalKey = externalKey;
	}

	public Integer getResourceType() {
		return resourceType;
	}

	public void setResourceType(Integer resourceType) {
		this.resourceType = resourceType;
	}

	public String getResourceExternalKey() {
		return resourceExternalKey;
	}

	public void setResourceExternalKey(String resourceExternalKey) {
		this.resourceExternalKey = resourceExternalKey;
	}
	
	
}
