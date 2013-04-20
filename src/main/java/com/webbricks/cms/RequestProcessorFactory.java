package com.webbricks.cms;

public abstract class RequestProcessorFactory {

	public abstract ResourceRequestProcessor createResourceRequestProcessor();
	public abstract AjaxRequestProcessor createAjaxRequestProcessor();
}
