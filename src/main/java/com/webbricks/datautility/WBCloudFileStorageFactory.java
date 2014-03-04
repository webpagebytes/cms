package com.webbricks.datautility;


import com.webbricks.datautility.local.WBLocalCloudFileStorage;
import com.webbricks.utility.WBConfiguration;
import com.webbricks.utility.WBConfiguration.SECTION;
import com.webbricks.utility.WBConfigurationFactory;


public class WBCloudFileStorageFactory {
	static WBCloudFileStorage instance = null;
	private WBCloudFileStorageFactory() {}
	private static final Object lock = new Object();
	
	public static synchronized WBCloudFileStorage getInstance()
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
