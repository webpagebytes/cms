/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

class URLDataStructure {

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
