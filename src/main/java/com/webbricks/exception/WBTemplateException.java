package com.webbricks.exception;

public class WBTemplateException extends WBException {
	public WBTemplateException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBTemplateException(String message)
	{
		super(message);
	}

}
