package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.cms.appinterfaces.IPageModelProvider;
import com.webpagebytes.cms.appinterfaces.WBModel;

public class DummyPageModelProvider implements IPageModelProvider  {

	public void getPageModel(HttpServletRequest request, WBModel model)
	{
		
	}
}
