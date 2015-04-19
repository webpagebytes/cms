package com.webpagebytes.cms.utility;

public class WPBStopWatch {

	private Long start = 0L;
	private Long stop = 0L;
	public WPBStopWatch()
	{
		start = System.currentTimeMillis();
	}
	public Long stop()
	{
		stop = System.currentTimeMillis();
		return stop-start;
	}
	public Long elapsedTime()
	{
		return start-stop; 
	}
	public static WPBStopWatch newInstance()
	{
		return new WPBStopWatch();
	}
}
