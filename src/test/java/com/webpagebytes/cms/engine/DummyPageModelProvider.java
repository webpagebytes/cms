package com.webpagebytes.cms.engine;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.WPBPageModelProvider;

public class DummyPageModelProvider implements WPBPageModelProvider  {

	public void populatePageModel(HttpServletRequest request, WPBModel model)
	{
		
	}
	public void populatePageModel(WPBModel model)
	{
		
	}
}
