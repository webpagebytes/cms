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

package com.webpagebytes.cms.engine;

import java.util.ArrayList;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class URLMatcher {

private Long fingerPrint;
private Set<String> patterns;
private Set<String> simplePatterns; // patterns with no parameters
private Map<String, URLDataStructure> patternsWithParams; // the patterns with parameters
private Map<Integer, Set<String>> deepToPatternUrls; // fast lookup to get all patterns with a certain deep value
private Map<String, URLDataStructure> patternsWithAllMatch; // the patterns with all match params (these are patterns that ends with {**}

public URLMatcher()
{
	
}
public void initialize(Set<String> patterns, Long fingerPrint)
{
	Set<String> newPatters = new HashSet<String>(patterns);
	
	// newSimplePatterns are all the patterns that do not contain any parameter
	HashSet<String> newSimplePatterns = new HashSet<String>();
	
	// newPatternsWithParams is a map between patterns with params and their URLDataStructure
	HashMap<String, URLDataStructure> newPatternsWithParams = new HashMap<String, URLDataStructure>();
	
	// newDeepToPatternUrls is a map between deep url levels and their patterns ( /home/test has deed 2, /home has deep 1) 
	HashMap<Integer, Set<String>> newDeepToPatternUrls = new HashMap<Integer, Set<String>>();
	// newPatternsAllMatch are patterns that ends with /{**}
	HashMap<String, URLDataStructure> newPatternsAllMatch = new HashMap<String, URLDataStructure>();
	
	for (String pattern : patterns)
	{
		URLDataStructure aUrlStructure = new URLDataStructure(pattern);
		if (aUrlStructure.hasParams()) {
			newPatternsWithParams.put(pattern, aUrlStructure);
			if (newDeepToPatternUrls.containsKey(aUrlStructure.getDeep()) == false)
			{
				newDeepToPatternUrls.put(aUrlStructure.getDeep(), new HashSet<String>());
			}
			newDeepToPatternUrls.get(aUrlStructure.getDeep()).add(pattern);
		} else
		{
			newSimplePatterns.add(pattern);
		}
		if (aUrlStructure.isAllMatch())
		{
		    newPatternsAllMatch.put(pattern, aUrlStructure);
		}
	}
	
	this.patterns = newPatters;
	this.simplePatterns = newSimplePatterns;
	this.patternsWithParams = newPatternsWithParams;
	this.deepToPatternUrls = newDeepToPatternUrls;
	this.patternsWithAllMatch = newPatternsAllMatch;
	
	setFingerPrint(fingerPrint);
}


/*
 * possible matching
 * 
 * Matching is case sensitive 
 */
public URLMatcherResult matchUrlToPattern(String url)
{
	if (null == patterns) return null;
	// get rid of url request params i.e /news?id=123 will make the url as /news
	int indexQ = 0; 
	if ((indexQ = url.indexOf('?'))>=0)
	{
		url = url.substring(0, indexQ);
	}
	URLMatcherResult result = new URLMatcherResult();
	result.setUrlRequest(url);
	// check if the url matches one of the non parameterized patterns
	if (simplePatterns.contains(url))
	{
		result.setUrlPattern(url);
		return result;
	}
	
	URLDataStructure urlDataStructure = new URLDataStructure(url);
	Map<Integer, String> urlClearSubUrls = urlDataStructure.getClearSubUrl();
    Map<String, String> mapParams = new HashMap<String, String>();

	// get all the patterns with the same url deep
	Set<String> toSearchLevel1 = this.deepToPatternUrls.get(urlDataStructure.getDeep());
	if (null != toSearchLevel1)
	{
    	// toSearchLevel2 will contain all patterns that have the same clear sub urls and the same deep as the url
    	// the key is the pattern and the value a weight that measures the possibility to have a match, as greater the value as greater the posibility.
    	Map<String, Integer> toSearchLevel2 = new HashMap<String, Integer>(); 
    	for (String pattern: toSearchLevel1)
    	{
    		Map<Integer, String> patternClearSubUrl = this.patternsWithParams.get(pattern).getClearSubUrl();
    		if (isMapIncluded(patternClearSubUrl, urlClearSubUrls))
    		{
    			toSearchLevel2.put(pattern, patternClearSubUrl.size()*1000);
    			
    			Map<Integer, String> patternDirtySubUrl = this.patternsWithParams.get(pattern).getDirtySubUrl();
    			for(int i: patternDirtySubUrl.keySet())
    			{
    			    // increase the weight with the common number of characters in each dirty suburl 
    			    String patternSubUrl = patternDirtySubUrl.get(i);
    			    String inputSubUrl = urlClearSubUrls.get(i);
    			    int weight = toSearchLevel2.get(pattern);
    			    int lengthP = patternSubUrl.length();
    			    int lengthI = inputSubUrl.length();
    			    if (lengthP > lengthI) lengthP = lengthI;
    			    for(int x = 0; x< lengthP; x++)
    			    {
    			        if (patternSubUrl.charAt(x) == inputSubUrl.charAt(x))
    			        {
    			            weight = weight + 1;
    			        } else
    			            break;
    			    }
    			    toSearchLevel2.put(pattern, weight);
    			}
    		}
    	} 
    	
    	//the search was narrowed enough, we match against patterns with the same deep and common clear suburls 
    	// the match begins with the most probable patterns, the ones that have the most common clear suburls
    	String urlPatternMatched = "";
    	ArrayList<Integer> searchLevel2Order = new ArrayList<Integer>(toSearchLevel2.values());
    	Collections.sort(searchLevel2Order);
    	ArrayList<String> orderedPatterns = new ArrayList<String>();
    	for(int i= searchLevel2Order.size()-1; i >=0; i--)
    	{
    		Set<String> keySet = toSearchLevel2.keySet();
    		for( String s : keySet)
    		{
    			if (toSearchLevel2.get(s).compareTo(searchLevel2Order.get(i)) == 0)
    			{
    				orderedPatterns.add(s);
    				toSearchLevel2.remove(s);
    				break;
    			}
    		}
    	}
    	// orderedPatterns now contain candidates ordered by their probability to match our request 
    	for (String pattern: orderedPatterns)
    	{
    		//search though each narrowed pattern
    		URLDataStructure aDataStructure = patternsWithParams.get(pattern);
    		Map<Integer, String> aDirtySubUrls = aDataStructure.getDirtySubUrl();
    		boolean found = true;
    		
    		urlPatternMatched = pattern; //keep track of the last pattern checked
    		Set<Integer> keySet = aDirtySubUrls.keySet();
    		// each pattern has a set of sub urls with params (called dirty sub urls)
    		// each such sub pattern is matched against the corresponding index from the url
    		for(Integer key : keySet)
    		{
    			String subPattern = aDirtySubUrls.get(key);
    			if (urlClearSubUrls.containsKey(key))
    			{
    				String subUrl = urlClearSubUrls.get(key);
    				Map<String, String> tempMap = null;
    				if ((tempMap = matchSubUrls(subPattern, subUrl))!= null)
    				{
    					mapParams.putAll(tempMap);
    				} else
    				{
    					// one of the sub urls does not match
    					found = false;
    					break;
    				}
    			} else
    			{
    				found = false;
    				break;
    			}
    		}
    		
    		if (found)
    		{
    			result.setPatternParams(mapParams);
    			result.setUrlPattern(urlPatternMatched);
    			return result;
    		} else
    		{
    			// current pattern did not match,clear the params for next pattern
    			mapParams.clear();
    		}		
    	}
	}
	
	// match for /{**} match all
	if (this.patternsWithAllMatch.size()>0)
	{
	    String bestPatternMarch = "";
	    for(String pattern: patternsWithAllMatch.keySet())
	    {
	        // length of '/{**} is 5 but we will test also the ending /
	        String baseUrl = pattern.substring(0, pattern.length()-4);
	        if (url.startsWith(baseUrl))
	        {
	            if (bestPatternMarch.length() < pattern.length())
	            {
	                bestPatternMarch = pattern;
	            }
	        }
	    }
	    if (bestPatternMarch.length() > 0)
	    {
	        result.setUrlPattern(bestPatternMarch);
	        String baseUrl = bestPatternMarch.substring(0, bestPatternMarch.length()-5);
	        String paramValue = url.substring(baseUrl.length()+1);
	        mapParams.put("**", paramValue);
	        result.setPatternParams(mapParams);
	        return result;
	    }
	    
	}
	return null;
}

public Map<String, String> matchSubUrls(String subUrlPattern, String subUrl)
{
	// will match 'test-{id}' against 'test-234' and in this case will return a Map with key id=234
	// test-{*}-{id}
	// if subUrlPattern == subUrl and there are no params it will return an empty Map
	// if no match then will return null
	// there can be any number of parameters 'test-{keywords}-id-{id}' <-> 'test-sports-news-id-345'
	boolean canContinue = true;
	Map<String, String> result = new HashMap<String, String>();	
	while (canContinue)
	{
		int dpos = subUrlPattern.indexOf('{');
		if (dpos >= 0)
		{
			if (dpos > 0) // fixed prefix before { 
			{
				if (subUrl.startsWith(subUrlPattern.substring(0, dpos)))
				{
					subUrl = subUrl.substring(dpos);
					subUrlPattern = subUrlPattern.substring(dpos+1); //count also the '{'
				} else
				{
					return null; // no match 
				}
			} else
			{
				subUrlPattern = subUrlPattern.substring(1);	
			}
			
			dpos = subUrlPattern.indexOf('}');
			int epos = subUrlPattern.indexOf('{');
			if ((epos >=0) && (dpos > epos))
			{
				// protect against cases '{id{}'
				return null;
			}
			if (dpos >=0)
			{
				String parameter = subUrlPattern.substring(0, dpos);
				subUrlPattern = subUrlPattern.substring(dpos+1);
				if (subUrlPattern.length()>0)
				{
					// do we have another parameter?
					int tempPos = subUrlPattern.indexOf('{');
					if (tempPos >= 0)
					{				
						// we have another parameter so get the value for the first one
						String fixedValue = subUrlPattern.substring(0, tempPos);
						int fixedPos = -1;
						if (parameter.equals("*"))
						{
							fixedPos = subUrl.lastIndexOf(fixedValue);
						} else
						{
							fixedPos = subUrl.indexOf(fixedValue);
						}
						if (fixedPos >=0)
						{
							String parameterValue = subUrl.substring(0, fixedPos);
							result.put(parameter, parameterValue);
							subUrl = subUrl.substring(fixedPos);
						} else
						{
							return null;
						}
					} else
					{
						// check if the subUrl ends with the fixed value
						if (subUrl.endsWith(subUrlPattern))
						{
							subUrl = subUrl.substring(0,subUrl.length() - subUrlPattern.length());
							result.put(parameter, subUrl);
							return result;
						} else
						{
							return null;
						}
					}
				} else
				{
					result.put(parameter, subUrl);
					return result;
				}
			} else
			{
				return null; // wrong formatted pattern: contains '{' but no closing '}'
			}
		} else
		{
			if (subUrl.equals(subUrlPattern))
			{
				return result;
			} else
			{
				return null;
			}
		}
	}
		
	return null;
}

public boolean isMapIncluded(Map<Integer, String> small, Map<Integer, String> large)
{
	// return true if small is included in large, false otherwise
	Set<Integer> keys = small.keySet();
	for (Object key: keys)
	{
		if (large.containsKey(key) == false)
		{
			return false;
		} else
		{
			if (large.get(key).equals(small.get(key)) == false)
			{
				return false;
			}
		}
	}
	return true;
}
public Set<String> getPatterns() {
	return patterns;
}

public void setPatterns(Set<String> patterns) {
	this.patterns = patterns;	
}
public Long getFingerPrint() {
	return fingerPrint;
}
public void setFingerPrint(Long fingerPrint) {
	this.fingerPrint = fingerPrint;
}


}
