package com.webpagebytes.cms.datautility;

import com.webpagebytes.cms.utility.CmsConfiguration;

import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBImageProcessorFactory {

	private static WPBImageProcessor instance;
	private static final Object lock = new Object();

	private WPBImageProcessorFactory() {};
	
	public static WPBImageProcessor getInstance()
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
						factoryClass = config.getSectionClassFactory(WPBSECTION.SECTION_IMAGEPROCESSOR);
					}
					try
					{
						instance = (WPBImageProcessor) Class.forName(factoryClass).newInstance();
						return instance;
					} 
					
					catch (Exception e)
					{
						return null;
					}
				}
			}
		}
		return instance;
	}
	
}
