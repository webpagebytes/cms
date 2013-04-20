package com.webbricks.cms;

public class DefaultRequestProcessorFactory extends RequestProcessorFactory {

	public ResourceRequestProcessor createResourceRequestProcessor()
	{
		return new ResourceRequestProcessor();
	}
	public AjaxRequestProcessor createAjaxRequestProcessor()
	{
		return new AjaxRequestProcessor();
	}
}
