package com.webpagebytes.datautility;


import com.webpagebytes.datautility.local.WBLocalCloudFileStorage;
import com.webpagebytes.utility.WBConfiguration;
import com.webpagebytes.utility.WBConfigurationFactory;
import com.webpagebytes.utility.WBConfiguration.SECTION;


public class WBCloudFileStorageFactory {
	static WBCloudFileStorage instance = null;
	private WBCloudFileStorageFactory() {}
	private static final Object lock = new Object();
	
	public static WBCloudFileStorage getInstance()
	{
		if (instance == null) {
				synchronized (lock) {
					WBConfiguration config = WBConfigurationFactory.getConfiguration();
					String factoryClass = "";
					if (config!=null)
					{
						factoryClass = config.getSectionClassFactory(SECTION.SECTION_FILESTORAGE);
					}
					try
					{
						instance = (WBCloudFileStorage) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						return null;
					}
				}
			}
		return instance;
	}
}
