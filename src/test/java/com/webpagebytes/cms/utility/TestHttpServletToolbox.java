package com.webpagebytes.cms.utility;


import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletOutputStream;
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

import com.webpagebytes.cms.engine.JSONToFromObjectConverter;
import com.webpagebytes.cms.utility.HttpServletToolbox;

@RunWith(PowerMockRunner.class)
@PrepareForTest ({JSONToFromObjectConverter.class, ServletOutputStream.class})
public class TestHttpServletToolbox {

	HttpServletToolbox httpServletToolbox;
	@Before
	public void setup()
	{
		httpServletToolbox = new HttpServletToolbox();
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
			assertTrue (json.getJSONObject("errors").toString().compareTo("{}") == 0);
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
			assertTrue (json.getJSONObject("payload").toString().compareTo("{\"key\":\"value\"}") == 0);
			assertTrue (json.getJSONObject("errors").toString().compareTo("{}") == 0);
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
			assertTrue (json.getJSONObject("payload").toString().compareTo("{}") == 0);
			assertTrue (json.getJSONObject("errors").toString().compareTo("{\"key\":\"value\"}") == 0);
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
			assertTrue (json.getJSONObject("errors").toString().compareTo("{\"key\":\"value\"}") == 0);
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
			assertTrue (json.getJSONObject("errors").toString().compareTo("{\"reason\":\"WB_UNKNOWN_ERROR\"}") == 0);
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
			assertTrue (json.getJSONObject("errors").toString().compareTo("{\"reason\":\"WB_UNKNOWN_ERROR\"}") == 0);
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
