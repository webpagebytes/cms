package com.webpagebytes.cms.exception;

public class WPBContentException extends WPBException {
	public WPBContentException(String message, Throwable e)
	{
		super(message, e);
	}
	public WPBContentException(String message)
	{
		super(message);
	}
}