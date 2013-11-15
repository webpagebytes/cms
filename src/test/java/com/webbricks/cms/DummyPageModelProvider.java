package com.webbricks.cms;

import javax.servlet.http.HttpServletRequest;

import com.webbricks.appinterfaces.IPageModelProvider;
import com.webbricks.appinterfaces.WBModel;

public class DummyPageModelProvider implements IPageModelProvider  {

	public void getPageModel(HttpServletRequest request, WBModel model)
	{
		
	}
}
