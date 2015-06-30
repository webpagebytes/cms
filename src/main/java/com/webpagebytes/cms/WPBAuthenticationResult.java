package com.webpagebytes.cms;

public interface WPBAuthenticationResult {
		
	public String getUserIdentifier();
	public String getLogoutLink();
	public String getLoginLink();
	public String getUserProfileLink();
	
}
