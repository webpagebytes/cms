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

package com.webpagebytes.cms.datautility;

import java.util.HashMap;
import java.util.Map;

public class WPBDefaultCloudFileInfo implements WPBCloudFileInfo {
	private WPBCloudFile cloudFile;
	private String contentType;
	private Map<String, String> customProperties;
	private long size;
	private String md5;
	private long crc32;
	private long creationDate;
	
	public WPBDefaultCloudFileInfo(WPBCloudFile cloudFile, 
							String contentType, 
							boolean fileExists, 
							long size,
							String md5,
							long crc32,
							long creationDate) {
		this.cloudFile = cloudFile;
		this.contentType = contentType;
		this.size = size;
		this.crc32 = crc32;
		this.creationDate = creationDate;
		this.md5 = md5;
		customProperties = new HashMap<String, String>();
	}
	
	public WPBCloudFile getCloudFile() {
		return cloudFile;
	}

	public long getSize()
	{
		return size;
	}
	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}
	public Map<String, String> getCustomProperties() {
		return customProperties;
	}
	public void setCustomProperties(Map<String, String> customProperties) {
		this.customProperties = customProperties;
	}
	public void setProperty(String name, String value)
	{
		customProperties.put(name, value);
	}
	public String getProperty(String name)
	{
		return customProperties.get(name);
	}

	public String getMd5() {
		return md5;
	}

	public long getCrc32() {
		return crc32;
	}

	public long getCreationDate() {
		return creationDate;
	}
	
}
