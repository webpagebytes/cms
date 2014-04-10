package com.webpagebytes.cms.exception;

public class WBSerializerException extends WBException {
	public WBSerializerException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBSerializerException(String message)
	{
		super(message);
	}
}