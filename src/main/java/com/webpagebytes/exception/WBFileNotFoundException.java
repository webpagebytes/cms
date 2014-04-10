package com.webpagebytes.exception;

public class WBFileNotFoundException extends WBIOException {
	public WBFileNotFoundException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBFileNotFoundException(String message)
	{
		super(message);
	}
}
