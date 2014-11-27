package com.webpagebytes.cms.cache;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class DefaultWPBCacheFactory {
	
	private DefaultWPBCacheFactory() {};
	private static volatile WPBCacheFactory instance = null;
	private static final Object lock = new Object();
	private static final Logger log = Logger.getLogger(DefaultWPBCacheFactory.class.getName());

	public static WPBCacheFactory getInstance()
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
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_CACHE);
					}
					try
					{
						instance = (WPBCacheFactory) Class.forName(factoryClass).newInstance();
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
