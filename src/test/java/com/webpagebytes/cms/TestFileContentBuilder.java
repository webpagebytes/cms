package com.webpagebytes.cms;

import static org.junit.Assert.*;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringBufferInputStream;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.FileContentBuilder;
import com.webpagebytes.cms.cache.WBCacheInstances;
import com.webpagebytes.cms.cache.WBFilesCache;
import com.webpagebytes.cms.cmsdata.WBFile;
import com.webpagebytes.cms.datautility.WBBlobHandler;
import com.webpagebytes.cms.exception.WBIOException;

//@RunWith(PowerMockRunner.class)
//@PrepareForTest({FileContentBuilder.class})
public class TestFileContentBuilder {

WBCacheInstances cacheInstancesMock;
WBFilesCache filesCacheMock;
FileContentBuilder fileContentBuilder;
/*
@Before
public void setUp()
{
	cacheInstancesMock = EasyMock.createMock(WBCacheInstances.class);
	filesCacheMock = EasyMock.createMock(WBFilesCache.class);
	EasyMock.expect(cacheInstancesMock.getWBFilesCache()).andReturn(filesCacheMock);
}

@Test
public void test_initialize()
{
	EasyMock.replay(cacheInstancesMock, filesCacheMock);
	fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
	fileContentBuilder.initialize();
	EasyMock.verify(cacheInstancesMock, filesCacheMock);
}

@Test
public void test_find()
{
	WBFile fileMock = EasyMock.createMock(WBFile.class);
	String externalKey = "1234";
	
	try
	{		
		EasyMock.expect(filesCacheMock.getByExternalKey(externalKey)).andReturn(fileMock);		
		EasyMock.replay(cacheInstancesMock, filesCacheMock, fileMock);
		
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		WBFile result = fileContentBuilder.find(externalKey);
		EasyMock.verify(cacheInstancesMock, filesCacheMock, fileMock);
		
		assertTrue (result == fileMock);
	} catch (Exception e)
	{
		assertTrue (false);
	}
}

@Test
public void test_getFileContent()
{
	WBFile fileMock = EasyMock.createMock(WBFile.class);
	String blobKey = "blob1234";
	WBBlobHandler blobHandlerMock = EasyMock.createMock(WBBlobHandler.class);
	String buffer = "this is my buffer";
	ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.getBytes()); 
	try
	{
		EasyMock.expect(fileMock.getBlobKey()).andReturn(blobKey);
		EasyMock.expect(blobHandlerMock.getBlobData(blobKey)).andReturn(inputStream);
		
		EasyMock.replay(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock);
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		Whitebox.setInternalState(fileContentBuilder, "blobHandler", blobHandlerMock);		
		InputStream is = fileContentBuilder.getFileContent(fileMock);
		assertTrue (is == inputStream);
		EasyMock.verify(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock);
		
	} catch (Exception e)
	{
		assertTrue (false);
	}
	
}

@Test
public void test_writeFileContent()
{
	WBFile fileMock = EasyMock.createMock(WBFile.class);
	String blobKey = "blob1234";
	WBBlobHandler blobHandlerMock = EasyMock.createMock(WBBlobHandler.class);
	String buffer = "this is my buffer";
	for(int i = 0; i< 1024; i++)
	{
		buffer = buffer.concat(Integer.toString(i));
	}
	ByteArrayInputStream inputStream = new ByteArrayInputStream(buffer.getBytes());
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	try
	{
		EasyMock.expect(fileMock.getBlobKey()).andReturn(blobKey);
		EasyMock.expect(blobHandlerMock.getBlobData(blobKey)).andReturn(inputStream);
		
		EasyMock.replay(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock);
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		Whitebox.setInternalState(fileContentBuilder, "blobHandler", blobHandlerMock);		
		fileContentBuilder.writeFileContent(fileMock, outputStream);
		EasyMock.verify(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock);
		
		assertTrue (outputStream.toString().equals(buffer));
		
	} catch (Exception e)
	{
		assertTrue (false);
	}		
}

@Test
public void test_writeFileContent_exception()
{
	WBFile fileMock = EasyMock.createMock(WBFile.class);
	String blobKey = "blob1234";
	WBBlobHandler blobHandlerMock = EasyMock.createMock(WBBlobHandler.class);
	byte[] buffer = new byte[1024];
	InputStream is = EasyMock.createMock(InputStream.class);
	
	try
	{
		EasyMock.expect(is.read((byte[])EasyMock.anyObject())).andThrow(new IOException ());

	} catch (Exception e)
	{
		assertTrue (false);
	}
	ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
	
	try
	{
		EasyMock.expect(fileMock.getBlobKey()).andReturn(blobKey);
		EasyMock.expect(blobHandlerMock.getBlobData(blobKey)).andReturn(is);
		
		EasyMock.replay(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock, is);
		fileContentBuilder = new FileContentBuilder(cacheInstancesMock);
		Whitebox.setInternalState(fileContentBuilder, "blobHandler", blobHandlerMock);		
		fileContentBuilder.writeFileContent(fileMock, outputStream);
		EasyMock.verify(cacheInstancesMock, filesCacheMock, fileMock, blobHandlerMock, is);
		
	} catch (WBIOException e)
	{
		// OK
	}		
	catch (Exception e)
	{
		assertTrue (false);
	}
}
*/
}
