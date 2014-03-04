package com.webbricks.utility;

import java.io.InputStream;

public class WBConfigurationFactory {

	private static WBConfiguration configuration;
	private static String CONFIG_FILE_XML = "wbconfiguration.xml";
	private static final Object lock = new Object();
	
	public static WBConfiguration getConfiguration()
	{
		if (configuration == null)
		{
			synchronized (lock) {
				try
				{
					InputStream is = WBConfigurationFactory.class.getClassLoader().getResourceAsStream(CONFIG_FILE_XML);
					XMLConfigReader reader = new XMLConfigReader();
					configuration = reader.readConfiguration(is);
				} catch (Exception e)
				{
					return null;
				}
			}
		}
		return configuration;
	}
}
