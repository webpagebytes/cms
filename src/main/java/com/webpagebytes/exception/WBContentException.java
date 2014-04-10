package com.webpagebytes.exception;

public class WBContentException extends WBException {
	public WBContentException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBContentException(String message)
	{
		super(message);
	}
}