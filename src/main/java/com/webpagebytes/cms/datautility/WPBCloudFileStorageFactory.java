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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.appinterfaces.WPBCloudFileStorage;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBCloudFileStorageFactory {
	static WPBCloudFileStorage instance = null;
	private WPBCloudFileStorageFactory() {}
	private static final Object lock = new Object();
	private static final Logger log = Logger.getLogger(WPBCloudFileStorageFactory.class.getName());

	public static WPBCloudFileStorage getInstance()
	{
		if (instance == null) {
				synchronized (lock) {
					CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_FILESTORAGE);
					}
					try
					{
						instance = (WPBCloudFileStorage) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						log.log(Level.SEVERE, "Cannot instantiate WBCloudFileStorage ", e);
						return null;
					}
				}
			}
		return instance;
	}
}
