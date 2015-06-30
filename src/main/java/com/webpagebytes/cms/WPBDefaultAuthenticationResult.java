package com.webpagebytes.cms;

public class WPBDefaultAuthenticationResult implements WPBAuthenticationResult {

	private String userIdentifier;
	private String logoutLink;
	private String loginLink;
	private String userProfileLink;
	
	public String getUserIdentifier() {
		return userIdentifier;
	}
	public String getLogoutLink() {
		return logoutLink;
	}
	public String getLoginLink() {
		return loginLink;
	}
	public String getUserProfileLink() {
		return userProfileLink;
	}
	public void setUserIdentifier(String userIdentifier) {
		this.userIdentifier = userIdentifier;
	}
	public void setLogoutLink(String logoutLink) {
		this.logoutLink = logoutLink;
	}
	public void setLoginLink(String loginLink) {
		this.loginLink = loginLink;
	}
	public void setUserProfileLink(String userProfileLink) {
		this.userProfileLink = userProfileLink;
	}

	
}
