/*
 *   Copyright 2015 Webpagebytes
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

import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.WPBAuthentication;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBAuthenticationFactory {
	private static WPBAuthentication instance;
	private static final Logger log = Logger.getLogger(WPBAuthenticationFactory.class.getName());
	private static final Object lock = new Object();
	private WPBAuthenticationFactory() { };
	
	public static WPBAuthentication getInstance()
	{
		if (instance == null) {
			synchronized (lock) {
				CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
				String factoryClass = "";
				if (config != null) 
				{
					factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_AUTHENTICATION);
				}
				if (factoryClass != null && factoryClass.length() > 0)
				{
					try
					{
						
						WPBAuthentication tempInstance = (WPBAuthentication) Class.forName(factoryClass).newInstance();
						tempInstance.initialize(config.getSectionParams(WPBSECTION.SECTION_AUTHENTICATION));
						instance = tempInstance;
						return instance;
					} 			
					catch (Exception e)
					{
						// if there is an exception on authentication then we stop the server
						
						log.log(Level.SEVERE, e.getMessage(), e);
						
						log.log(Level.SEVERE, "Shutting down the process because authentication has exceptions");
						System.exit(1);
						return null;
					}
				}
			}
		}
		return instance;
	}

}
