package com.webpagebytes.cms;

abstract class AdminRequestProcessorFactory {

	public abstract ResourceRequestProcessor createResourceRequestProcessor();
	public abstract AjaxRequestProcessor createAjaxRequestProcessor();
}
