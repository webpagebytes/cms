package com.webbricks.cms;

import java.util.Map;

public class URLMatcherResult {
private String urlRequest;

private String urlPattern;
private Map<String, String> patternParams;
public String getUrlPattern() {
	return urlPattern;
}
public void setUrlPattern(String urlPattern) {
	this.urlPattern = urlPattern;
}
public Map<String, String> getPatternParams() {
	return patternParams;
}
public void setPatternParams(Map<String, String> patternParams) {
	this.patternParams = patternParams;
}
public String getUrlRequest() {
	return urlRequest;
}
public void setUrlRequest(String urlRequest) {
	this.urlRequest = urlRequest;
}


}
