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

import com.webpagebytes.cms.engine.DefaultContentService;

/**
 * <p>
 * Factory class to create WPBContentService instances.
 * </p>
 * <p> 
 * This can be used in batch mode. <br>
 * WPBContentServiceFactory is implemented as a singleton.
 * </p>
 */
public class WPBContentServiceFactory {
    /**
     * Static instance of WPBContentService to support singleton pattern.
     */
	private static WPBContentService instance;
	
	/**
	 * Method to get instance of WPBContentService implemented as singleton.
	 * @return Returns singleton instance of WPBContentService.
	 */
	public static WPBContentService getInstance()
	{
		if (instance == null)
		{
			instance = new DefaultContentService();
		}
		return instance;
	}
}
