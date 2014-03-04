package com.webbricks.datautility;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.webbricks.datautility.local.WBLocalAdminDataStorage;
import com.webbricks.utility.WBConfiguration;
import com.webbricks.utility.WBConfigurationFactory;
import com.webbricks.utility.WBConfiguration.SECTION;

public class AdminDataStorageFactory {
	private static AdminDataStorage instance;
	private static final Logger log = Logger.getLogger(AdminDataStorageFactory.class.getName());
	private static final Object lock = new Object();
	private AdminDataStorageFactory() { };
	
	public static AdminDataStorage getInstance()
	{
		if (instance == null) {
			synchronized (lock) {
				WBConfiguration config = WBConfigurationFactory.getConfiguration();
				String factoryClass = "";
				if (config != null) 
				{
					factoryClass = config.getSectionClassFactory(SECTION.SECTION_DATASTORAGE);
				}
				try
				{
					instance = (AdminDataStorage) Class.forName(factoryClass).newInstance();
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
