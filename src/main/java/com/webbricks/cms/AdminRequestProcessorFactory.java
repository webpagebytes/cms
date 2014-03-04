package com.webbricks.cms;

public abstract class AdminRequestProcessorFactory {

	public abstract ResourceRequestProcessor createResourceRequestProcessor();
	public abstract AjaxRequestProcessor createAjaxRequestProcessor();
}
