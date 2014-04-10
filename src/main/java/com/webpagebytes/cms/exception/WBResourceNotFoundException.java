package com.webpagebytes.cms.exception;

public class WBResourceNotFoundException extends WBException {
	public WBResourceNotFoundException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBResourceNotFoundException(String message)
	{
		super(message);
	}

}
