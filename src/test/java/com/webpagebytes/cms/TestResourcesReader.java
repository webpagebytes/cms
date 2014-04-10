package com.webpagebytes.cms;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.webpagebytes.cms.*;
import com.webpagebytes.exception.*;

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
		} catch (WBException e)
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
		catch (WBFileNotFoundException e)
		{
		}
		catch (WBException e)
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
		catch (WBException e)
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
			if (!(e instanceof WBFileNotFoundException))
			{
				assertTrue(false);
			}
		}
	}
}
