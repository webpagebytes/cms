package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.WBContentProvider;
import com.webpagebytes.cms.appinterfaces.WBRequestHandler;
import com.webpagebytes.cms.appinterfaces.WBForward;
import com.webpagebytes.cms.appinterfaces.WBModel;

public class DummyRequestHandler implements WBRequestHandler {
	public void handleRequest(HttpServletRequest request, 
			  HttpServletResponse response, 
			  WBModel model,
			  WBForward forward)
	{
		
	}

	@Override
	public void initialize(WBContentProvider contentProvider) {
		// TODO Auto-generated method stub
		
	}
}
