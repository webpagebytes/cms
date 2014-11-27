package com.webpagebytes.cms;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBFileNotFoundException;
import com.webpagebytes.cms.exception.WPBReadConfigException;
import com.webpagebytes.cms.utility.Pair;

public class AdminServletOperationsReader {

	private Map<String, Pair<String, String>> operationsToMethods;
	private Map<String, Pair<String, String>> wildOperationsToMethods;
	
	
	public AdminServletOperationsReader()
	{
		
	}
	
	public void initialize(String configFile) throws WPBException
	{
		HashSet<String> httpOperations = new HashSet<String>();
		httpOperations.add("PUT");
		httpOperations.add("GET");
		httpOperations.add("POST");
		httpOperations.add("DELETE");
		operationsToMethods = new HashMap<String, Pair<String, String>>();
		wildOperationsToMethods = new HashMap<String, Pair<String, String>>();
		
		try
		{
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(configFile);
			if (null == is)
			{
				throw new WPBFileNotFoundException("Could not locate:" + configFile);
			}
			Properties prop = new Properties();  
            prop.load(is);  
            for (String str: httpOperations)
            {
            	String readString = prop.getProperty(str);
            	if (readString == null)
            	{
            		continue;
            	}
	            String[] ajaxOperations = readString.split(";");
	            for(String operation: ajaxOperations)
	            {
	            	String key = str + "_" + operation;
	            	String property = prop.getProperty(key);
	            	if (property != null && property.length()> 0)
	            	{
		            	String[] strs = property.split("::");
		            	
		            	if (strs.length == 2)
		            	{
		            		Pair<String, String> pair = new Pair<String, String>(strs[0], strs[1]);
		            		operationsToMethods.put(key, pair);		         
		            		if (operation.indexOf('*')>0)
		            		{
		            			wildOperationsToMethods.put(key, pair);
		            		}
		            	}
	            	}
	            }
            }
            is.close();
		} catch (IOException e)
		{
			throw new WPBReadConfigException("Coult not read config file:" + configFile, e);
		}		
	}
	
	// return a pair class and method for the controller
	public Pair<String,String> operationToMethod(String operation, String httpOperation)
	{
		String key = httpOperation.toUpperCase() + "_" + operation;
		if (operationsToMethods.containsKey(key))
		{
			return operationsToMethods.get(key);
		}
		return null;
	}
	
	public String wildOperationToMethod(String operation, String httpOperation)
	{
		// operation may be /export_12_01_2003.zip and will be matched against /export_*.zip
		String temp = httpOperation.toUpperCase() + "_";
		Set<String> keys = wildOperationsToMethods.keySet();
		for (String key: keys)
		{
			if (key.length()>temp.length())
			{
				String wildUrl = key.substring(temp.length());
				String wildUrl_ = (new StringBuffer(wildUrl)).delete(wildUrl.indexOf('*'), wildUrl.length()).toString();
				if (operation.indexOf(wildUrl_) == 0 && wildUrl.length()>0)
				{
					return wildUrl;
				}
			}
		}
		return null;
	}
	
}
