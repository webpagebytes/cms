package com.webpagebytes.cms.exception;

public class WPBException extends Exception {
	public WPBException(String message, Throwable e)
	{
		super(message, e);
	}
	public WPBException(String message)
	{
		super(message);
	}

}
