package com.webpagebytes.cms.datautility;


import java.util.logging.Level;
import java.util.logging.Logger;
import com.webpagebytes.cms.utility.WBConfiguration;
import com.webpagebytes.cms.utility.WBConfigurationFactory;
import com.webpagebytes.cms.utility.WBConfiguration.WPBSECTION;


public class WBCloudFileStorageFactory {
	static WBCloudFileStorage instance = null;
	private WBCloudFileStorageFactory() {}
	private static final Object lock = new Object();
	private static final Logger log = Logger.getLogger(WBCloudFileStorageFactory.class.getName());

	public static WBCloudFileStorage getInstance()
	{
		if (instance == null) {
				synchronized (lock) {
					WBConfiguration config = WBConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_FILESTORAGE);
					}
					try
					{
						instance = (WBCloudFileStorage) Class.forName(factoryClass).newInstance();
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
