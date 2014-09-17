package com.webpagebytes.cms.utility;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.Test;
import org.junit.Before;

import static org.junit.Assert.*;

import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.utility.HttpServletToolbox;
import com.webpagebytes.cms.utility.IOFactory;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({WBJSONToFromObjectConverter.class, ServletOutputStream.class})
public class TestHttpServletToolbox {

	HttpServletToolbox httpServletToolbox;
	@Before
	public void setup()
	{
		httpServletToolbox = new HttpServletToolbox();
	}
	
	@Test
	public void testGetBodyText()
	{
		try
		{
			HttpServletRequest request = EasyMock.createMock(HttpServletRequest.class);
			ServletInputStream inputStreamMock = EasyMock.createMock(ServletInputStream.class);
			EasyMock.expect(request.getInputStream()).andReturn(inputStreamMock);
			
			IOFactory ioFactoryMock = EasyMock.createMock(IOFactory.class);
			String buffer = "";
			for(int i = 0;i< 1000; i++)
			{
				buffer = buffer + ("x" + i);
			}
			StringReader reader = new StringReader(buffer);
			EasyMock.expect(ioFactoryMock.createBufferedUTF8Reader(inputStreamMock)).andReturn(reader);
			inputStreamMock.close();
			
			EasyMock.replay(request, inputStreamMock, ioFactoryMock);
			httpServletToolbox.setIoFactory(ioFactoryMock);
			
			String result = httpServletToolbox.getBodyText(request);
			
			EasyMock.verify(request, inputStreamMock, ioFactoryMock);
			assertTrue (result.compareTo(buffer) == 0);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}
	
	@Test
	public void testGetIOFactory()
	{
		assertTrue (httpServletToolbox.getIoFactory() != null);
	}
	
	@Test
	public void testWriteBodyResponseAsJson_ok()
	{
		try
		{
			HashMap<String, String> errors = null;
			String data = "data";
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			outputStream.write(EasyMock.capture(captureContent));
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, data, errors);
			
			org.json.JSONObject json = new org.json.JSONObject(new String (captureContent.getValue()));
			Integer captureLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("OK") == 0);
			assertTrue (json.getString("payload").compareTo(data) == 0);
			assertTrue (json.getString("errors").compareTo("{}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_json_ok()
	{
		try
		{
			HashMap<String, String> errors = null;
			org.json.JSONObject object = new org.json.JSONObject();
			object.put("key", "value");
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			outputStream.write(EasyMock.capture(captureContent));
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, object, errors);
			
			org.json.JSONObject json = new org.json.JSONObject(new String (captureContent.getValue()));
			Integer captureLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("OK") == 0);
			assertTrue (json.getString("payload").compareTo("{\"key\":\"value\"}") == 0);
			assertTrue (json.getString("errors").compareTo("{}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_fail()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			errors.put("key","value");
			org.json.JSONObject object = new org.json.JSONObject();
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.write(EasyMock.capture(captureContent));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, object, errors);
			
			EasyMock.verify(responseMock, outputStream);
			org.json.JSONObject json = new org.json.JSONObject(new String(captureContent.getValue()));
			Integer captureContentLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("FAIL") == 0);
			assertTrue (json.getString("payload").compareTo("{}") == 0);
			assertTrue (json.getString("errors").compareTo("{\"key\":\"value\"}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureContentLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_json_fail()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			errors.put("key","value");
			String data = "data";
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.write(EasyMock.capture(captureContent));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, data, errors);
			
			EasyMock.verify(responseMock, outputStream);
			org.json.JSONObject json = new org.json.JSONObject(new String(captureContent.getValue()));
			Integer captureContentLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("FAIL") == 0);
			assertTrue (json.getString("payload").compareTo(data) == 0);
			assertTrue (json.getString("errors").compareTo("{\"key\":\"value\"}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureContentLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_exception()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			String data = "data";
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			responseMock.setContentType("application/json");
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.write(EasyMock.capture(captureContent));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);

			httpServletToolbox.writeBodyResponseAsJson(responseMock, data, errors);
			EasyMock.verify(responseMock, outputStream);
			org.json.JSONObject json = new org.json.JSONObject(new String (captureContent.getValue()));
			Integer captureContentLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("FAIL") == 0);
			assertTrue (json.getString("payload").compareTo("{}") == 0);
			assertTrue (json.getString("errors").compareTo("{\"reason\":\"WB_UNKNOWN_ERROR\"}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureContentLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_json_exception()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			org.json.JSONObject object = new org.json.JSONObject();
			
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			ServletOutputStream outputStream = PowerMock.createMock(ServletOutputStream.class);
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			responseMock.setContentType("application/json");
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			Capture<byte[]> captureContent = new Capture<byte[]>();
			Capture<Integer> captureInt = new Capture<Integer>();
			EasyMock.expect(responseMock.getOutputStream()).andReturn(outputStream);
			responseMock.setContentLength(EasyMock.captureInt(captureInt));
			outputStream.write(EasyMock.capture(captureContent));
			outputStream.flush();
			
			EasyMock.replay(responseMock, outputStream);

			httpServletToolbox.writeBodyResponseAsJson(responseMock, object, errors);
			EasyMock.verify(responseMock, outputStream);
			org.json.JSONObject json = new org.json.JSONObject(new String (captureContent.getValue()));
			Integer captureContentLen = json.toString().length();
			assertTrue (json.getString("status").compareTo("FAIL") == 0);
			assertTrue (json.getString("payload").compareTo("{}") == 0);
			assertTrue (json.getString("errors").compareTo("{\"reason\":\"WB_UNKNOWN_ERROR\"}") == 0);
			assertTrue (captureInt.getValue().compareTo(captureContentLen) == 0);
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_exception_exception()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			String data = "data";
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			
			responseMock.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			EasyMock.replay(responseMock);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, data, errors);
			EasyMock.verify(responseMock);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

	@Test
	public void testWriteBodyResponseAsJson_json_exception_exception()
	{
		try
		{
			HashMap<String, String> errors = new HashMap<String, String>();
			org.json.JSONObject object = new org.json.JSONObject();
			
			HttpServletResponse responseMock = EasyMock.createMock(HttpServletResponse.class);
			responseMock.setContentType("application/json");
			responseMock.setCharacterEncoding("UTF-8");
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			EasyMock.expect(responseMock.getOutputStream()).andThrow(new IOException());
			
			responseMock.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			
			EasyMock.replay(responseMock);
			httpServletToolbox.writeBodyResponseAsJson(responseMock, object, errors);
			EasyMock.verify(responseMock);
			
		} catch (Exception e)
		{
			assertTrue(false);
		}
	}

}
