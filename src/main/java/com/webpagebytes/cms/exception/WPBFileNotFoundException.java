package com.webpagebytes.cms.exception;

public class WPBFileNotFoundException extends WPBIOException {
	public WPBFileNotFoundException(String message, Throwable e)
	{
		super(message, e);
	}
	public WPBFileNotFoundException(String message)
	{
		super(message);
	}
}
