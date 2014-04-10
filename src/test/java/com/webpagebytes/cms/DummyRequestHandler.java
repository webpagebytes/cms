package com.webpagebytes.cms;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.appinterfaces.IWBRequestHandler;
import com.webpagebytes.cms.appinterfaces.WBForward;
import com.webpagebytes.cms.appinterfaces.WBModel;

public class DummyRequestHandler implements IWBRequestHandler {
	public void handleRequest(HttpServletRequest request, 
			  HttpServletResponse response, 
			  WBModel model,
			  WBForward forward)
	{
		
	}

}
