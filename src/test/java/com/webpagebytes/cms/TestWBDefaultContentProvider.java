package com.webpagebytes.cms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;




import java.util.logging.Logger;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.*;

import com.webpagebytes.cms.appinterfaces.WPBModel;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBPage;
import com.webpagebytes.cms.exception.WPBException;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({OutputStream.class, WPBDefaultContentProvider.class})
public class TestWBDefaultContentProvider {

private FileContentBuilder fileContentBuilderMock;
private PageContentBuilder pageContentBuilderMock;
private WPBDefaultContentProvider contentProvider;
private Logger loggerMock;

@Before
public void before()
{
	fileContentBuilderMock = EasyMock.createMock(FileContentBuilder.class);
	pageContentBuilderMock = EasyMock.createMock(PageContentBuilder.class);
	contentProvider = new WPBDefaultContentProvider(fileContentBuilderMock, pageContentBuilderMock);
	loggerMock = EasyMock.createMock(Logger.class);
	Whitebox.setInternalState(WPBDefaultContentProvider.class, "log", loggerMock);
}


@Test
public void test_writeFileContent()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WPBFile fileMock = EasyMock.createMock(WPBFile.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andReturn(fileMock);
		fileContentBuilderMock.writeFileContent(fileMock, osMock);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result);
	} catch (WPBException e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writeFileContent_file_not_found()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andReturn(null);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result == false);
	} catch (WPBException e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writeFileContent_exception()
{
	try
	{
		String externalKey = "abc";
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		EasyMock.expect(fileContentBuilderMock.find(externalKey)).andThrow(new WPBException(""));
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, osMock);
		boolean result = contentProvider.writeFileContent(externalKey, osMock);
		assertTrue (result == false);
	} catch (WPBException e)
	{
		assertTrue (false);
	}		
}

@Test
public void test_writePageContent()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WPBPage pageMock = EasyMock.createMock(WPBPage.class);
		InternalModel modelMock = EasyMock.createMock(InternalModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andReturn(content);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, bos);
		assertTrue (bos.toString().equals(content));
		assertTrue (result);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_page_not_found()
{
	try
	{
		String externalKey = "abc";
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		OutputStream osMock  = EasyMock.createMock(OutputStream.class);
		WPBPage pageMock = EasyMock.createMock(WPBPage.class);
		WPBModel modelMock = EasyMock.createMock(WPBModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(null);
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, bos);
		assertTrue (result == false);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_ioexception()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		OutputStream osMock  = PowerMock.createMock(OutputStream.class);
		WPBPage pageMock = EasyMock.createMock(WPBPage.class);
		InternalModel modelMock = EasyMock.createMock(InternalModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andReturn(content);
		Capture<byte[]> capture = new Capture<byte[]>();
		osMock.write(EasyMock.capture(capture));
		EasyMock.expectLastCall().andThrow(new IOException());
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, osMock);
		assertTrue (result == false);
		assertTrue ((new String(capture.getValue()).equals(content)));
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

@Test
public void test_writePageContent_wbexception()
{
	try
	{
		String externalKey = "abc";
		String content = "content";
		OutputStream osMock  = PowerMock.createMock(OutputStream.class);
		WPBPage pageMock = EasyMock.createMock(WPBPage.class);
		InternalModel modelMock = EasyMock.createMock(InternalModel.class);
		EasyMock.expect(pageContentBuilderMock.findWebPage(externalKey)).andReturn(pageMock);
		EasyMock.expect(pageContentBuilderMock.buildPageContent(pageMock, modelMock)).andThrow(new WPBException(""));
		EasyMock.replay(fileContentBuilderMock, pageContentBuilderMock, pageMock, modelMock, osMock);
		boolean result = contentProvider.writePageContent(externalKey, modelMock, osMock);
		assertTrue (result == false);
	} catch (Exception e)
	{
		assertTrue(false);
	}	
}

}
