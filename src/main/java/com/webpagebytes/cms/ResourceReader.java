package com.webpagebytes.cms;

import java.io.ByteArrayOutputStream;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBFileNotFoundException;
import com.webpagebytes.cms.exception.WBReadConfigException;

class ResourceReader {

	public Set<String> parseWhiteListFile(String resourcesWhileList) throws WBException
	{
		Set<String> result = new HashSet<String>();
		try
		{
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourcesWhileList);
			if (null == is)
			{
				throw new WBFileNotFoundException("Could not locate:" + resourcesWhileList);
			}
			BufferedReader breader = new BufferedReader(new InputStreamReader(is));
			String line = null;
			while ((line = breader.readLine()) != null)
			{
				if (line.indexOf('[')!= -1)
				{
					continue;
				}
				line = line.trim();
				if (line.length()>0)
				{
					result.add(line);
				}
			}
			breader.close();
	        is.close();
		} catch (IOException e)
		{
			throw new WBReadConfigException("Coult not read config file:" + resourcesWhileList, e);
		}
		return result;
	}
	
	public byte[] getResourceContent(String resourceFile) throws WBException
	{
		ByteArrayOutputStream os = new ByteArrayOutputStream(4096);
		try
		{
			InputStream is = this.getClass().getClassLoader().getResourceAsStream(resourceFile);
			if (null == is)
			{
				throw new WBFileNotFoundException("Could not locate:" + resourceFile);
			}
			byte[] array = new byte[4096];
			int n = 0;
			while ((n = is.read(array)) > 0)
			{
				os.write(array, 0, n);
			}
			is.close();
		} catch(IOException e)
		{
			throw new WBException("Could not read resource file " + resourceFile, e);
		}
		return os.toByteArray();
	}

}
