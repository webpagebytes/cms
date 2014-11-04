package com.webpagebytes.cms.datautility;

import java.util.logging.Level;

import java.util.logging.Logger;

import com.webpagebytes.cms.datautility.local.WBLocalAdminDataStorage;
import com.webpagebytes.cms.utility.WBConfiguration;
import com.webpagebytes.cms.utility.WBConfigurationFactory;
import com.webpagebytes.cms.utility.WBConfiguration.WPBSECTION;

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
					factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_DATASTORAGE);
				}
				try
				{
					instance = (AdminDataStorage) Class.forName(factoryClass).newInstance();
					return instance;
				} 
				
				catch (Exception e)
				{
					log.log(Level.SEVERE, e.getMessage(), e);
					return null;
				}
			}
		}
		return instance;
	}
}
