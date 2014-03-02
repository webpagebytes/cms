package com.webbricks.datautility;

import com.webbricks.datautility.local.WBLocalAdminDataStorage;

public class AdminDataStorageFactory {
	private static AdminDataStorage adminDataStorage;
	
	private AdminDataStorageFactory() { };
	
	public static AdminDataStorage getInstance()
	{
		if (adminDataStorage == null)
		{
			adminDataStorage = new WBLocalAdminDataStorage();
			return adminDataStorage;
		}
		return adminDataStorage;
	}
}
