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

import java.util.Map;

import com.webpagebytes.cms.WPBFilePath;

public interface WPBFileInfo {
	
	public WPBFilePath getCloudFile();
	public long getSize();
	public String getContentType();
	public void setContentType(String contentType);
	public Map<String, String> getCustomProperties();
	public void setCustomProperties(Map<String, String> customProperties);
	public void setProperty(String name, String value);
	public String getProperty(String name);
	public String getMd5();
	public long getCrc32();
	public long getCreationDate();
	
}
