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

import java.util.Map;

/**
 * Class that represents file information for a Webpagebytes CMS file. 
 *
 */
public interface WPBFileInfo {
	
    /**
     * Returns the file path
     * @return Returns the file path
     */
	public WPBFilePath getFilePath();
	
	/**
	 * Returns the size of the file in bytes
	 * @return Returns the size of the file in bytes
	 */
	public long getSize();
	
	/**
	 * Returns the file content type
	 * @return Returns the file content type
	 */
	public String getContentType();
	
	/**
	 * Sets the file content type
	 * @param contentType New file content type
	 */
	public void setContentType(String contentType);
	
	/**
	 * Retruns the file custom properties
	 * @return Retruns the file custom properties as a map of String of String
	 */
	public Map<String, String> getCustomProperties();
	
	/**
	 * Sets the file custom properties
	 * @param customProperties Custom properties for the file
	 */
	public void setCustomProperties(Map<String, String> customProperties);
	
	/**
	 * Sets a file property value
	 * @param name Name of property
	 * @param value Value of property
	 */
	public void setProperty(String name, String value);
	
	/**
	 * Gets the property value for the property name provided
	 * @param name Property name
	 * @return Gets the property value for the property name provided
	 */
	public String getProperty(String name);
	
	/**
	 * Retuns MD5 hash for the file content.
	 * @return Retuns MD5 hash for the file content.
	 */
	public String getMd5();
	
	/**
	 * Returns a crc32 hash for the file content.
	 * @return Returns a crc32 hash for the file content.
	 */
	public long getCrc32();
	
	/**
	 * Returns the timestamp of the creation time for the file.
	 * @return Returns the timestamp of the creation time for the file.
	 */
	public long getCreationDate();
	
}
