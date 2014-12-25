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

import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.exception.WPBIOException;

/**
 * Cache interface to access CMS site files.
 */
public interface WPBFilesCache extends WPBRefreshableCache {

    /**
     * Gets a WPBFile from cache based on its externalKey
     * @param externalKey externalKey that identifies the record.
     * @return WPBFile instance or null if there is no record with the provided externalKey. 
     * @throws WPBIOException
     */
	public WPBFile getByExternalKey(String externalKey)throws WPBIOException;
	
	/**
	 * Gets the WPBFile from cache that corresponds to a mentioned file path
	 * @param filePath File path where directories and files are separated by / character 
	 * @return WPBFile instance or null if there is no record with the provided file path. 
	 * @throws WPBIOException
	 */
	public WPBFile geByPath(String filePath) throws WPBIOException;
    
    /**
     * Given a WPBFile the method returns the full file path.
     * @param file Instance of WPBFile that can represent a file or a directory.
     * @return Returns the full file path. For the files located in root the method returns their file name.
     * @throws WPBIOException
     */
    public String getFullFilePath(WPBFile file) throws WPBIOException;
	 
}
