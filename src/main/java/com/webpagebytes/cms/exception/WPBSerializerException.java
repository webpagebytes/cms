package com.webpagebytes.cms.exception;

public class WPBSerializerException extends WPBException {
	public WPBSerializerException(String message, Throwable e)
	{
		super(message, e);
	}
	public WPBSerializerException(String message)
	{
		super(message);
	}
}