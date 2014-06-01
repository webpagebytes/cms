package com.webpagebytes.cms.datautility;

import static org.junit.Assert.*;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;

import com.webpagebytes.cms.datautility.WBCloudFile;
import com.webpagebytes.cms.datautility.WBCloudFileInfo;
import com.webpagebytes.cms.datautility.local.WBLocalCloudFileStorage;

@RunWith(PowerMockRunner.class)
public class TestWBLocalCloudFileStorage {

WBLocalCloudFileStorage storage;

@Before
public void setup() throws IOException
{
	File file = File.createTempFile("wb_local_storage", "1");
	if (file.exists())
	{
		file.delete();
	}
	file.mkdir();
	storage = new WBLocalCloudFileStorage(file.getPath(), "");
}

@After
public void teardown() throws IOException
{
	File dir = new File(storage.getDataDir());
	FileUtils.deleteDirectory(dir);
}

public void test_initialize()
{
	try
	{
		File file = File.createTempFile("wb_local_storage", "1");
		if (file.exists())
		{
			file.delete();
		}
		file.mkdir();
	
		WBLocalCloudFileStorage storage = new WBLocalCloudFileStorage(file.getPath(), "");
		assertTrue (storage.isInitialized());
	} catch (IOException e)
	{
		assertTrue (false);
	}
}

@Test
public void test_storeFile()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
			
	} catch (IOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getFileInfo()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
		
		WBCloudFileInfo fileInfo = storage.getFileInfo(file);
		assertTrue(fileInfo.getContentType().equals("application/octet-stream"));
		assertTrue(fileInfo.getCrc32() > 0);
		assertTrue(fileInfo.getMd5().length()>0);
		assertTrue(fileInfo.getCreationDate()>0);
		assertTrue(fileInfo.getSize()>=0);
	
	} catch (IOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_UpdateFileCustomProperties()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
		
		WBCloudFileInfo fileInfo = storage.getFileInfo(file);
		assertTrue(fileInfo.getContentType().equals("application/octet-stream"));

		Map<String, String> customProps = fileInfo.getCustomProperties();
		customProps.put("a", "value1");
		customProps.put("b", "value2");
		storage.updateFileCustomProperties(file, customProps);
		WBCloudFileInfo fileInfo2 = storage.getFileInfo(file);
		assertTrue(fileInfo2.getContentType().equals("application/octet-stream"));
		assertTrue(fileInfo.getSize() == fileInfo2.getSize());
		assertTrue(fileInfo.getCrc32() == fileInfo2.getCrc32());
		assertTrue(fileInfo.getCreationDate() == fileInfo2.getCreationDate());
		assertTrue(fileInfo.getMd5().equals(fileInfo2.getMd5()));
		assertTrue(fileInfo2.getCustomProperties().size()==2);
		assertTrue(fileInfo.getCustomProperties().get("a").equals("value1"));
		assertTrue(fileInfo.getCustomProperties().get("b").equals("value2"));		
	} catch (IOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_updateContentType()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
		
		WBCloudFileInfo fileInfo = storage.getFileInfo(file);
		assertTrue(fileInfo.getContentType().equals("application/octet-stream"));

		storage.updateContentType(file, "text/html");
		WBCloudFileInfo fileInfo2 = storage.getFileInfo(file);
		assertTrue(fileInfo2.getContentType().equals("text/html"));
		assertTrue(fileInfo.getSize() == fileInfo2.getSize());
		assertTrue(fileInfo.getCrc32() == fileInfo2.getCrc32());
		assertTrue(fileInfo.getCreationDate() == fileInfo2.getCreationDate());
		assertTrue(fileInfo.getMd5().equals(fileInfo2.getMd5()));
		assertTrue(fileInfo.getCustomProperties().size()==fileInfo2.getCustomProperties().size());
	} catch (IOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_file_name_special_characters()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test?<>:\"//test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
		
		WBCloudFileInfo fileInfo = storage.getFileInfo(file);
		assertTrue(fileInfo.getContentType().equals("application/octet-stream"));

	} catch (IOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_getFileContent()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is \nstring 1".getBytes());
		storage.storeFile(bais, file);
		
		InputStream is = storage.getFileContent(file);
		
		byte[] buffer = new byte[1000];
		IOUtils.read(is, buffer);
		Arrays.equals("this is string 1".getBytes(), buffer);
		IOUtils.closeQuietly(is);
		IOUtils.closeQuietly(bais);
	} catch (Exception e)
	{
		assertTrue(false);
	}
}

@Test
public void test_storeFile_already_exists()
{
	try
	{
		WBCloudFile file = new WBCloudFile("public", "test.txt");
		ByteArrayInputStream bais = new ByteArrayInputStream("this is string 1".getBytes());
		storage.storeFile(bais, file);
		
		ByteArrayInputStream bais_2 = new ByteArrayInputStream("this is string 2".getBytes());
		storage.storeFile(bais_2, file);
		
		
	} catch (IOException e)
	{
		return;
	}
	assertTrue(false);
}

}
