package com.webbricks.exception;

public class WBException extends Exception {
	public WBException(String message, Throwable e)
	{
		super(message, e);
	}
	public WBException(String message)
	{
		super(message);
	}

}
