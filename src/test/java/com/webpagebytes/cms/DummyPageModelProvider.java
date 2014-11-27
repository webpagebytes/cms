package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.WPBPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WPBModel;

public class DummyPageModelProvider implements WPBPageModelProvider  {

	public void populatePageModel(HttpServletRequest request, WPBModel model)
	{
		
	}
	public void populatePageModel(WPBModel model)
	{
		
	}
}
