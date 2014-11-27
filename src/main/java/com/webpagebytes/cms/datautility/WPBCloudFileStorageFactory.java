package com.webpagebytes.cms.datautility;


import java.util.logging.Level;
import java.util.logging.Logger;
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
