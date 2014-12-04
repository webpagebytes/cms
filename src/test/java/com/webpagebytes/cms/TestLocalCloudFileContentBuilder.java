package com.webpagebytes.cms;

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

import com.webpagebytes.cms.appinterfaces.WPBCloudFileStorage;
import com.webpagebytes.cms.cmsdata.WPBCloudFile;
import com.webpagebytes.cms.cmsdata.WPBCloudFileInfo;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.exception.WPBIOException;

@RunWith(PowerMockRunner.class)
public class TestLocalCloudFileContentBuilder {

private WPBCloudFileStorage cloudFileStorageMock;

@Before
public void before()
{
	cloudFileStorageMock = EasyMock.createMock(WPBCloudFileStorage.class);
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", cloudFileStorageMock);
}

@After
public void after()
{
	Whitebox.setInternalState(WPBCloudFileStorageFactory.class, "instance", (WPBCloudFileStorage)null);
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
	EasyMock.expect(cloudFileStorageMock.getFileContent(EasyMock.anyObject(WPBCloudFile.class))).andReturn(bais);
	
	ServletOutputStream sosMock = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cachesos = new CacheServletOutputStream(sosMock);
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cachesos);
	WPBCloudFileInfo fileInfoMock = EasyMock.createMock(WPBCloudFileInfo.class);
	sosMock.write(EasyMock.anyObject(byte[].class),EasyMock.anyInt(), EasyMock.anyInt());
	String contentType = "image/png";
	EasyMock.expect(cloudFileStorageMock.getFileInfo(EasyMock.anyObject(WPBCloudFile.class))).andReturn(fileInfoMock);
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
	EasyMock.expect(cloudFileStorageMock.getFileContent(EasyMock.anyObject(WPBCloudFile.class))).andReturn(bais);
	
	ServletOutputStream sosMock = EasyMock.createMock(ServletOutputStream.class);
	CacheServletOutputStream cachesos = new CacheServletOutputStream(sosMock);
	HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
	EasyMock.expect(responseMock.getOutputStream()).andReturn(cachesos);
	WPBCloudFileInfo fileInfoMock = EasyMock.createMock(WPBCloudFileInfo.class);
	sosMock.write(EasyMock.anyObject(byte[].class),EasyMock.anyInt(), EasyMock.anyInt());
	String contentType = "image/png";
	EasyMock.expect(cloudFileStorageMock.getFileInfo(EasyMock.anyObject(WPBCloudFile.class))).andThrow(new IOException());
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
