package com.webbricks.cms;

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

public URLMatcher()
{
	
}
public void initialize(Set<String> patterns, Long fingerPrint)
{
	Set<String> newPatters = new HashSet<String>(patterns);
	HashSet<String> newSimplePatterns = new HashSet<String>();
	HashMap<String, URLDataStructure> newPatternsWithParams = new HashMap<String, URLDataStructure>();
	HashMap<Integer, Set<String>> newDeepToPatternUrls = new HashMap<Integer, Set<String>>();

	
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
	}
	
	this.patterns = newPatters;
	this.simplePatterns = newSimplePatterns;
	this.patternsWithParams = newPatternsWithParams;
	this.deepToPatternUrls = newDeepToPatternUrls;
	
	setFingerPrint(fingerPrint);
}

private void buildInternalData()
{
	/*
	 * Each pattern has:
	 * 1) a path deep (/test is deep 1, /test/abc is deep 2, /test/abc/xyz is deep 3, /abc/ is deep 2
	 * 2) an order of sub paths /test/{id}/value has /test/, /{id}/, /value
	 */
	for (String pattern : patterns)
	{
		URLDataStructure aUrlStructure = new URLDataStructure(pattern);
		if (aUrlStructure.hasParams()) {
			patternsWithParams.put(pattern, aUrlStructure);
			if (deepToPatternUrls.containsKey(aUrlStructure.getDeep()) == false)
			{
				deepToPatternUrls.put(aUrlStructure.getDeep(), new HashSet<String>());
			}
			deepToPatternUrls.get(aUrlStructure.getDeep()).add(pattern);
		} else
		{
			simplePatterns.add(pattern);
		}
	}
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

	// get all the patters with the same url deep
	Set<String> toSearchLevel1 = this.deepToPatternUrls.get(urlDataStructure.getDeep());
	if (null == toSearchLevel1)
	{
		return null;
	}
	// toSearchLevel2 will contain all patterns that have the same clear sub urls and the same deep as the url
	// the key is the number of clear suburls that match. as greater as possible is to have a match.
	Map<String, Integer> toSearchLevel2 = new HashMap<String, Integer>(); 
	for (String pattern: toSearchLevel1)
	{
		Map<Integer, String> patternClearSubUrl = this.patternsWithParams.get(pattern).getClearSubUrl();
		if (isMapIncluded(patternClearSubUrl, urlClearSubUrls))
		{
			toSearchLevel2.put(pattern, patternClearSubUrl.size());
		}
	} 
	
	//the search was narrowed enough, we match against patterns with the same deep and common clear suburls 
	// the match begins with the most probable patterns, the ones that have the most common clear suburls
	Map<String, String> mapParams = new HashMap<String, String>();
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
	// orderedPatterns now contain candidates with the  
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
