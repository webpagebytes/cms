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

package com.webpagebytes.cms.engine;

import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBImageProcessorFactory {

	private static DefaultImageProcessor instance;
	private static final Object lock = new Object();

	private WPBImageProcessorFactory() {};
	
	public static DefaultImageProcessor getInstance()
	{
		if (instance == null) 
		{
			synchronized (lock) {
				if (instance == null)
				{
					CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_IMAGEPROCESSOR);
					}
					try
					{
						instance = (DefaultImageProcessor) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						return null;
					}
				}
			}
		}
		return instance;
	}
	
}
