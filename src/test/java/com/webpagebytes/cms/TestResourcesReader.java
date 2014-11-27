package com.webpagebytes.cms;

import org.junit.Test;
import static org.junit.Assert.*;
import com.webpagebytes.cms.exception.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class TestResourcesReader {

	@Test
	public void testReadResourceContent()
	{
		String str ="var a=1;";
		try
		{
			ResourceReader reader = new ResourceReader();
			byte[] b = reader.getResourceContent("META-INF/admin/js/base.js");
			assertTrue(Arrays.equals(b, str.getBytes()));
		} catch (WPBException e)
		{
			assertTrue(false);
		}
	}
	@Test
	public void testReadResourceNotFound()
	{
		try
		{
			ResourceReader reader = new ResourceReader();
			byte[] b = reader.getResourceContent("dummyReaderFile.txt");
			assertTrue(false);
		} 
		catch (WPBFileNotFoundException e)
		{
		}
		catch (WPBException e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testReadWhiteListConfigFile()
	{
		HashSet<String> files = new HashSet<String>();
		files.add("/js/base.js");
		files.add("/css/base.css");
		files.add("/donotchange.txt");
		try
		{
			ResourceReader reader = new ResourceReader();
			Set<String> res = reader.parseWhiteListFile("META-INF/config/resourceswhitelist.properties");
			assertTrue (res.containsAll(files) && files.containsAll(res));
		} 
		catch (WPBException e)
		{
			assertTrue (false);
		}	
	}
	
	@Test
	public void testReadWhiteListConfigFile_NotFound()
	{
		try
		{
			ResourceReader reader = new ResourceReader();
			reader.parseWhiteListFile("testFile.properties");
		} catch (Exception e)
		{
			if (!(e instanceof WPBFileNotFoundException))
			{
				assertTrue(false);
			}
		}
	}
}
