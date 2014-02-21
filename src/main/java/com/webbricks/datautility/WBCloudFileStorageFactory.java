package com.webbricks.datautility;

import java.net.URL;

import com.webbricks.datautility.local.WBLocalCloudFileStorage;


public class WBCloudFileStorageFactory {
	static WBCloudFileStorage instance = null;
	private WBCloudFileStorageFactory() {}
	
	public static synchronized WBCloudFileStorage getInstance()
	{
		if (instance == null) {
				String path = "d:\\Tree"; 
				WBLocalCloudFileStorage _instance = null;
				try {
					_instance = new WBLocalCloudFileStorage(path, "");
					_instance.initialize();
				} catch (Exception e)
				{
					e.printStackTrace();
				};
				
				return _instance;
			}			
		else
		{
			return instance;
		}
		
	}
}
