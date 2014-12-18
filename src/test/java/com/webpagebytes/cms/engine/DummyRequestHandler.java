package com.webpagebytes.cms.engine;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.WPBContentProvider;
import com.webpagebytes.cms.WPBForward;
import com.webpagebytes.cms.WPBModel;
import com.webpagebytes.cms.WPBRequestHandler;

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
