package com.webpagebytes.cms.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Map;

public class HttpServletToolbox {

	IOFactory ioFactory;
	
	public HttpServletToolbox()
	{
		ioFactory = new WBIOFactory();
	}
	public void writeBodyResponseAsJson(HttpServletResponse response, String data, Map<String, String> errors)
	{

		try
		{
			org.json.JSONObject jsonResponse = new org.json.JSONObject();
			org.json.JSONObject jsonErrors = new org.json.JSONObject();
			if (errors == null || errors.keySet().size() == 0)
			{
				jsonResponse.put("status", "OK");
			}else
			{
				jsonResponse.put("status", "FAIL");
				for(String key: errors.keySet())
				{			
					jsonErrors.put(key, errors.get(key));
				}
			}
			jsonResponse.put("errors", jsonErrors);
			jsonResponse.put("payload", data);
			String jsonString = jsonResponse.toString();
			response.setContentType("application/json");			
			response.setCharacterEncoding("UTF-8");
			ServletOutputStream writer = response.getOutputStream();
			byte[] utf8bytes = jsonString.getBytes("UTF-8");
			writer.write(utf8bytes);
			response.setContentLength(utf8bytes.length);
			writer.flush();
			
		} catch (Exception e)
		{
			try
			{
				String errorResponse = "{\"status\":\"FAIL\",\"payload\":\"{}\",\"errors\":{\"reason\":\"WB_UNKNOWN_ERROR\"}}";
				ServletOutputStream writer = response.getOutputStream();
				response.setContentType("application/json");				
				byte[] utf8bytes = errorResponse.getBytes("UTF-8");
				response.setContentLength(utf8bytes.length);
				writer.write(utf8bytes);
				writer.flush();
			} catch (IOException ioe)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		
	}
	
	public void writeBodyResponseAsJson(HttpServletResponse response, org.json.JSONObject data, Map<String, String> errors)
	{

		try
		{
			org.json.JSONObject jsonResponse = new org.json.JSONObject();
			org.json.JSONObject jsonErrors = new org.json.JSONObject();
			if (errors == null || errors.keySet().size() == 0)
			{
				jsonResponse.put("status", "OK");
			}else
			{
				jsonResponse.put("status", "FAIL");
				for(String key: errors.keySet())
				{			
					jsonErrors.put(key, errors.get(key));
				}
			}
			jsonResponse.put("errors", jsonErrors);
			jsonResponse.put("payload", data);
			String jsonString = jsonResponse.toString();
			response.setContentType("application/json");			
			response.setCharacterEncoding("UTF-8");
			ServletOutputStream writer = response.getOutputStream();
			byte[] utf8bytes = jsonString.getBytes("UTF-8");
			writer.write(utf8bytes);
			response.setContentLength(utf8bytes.length);
			writer.flush();
			
		} catch (Exception e)
		{
			try
			{
				String errorResponse = "{\"status\":\"FAIL\",\"payload\":\"{}\",\"errors\":{\"reason\":\"WB_UNKNOWN_ERROR\"}}";
				ServletOutputStream writer = response.getOutputStream();
				response.setContentType("application/json");				
				byte[] utf8bytes = errorResponse.getBytes("UTF-8");
				response.setContentLength(utf8bytes.length);
				writer.write(utf8bytes);
				writer.flush();
			} catch (IOException ioe)
			{
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		}
		
	}

	public String getBodyText(HttpServletRequest request) throws Exception
	{
		StringWriter writer = new StringWriter();
		InputStream is = request.getInputStream();		
		char[] buffer = new char[1024];
	    try {
	    	Reader reader = ioFactory.createBufferedUTF8Reader(is);
	        int n;
	            while ((n = reader.read(buffer)) != -1) {
	                writer.write(buffer, 0, n);
	            }
	    } 
	    finally {
	    	is.close();
	    }
	    
		return writer.toString();
	}
	public IOFactory getIoFactory() {
		return ioFactory;
	}
	public void setIoFactory(IOFactory ioFactory) {
		this.ioFactory = ioFactory;
	}
	
	
}
