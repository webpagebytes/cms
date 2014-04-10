package com.webpagebytes.cms;
import java.io.File;


import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.List;

import com.webpagebytes.cms.ResourceReader;
import com.webpagebytes.cms.StaticResourceMap;
import com.webpagebytes.cms.exception.*;

import junit.framework.*;

import java.util.Date;
import java.util.Map;
import java.util.HashSet;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;

public class TestStaticResourcesMap {

	@Test
	public void test_getResourceHash()
	{
		try
		{
			StaticResourceMap resMap = new StaticResourceMap();
			Map<String, String> resourceMapHash = EasyMock.createMock(Map.class);
			String resPath = "/js/base.js";
			String hash = "1234";
			
			EasyMock.expect(resourceMapHash.containsKey(resPath)).andReturn(true);
			EasyMock.expect(resourceMapHash.get(resPath)).andReturn(hash);
			EasyMock.replay(resourceMapHash);
			
			resMap.setResourcesMapHash(resourceMapHash);
			
			String aHash = resMap.getResourceHash(resPath);
			assertTrue (aHash.compareTo(hash) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void test_getResourceHash_notFound()
	{
			Throwable exception = new Exception();
			StaticResourceMap resMap = new StaticResourceMap();
			Map<String, String> resourceMapHash = EasyMock.createMock(Map.class);
			String resPath = "/js/base.js";
			String hash = "1234";
			
			EasyMock.expect(resourceMapHash.containsKey(resPath)).andReturn(false);
			EasyMock.replay(resourceMapHash);			
			resMap.setResourcesMapHash(resourceMapHash);
			try
			{
				String aHash = resMap.getResourceHash(resPath);
				EasyMock.verify(resourceMapHash);
				
				assertTrue (aHash.compareTo(hash) == 0);
			} catch (Exception e)
			{
				exception = e;
			}
			
			assertTrue( exception instanceof WBResourceNotFoundException);
	}

	@Test
	public void test_getResource()
	{
		try
		{
			StaticResourceMap resMap = new StaticResourceMap();
			Map<String, byte[]> resourceMapContent = EasyMock.createMock(Map.class);
			String resPath = "/js/base.js";
			byte[] content = "1234".getBytes();
			
			EasyMock.expect(resourceMapContent.containsKey(resPath)).andReturn(true);
			EasyMock.expect(resourceMapContent.get(resPath)).andReturn(content);
			EasyMock.replay(resourceMapContent);
			
			resMap.setResourcesMap(resourceMapContent);
			
			byte[] aContent = resMap.getResource(resPath);
			assertTrue (Arrays.equals(aContent, content));
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void test_getResource_NotFound()
	{
		Throwable exception = new Exception();
		StaticResourceMap resMap = new StaticResourceMap();
		Map<String, byte[]> resourceMapContent = EasyMock.createMock(Map.class);
		String resPath = "/js/base.js";
		byte[] content = "1234".getBytes();
		
		EasyMock.expect(resourceMapContent.containsKey(resPath)).andReturn(false);
		EasyMock.replay(resourceMapContent);
	
		resMap.setResourcesMap(resourceMapContent);
		try
		{
			resMap.getResource(resPath);
		} catch (Exception e)
		{
			exception = e;
		}
		assertTrue (exception instanceof WBResourceNotFoundException);
	
	}

	@Test
	public void test_initialize_ok()
	{
		
		try
		{
			StaticResourceMap resMap = new StaticResourceMap();
			
			Map resourceMapContent = EasyMock.createMock(Map.class);
			Map resourceMapHash = EasyMock.createMock(Map.class);
			ResourceReader resourceReader = EasyMock.createMock(ResourceReader.class);
			
			String adminFolder = "admin";
			String content1 = "var a = 1;";
			String content2 = "a { color: #123456; }";
			String file1 = "/js/base.js";
			String file2 = "/css/base.css";
			
			String whiteListFile = "/aFile";
			HashSet<String> files = new HashSet<String>();
			files.add(file1);
			files.add(file2);			
			
			EasyMock.expect(resourceReader.parseWhiteListFile(whiteListFile)).andReturn(files);
			EasyMock.expect(resourceReader.getResourceContent("META-INF/admin/js/base.js")).andReturn(content1.getBytes());
			EasyMock.expect(resourceReader.getResourceContent("META-INF/admin/css/base.css")).andReturn(content2.getBytes());
			
			Capture<String> cfile1a = new Capture<String>();
			Capture<String> cfile1b = new Capture<String>();
			
			Capture<byte[]> ccontent1a = new Capture<byte[]>();
			Capture<byte[]> ccontent1b = new Capture<byte[]>();

			Capture<String> cfile2a = new Capture<String>();
			Capture<String> cfile2b = new Capture<String>();
			
			Capture<String> chash1 = new Capture<String>();
			Capture<String> chash2 = new Capture<String>();
			
			EasyMock.expect(resourceMapContent.put(EasyMock.capture(cfile1a), EasyMock.capture(ccontent1a))).andReturn(content1.getBytes());
			EasyMock.expect(resourceMapContent.put(EasyMock.capture(cfile1b), EasyMock.capture(ccontent1b))).andReturn(content2.getBytes());
				
			EasyMock.expect(resourceMapHash.put(EasyMock.capture(cfile2a), EasyMock.capture(chash1))).andReturn("hash1");
			EasyMock.expect(resourceMapHash.put(EasyMock.capture(cfile2b), EasyMock.capture(chash2))).andReturn("hash2");
		
			EasyMock.replay(resourceReader);
			EasyMock.replay(resourceMapContent);
			EasyMock.replay(resourceMapHash);
			
			resMap.setResReader(resourceReader);
			resMap.setResourcesMapHash(resourceMapHash);
			resMap.setResourcesMap(resourceMapContent);
			
			resMap.initialize(adminFolder, whiteListFile);
			
			HashSet resSet1 = new HashSet<String>(); 
			resSet1.add(cfile1a.getValue());
			resSet1.add(cfile1b.getValue());
			assertTrue (resSet1.containsAll(files));

			HashSet resSet2 = new HashSet<String>(); 
			resSet2.add(cfile2a.getValue());
			resSet2.add(cfile2b.getValue());
			assertTrue (resSet2.containsAll(files));		

			// CRC32 of content1
			String hash1 = "1027529934";
			// CRC32 of content2
			String hash2 = "3006211168";

			if (cfile1a.getValue().compareTo(file1) == 0)
			{
				assertTrue (Arrays.equals(ccontent1a.getValue(), content1.getBytes()));
				assertTrue (chash1.getValue().compareTo(hash1) == 0);
			} else
			{
				assertTrue (Arrays.equals(ccontent1a.getValue(), content2.getBytes()));
				assertTrue (chash1.getValue().compareTo(hash2) == 0);
			}

			if (cfile1b.getValue().compareTo(file2) == 0)
			{
				assertTrue (Arrays.equals(ccontent1b.getValue(), content2.getBytes()));
				assertTrue (chash2.getValue().compareTo(hash2) == 0);
			} else
			{
				assertTrue (Arrays.equals(ccontent1b.getValue(), content1.getBytes()));
				assertTrue (chash2.getValue().compareTo(hash1) == 0);
			}
			
			
		} catch (Exception e)
		{
			assertTrue(false);
		}		
	}
	
	@Test
	public void testGetResourcesMap()
	{
		StaticResourceMap resMap = new StaticResourceMap();
		assertTrue(resMap.getResourcesMap() != null);
	}

	@Test
	public void testGetResourcesMapHash()
	{
		StaticResourceMap resMap = new StaticResourceMap();
		assertTrue(resMap.getResourcesMapHash() != null);
	}

	@Test
	public void testGetResourcesReader()
	{
		StaticResourceMap resMap = new StaticResourceMap();
		assertTrue(resMap.getResReader() != null);
	}

}
