package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WPBContentProvider;
import com.webpagebytes.cms.appinterfaces.WPBRequestHandler;
import com.webpagebytes.cms.appinterfaces.WPBForward;
import com.webpagebytes.cms.appinterfaces.WPBModel;

public class DummyRequestHandler implements WPBRequestHandler {
	public void handleRequest(HttpServletRequest request, 
			  HttpServletResponse response, 
			  WPBModel model,
			  WPBForward forward)
	{
		
	}

	public void initialize(WPBContentProvider contentProvider) {
		// TODO Auto-generated method stub
		
	}
}
