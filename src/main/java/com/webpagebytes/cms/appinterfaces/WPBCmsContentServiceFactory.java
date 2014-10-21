package com.webpagebytes.cms.appinterfaces;

import com.webpagebytes.cms.WPBCmsContentService;

public class WPBCmsContentServiceFactory {
	private static WPBContentService instance;
	public static WPBContentService getInstance()
	{
		if (instance == null)
		{
			instance = new WPBCmsContentService();
		}
		return instance;
	}
}
