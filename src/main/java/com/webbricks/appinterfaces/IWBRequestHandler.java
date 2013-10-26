package com.webbricks.appinterfaces;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface IWBRequestHandler {
	public void handleRequest(HttpServletRequest request, 
							  HttpServletResponse response, 
							  Map<String, Object> model,
							  WBForward forward);
}
