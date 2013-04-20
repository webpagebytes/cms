package com.webbricks.cms;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;

import com.webbricks.exception.WBException;
import com.webbricks.exception.WBFileNotFoundException;
import com.webbricks.exception.WBReadConfigException;

public class OperationsReader {

	private Map<String, Pair<String, String>> operationsToMethods;
	
	
	public OperationsReader()
	{
		
	}
	
	public void initialize(String configFile) throws WBException
	{
		HashSet<String> httpOperations = new HashSet<String>();
		httpOperations.add("PUT");
		httpOperations.add("GET");
		httpOperations.add("POST");
		httpOperations.add("DELETE");
		operationsToMethods = new HashMap<String, Pair<String, String>>();
		
		try
		{
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(configFile);
			if (null == is)
			{
				throw new WBFileNotFoundException("Could not locate:" + configFile);
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
		            	}
	            	}
	            }
            }
            is.close();
		} catch (IOException e)
		{
			throw new WBReadConfigException("Coult not read config file:" + configFile, e);
		}		
	}
		
	public Pair<String,String> operationToMethod(String operation, String httpOperation)
	{
		String key = httpOperation.toUpperCase() + "_" + operation;
		if (operationsToMethods.containsKey(key))
		{
			return operationsToMethods.get(key);
		}
		return null;
	}
	
}
