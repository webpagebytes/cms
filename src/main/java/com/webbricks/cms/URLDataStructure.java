package com.webbricks.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class URLDataStructure {
private int deep;
private ArrayList<String> subUrls;
private boolean hasParams;
private Map<Integer, String> clearSubUrl; // subUrls that have no params
private Map<Integer, String> dirtySubUrl; // subUrls with params


public URLDataStructure(String url)
{
	hasParams = false;
	if (url == null) return;
	hasParams = url.indexOf('{') >= 0;
	StringTokenizer tokenizer = new StringTokenizer(url, "/", true);
	subUrls = new ArrayList<String>();
	clearSubUrl = new HashMap<Integer, String>();
	dirtySubUrl = new HashMap<Integer, String>();
	
	String lastToken = "url"; 
	int position = 0;
	while (tokenizer.hasMoreElements())
	{
		lastToken = tokenizer.nextToken();
		if (! lastToken.equals("/"))
		{
			subUrls.add(lastToken);
			if (lastToken.indexOf('{') >=0)
			{
				dirtySubUrl.put(position, lastToken);
			} else
			{
				clearSubUrl.put(position, lastToken);
			}
			position +=1;
		}		
	}
	if (lastToken.equals("/"))
	{
		subUrls.add("");
		clearSubUrl.put(position, "");
	}
	this.deep = subUrls.size();
}

public boolean hasParams()
{
	return hasParams;
}

public int getDeep() {
	return deep;
}

public ArrayList<String> getSubUrls() {
	return subUrls;
}

public Map<Integer, String> getClearSubUrl() {
	return clearSubUrl;
}

public Map<Integer, String> getDirtySubUrl() {
	return dirtySubUrl;
}


}
