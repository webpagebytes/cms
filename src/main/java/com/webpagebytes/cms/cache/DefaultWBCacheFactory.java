package com.webpagebytes.cms.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.datautility.WBCloudFileStorageFactory;
import com.webpagebytes.cms.utility.WBConfiguration;
import com.webpagebytes.cms.utility.WBConfigurationFactory;
import com.webpagebytes.cms.utility.WBConfiguration.WPBSECTION;

public class DefaultWBCacheFactory {
	
	private DefaultWBCacheFactory() {};
	private static volatile WBCacheFactory instance = null;
	private static final Object lock = new Object();
	private static final Logger log = Logger.getLogger(DefaultWBCacheFactory.class.getName());

	public static WBCacheFactory getInstance()
	{
		if (instance == null) 
		{
			synchronized (lock) {
				if (instance == null)
				{
					WBConfiguration config = WBConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_CACHE);
					}
					try
					{
						instance = (WBCacheFactory) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						log.log(Level.SEVERE, "Cannot instantiate WBCacheFactory ", e);
						return null;
					}
				}
			}
		}
		return instance;
	}
}
