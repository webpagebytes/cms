package com.webpagebytes.cms.datautility;

import java.util.logging.Level;

import java.util.logging.Logger;

import com.webpagebytes.cms.datautility.local.WPBLocalAdminDataStorage;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBAdminDataStorageFactory {
	private static WPBAdminDataStorage instance;
	private static final Logger log = Logger.getLogger(WPBAdminDataStorageFactory.class.getName());
	private static final Object lock = new Object();
	private WPBAdminDataStorageFactory() { };
	
	public static WPBAdminDataStorage getInstance()
	{
		if (instance == null) {
			synchronized (lock) {
				CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
				String factoryClass = "";
				if (config != null) 
				{
					factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_DATASTORAGE);
				}
				try
				{
					instance = (WPBAdminDataStorage) Class.forName(factoryClass).newInstance();
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
