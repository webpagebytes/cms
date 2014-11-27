package com.webpagebytes.cms.exception;

public class WPBResourceNotFoundException extends WPBException {
	public WPBResourceNotFoundException(String message, Throwable e)
	{
		super(message, e);
	}
	public WPBResourceNotFoundException(String message)
	{
		super(message);
	}

}
