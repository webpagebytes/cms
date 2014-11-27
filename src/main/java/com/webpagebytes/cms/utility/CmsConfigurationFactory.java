package com.webpagebytes.cms.utility;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;


public class CmsConfigurationFactory {

	private static String configPath; 
	private static CmsConfiguration configuration;
	private static final Object lock = new Object();
	private static Logger log = Logger.getLogger(CmsConfigurationFactory.class.getName());
	
	private CmsConfigurationFactory() {};
	public static String getConfigPath()
	{
		return configPath;
	}
	public static void setConfigPath(String path)
	{
		configPath = path;
	}
	
	public static CmsConfiguration getConfiguration()
	{
		if (configuration == null)
		{
			InputStream is = null;
			synchronized (lock) {
				try
				{
					try
					{
						is = new FileInputStream(configPath);
					} catch (FileNotFoundException e)
					{
						is = CmsConfigurationFactory.class.getClassLoader().getResourceAsStream(configPath);
					}
					XMLConfigReader reader = new XMLConfigReader();
					configuration = reader.readConfiguration(is);
				} catch (Exception e)
				{
					log.log(Level.SEVERE, e.getMessage(), e);
					return null;
				}
				finally 
				{
					IOUtils.closeQuietly(is);
				}
			}
		}
		return configuration;
	}
}
