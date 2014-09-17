package com.webpagebytes.cms.utility;

import java.io.InputStream;

public class WBConfigurationFactory {

	private static WBConfigurationFactory instance;
	private static WBConfiguration configuration;
	private static String CONFIG_FILE_XML = "wbconfiguration.xml";
	private static final Object lock = new Object();
	
	public static WBConfiguration getConfiguration()
	{
		if (configuration == null && instance == null)
		{
			synchronized (lock) {
				instance = new WBConfigurationFactory();
				instance.instance = instance;
				try
				{
					InputStream is = WBConfigurationFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE_XML);
					XMLConfigReader reader = new XMLConfigReader();
					instance.configuration = reader.readConfiguration(is);
				} catch (Exception e)
				{
					return null;
				}
			}
		}
		return instance.configuration;
	}
}
