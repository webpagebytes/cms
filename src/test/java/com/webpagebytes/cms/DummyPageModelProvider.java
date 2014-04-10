package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;

import com.webpagebytes.appinterfaces.IPageModelProvider;
import com.webpagebytes.appinterfaces.WBModel;

public class DummyPageModelProvider implements IPageModelProvider  {

	public void getPageModel(HttpServletRequest request, WBModel model)
	{
		
	}
}
