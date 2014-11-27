package com.webpagebytes.cms;

class BaseRequestProcessorFactory extends AdminRequestProcessorFactory {

	public ResourceRequestProcessor createResourceRequestProcessor()
	{
		return new ResourceRequestProcessor();
	}
	public AjaxRequestProcessor createAjaxRequestProcessor()
	{
		return new AjaxRequestProcessor();
	}
}
