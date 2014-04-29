package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.WBPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;

public class DummyPageModelProvider implements WBPageModelProvider  {

	public void populatePageModel(HttpServletRequest request, WBModel model)
	{
		
	}
	public void populatePageModel(WBModel model)
	{
		
	}
}
