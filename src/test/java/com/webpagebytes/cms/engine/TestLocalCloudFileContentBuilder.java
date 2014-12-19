package com.webpagebytes.cms.engine;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.cmsdata.WPBFileInfo;
import com.webpagebytes.cms.engine.LocalCloudFileContentBuilder;
import com.webpagebytes.cms.exception.WPBIOException;

@RunWith(PowerMockRunner.class)
public class TestLocalCloudFileContentBuilder {

private WPBFileStorage cloudFileStorageMock;

@Before
public void before()
{
	cloudFileStorageMock = EasyMock.createMock(WPBFileStorage.class);
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", cloudFileStorageMock);
}

@After
public void after()
{
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", (WPBFileStorage)null);
}

@Test
public void test_serveFile_wrong_file_path()
{
	try
	{
		LocalCloudFileContentBuilder fileContentBuilder = new LocalCloudFileContentBuilder();
		fileContentBuilder.serveFile(null, null, "/a_file_path");
		assertTrue(true);
	} catch (WPBIOException e)
	{
		assertTrue(false);
	}
}

@Test
public void test_serveFile()
{
	try
	{
	
	LocalCloudFileContentBuilder fileContentBuilder = new LocalCloudFileContentBuilder();
	Whitebox.setInternalState(fileContentBuilder, "cloudFileStorage", cloudFileStorageMock);

	String uri = "/__wblocalfile/public/xTEzNzA2ODctMDdjNy00YjZkLWI4ODMtOTcxNzhmNzQxMWUxL3RodW1ibmFpbC85MTM3MDY4Ny0wN2M3LTRiNmQtYjg4My05NzE3OGY3NDExZTEuanBn";
	String content = "content";
	
	
	ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
	EasyMock.expect(cloudFileStorageMock.getFileContent(EasyMock.anyObject(WPBFilePath.class))).andReturn(bais);
	
	ServletOutputStream sosMock = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cachesos = new CacheServletOutputStream(sosMock);
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cachesos);
	WPBFileInfo fileInfoMock = EasyMock.createMock(WPBFileInfo.class);
	sosMock.write(EasyMock.anyObject(byte[].class),EasyMock.anyInt(), EasyMock.anyInt());
	String contentType = "image/png";
	EasyMock.expect(cloudFileStorageMock.getFileInfo(EasyMock.anyObject(WPBFilePath.class))).andReturn(fileInfoMock);
	EasyMock.expect(fileInfoMock.getContentType()).andReturn(contentType);
	
	responseMock.setContentType(contentType);
	EasyMock.replay(cloudFileStorageMock, sosMock, responseMock, fileInfoMock);
	fileContentBuilder.serveFile(null, responseMock, uri);
	
	EasyMock.verify(cloudFileStorageMock, sosMock, responseMock, fileInfoMock);
	
	} catch (Exception e)
	{
		assertTrue(false);
	}
	
}

@Test
public void test_serveFile_exception()
{
	try
	{
	LocalCloudFileContentBuilder fileContentBuilder = new LocalCloudFileContentBuilder();
	String uri = "/__wblocalfile/public/xTEzNzA2ODctMDdjNy00YjZkLWI4ODMtOTcxNzhmNzQxMWUxL3RodW1ibmFpbC85MTM3MDY4Ny0wN2M3LTRiNmQtYjg4My05NzE3OGY3NDExZTEuanBn";
	String content = "content";	
	Whitebox.setInternalState(fileContentBuilder, "cloudFileStorage", cloudFileStorageMock);
	
	ByteArrayInputStream bais = new ByteArrayInputStream(content.getBytes());
	EasyMock.expect(cloudFileStorageMock.getFileContent(EasyMock.anyObject(WPBFilePath.class))).andReturn(bais);
	
	ServletOutputStream sosMock = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cachesos = new CacheServletOutputStream(sosMock);
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cachesos);
	WPBFileInfo fileInfoMock = EasyMock.createMock(WPBFileInfo.class);
	sosMock.write(EasyMock.anyObject(byte[].class),EasyMock.anyInt(), EasyMock.anyInt());
	String contentType = "image/png";
	EasyMock.expect(cloudFileStorageMock.getFileInfo(EasyMock.anyObject(WPBFilePath.class))).andThrow(new IOException());
	EasyMock.expect(fileInfoMock.getContentType()).andReturn(contentType);
	
	responseMock.setContentType(contentType);
	EasyMock.replay(cloudFileStorageMock, sosMock, responseMock, fileInfoMock);
	fileContentBuilder.serveFile(null, responseMock, uri);
	EasyMock.verify(cloudFileStorageMock, sosMock, responseMock, fileInfoMock);
	
	
	assertTrue(false);
	
	} catch (WPBIOException e)
	{
		assertTrue(true);
	}
	catch (Exception e)
	{
		assertTrue(false);
	}
	
}

}
