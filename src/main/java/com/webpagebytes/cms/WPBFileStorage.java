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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * <p>
 * WPBFileStorage offers an abstract interface to store and retrieve files used by the Webpagebytes CMS.
 * </p>
 * <p> Webpagebytes CMS uses two interfaces to store data, WPBAdminDataStorage will store editable text based content together with the 
 * metadata and WPBCloudFileStorage will store the binary files content. <br>
 * </p>
 * An application that uses Webpagebytes CMS will have to use a concrete implementation of WPBFileStorage in the application configuration file.
 * 
 * 
 */
public interface WPBFileStorage {
    /**
     * Stores a file in the storage
     * @param is InputStream with the file content
     * @param file Represents a file location
     * @throws IOException Exception 
     */
	public void storeFile(InputStream is, WPBFilePath file) throws IOException;
	
	/**
	 * Provides access to file metadata
	 * @param file Represents a file location
	 * @return Returns an instance of WPBFileInfo
	 * @throws IOException Exception
	 */
	public WPBFileInfo getFileInfo(WPBFilePath file) throws IOException;
	
	/**
	 * Deletes a file from the storage
	 * @param file Represents a file location
	 * @return true if the file was deleted, false otherwise
	 * @throws IOException Exception 
	 */
	public boolean deleteFile(WPBFilePath file) throws IOException;
	
	/**
	 * Provides access to a stored file content
	 * @param file Represents a file location
	 * @return Returns an InputStream with the file content 
	 * @throws IOException Exception 
	 */
	public InputStream getFileContent(WPBFilePath file) throws IOException;
	
	/**
	 * Method to update a file custom properties
	 * @param file Represents a file location
	 * @param customProps Map with strings key-values that represent custom properties
	 * @throws IOException Exception
	 */
	public void updateFileCustomProperties(WPBFilePath file, Map<String, String> customProps) throws IOException;
	
	/**
	 * Method to update the file content type
	 * @param file Represents a file location
	 * @param contentType Content type value
	 * @throws IOException Exception
	 */
	public void updateContentType(WPBFilePath file, String contentType) throws IOException;	
	
	/**
	 * Each implementation of WPBFileStorage should have a way to publish the file on a public url.
	 * @param file Represents a file location
	 * @return Returns the public url of the file
	 */
	public String getPublicFileUrl(WPBFilePath file);	
}
