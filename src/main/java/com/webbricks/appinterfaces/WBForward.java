package com.webbricks.appinterfaces;

public class WBForward {
	String redirectToExternalKey = null;
	boolean forwardRequest = false;
	
	public void setForwardTo(String externalKey)
	{
		redirectToExternalKey = externalKey;
		forwardRequest = false;
		if (externalKey != null && externalKey.length()>0)
		{
			forwardRequest = true;
		}
	}
	public String getForwardTo()
	{
		return redirectToExternalKey;
	}
	public boolean isRequestForwarded()
	{
		return forwardRequest;
	}
	
}
