package com.webbricks.cms;
import java.util.Map;

import java.util.HashMap;
import java.io.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.exception.WBException;
import com.webbricks.exception.WBResourceNotFoundException;
import javax.servlet.*;

public class ResourceRequestProcessor {

	
	private static Map<String, String> resourcesContentType = new HashMap<String, String>();
	private StaticResourceMap resourcesMap = new StaticResourceMap();;
	
	static
	{
		resourcesContentType.put("js", "application/x-javascript");
		resourcesContentType.put("css", "text/css");
		resourcesContentType.put("png", "image/png");
		resourcesContentType.put("jpg", "image/jpg");
		resourcesContentType.put("gif", "image/gif");
		resourcesContentType.put("html", "text/html");
		resourcesContentType.put("htm", "text/html");
		resourcesContentType.put("jpeg", "text/jpeg");
	}
		
	public void initialize(String adminResourceFolder, String resourcesWhiteList) throws WBException
	{
		resourcesMap.initialize(adminResourceFolder, resourcesWhiteList);			
	}
	
	
	public StaticResourceMap getResourcesMap() {
		return resourcesMap;
	}


	public void setResourcesMap(StaticResourceMap resourcesMap) {
		this.resourcesMap = resourcesMap;
	}


	public boolean isResourceRequest(String requestUri)
	{
		int lastIndex = requestUri.lastIndexOf('.');
		if (lastIndex <= 0)
		{
			return false;
		}
		String type = requestUri.substring(lastIndex+1);
		type = type.toLowerCase();
		
		if (resourcesContentType.containsKey(type))
		{
			return true;
		}
		return false;
	}
	protected static boolean addContentType(HttpServletResponse resp, String requestUri)
	{
		int lastIndex = requestUri.lastIndexOf('.');
		if (lastIndex <= 0)
		{
			return false;
		}
		String type = requestUri.substring(lastIndex+1);
		type = type.toLowerCase();
		
		if (resourcesContentType.containsKey(type))
		{
			resp.setContentType(resourcesContentType.get(type));
			return true;
		}
		return false;
	}
	
	public void process(HttpServletRequest req, 
							   HttpServletResponse resp, 
							   String resource)
	{
		try
		{
			String etag = req.getHeader("If-None-Match");
			String hash = resourcesMap.getResourceHash(resource);
			resp.addHeader("Etag", hash);
			if ((etag != null) && (etag.compareToIgnoreCase(hash) == 0))
			{
				resp.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				return;
			}
			byte[] res = resourcesMap.getResource(resource);
			if (addContentType(resp, resource))
			{
				resp.getOutputStream().write(res);
				
			}
		} 
		catch (IOException e)
		{
			try
			{
				resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			} catch (IOException exception)
			{
				
			}
		}
		catch (WBResourceNotFoundException e)
		{
			try
			{
				resp.sendError(HttpServletResponse.SC_NOT_FOUND);
			} catch (IOException exception)
			{
				
			}
		}
	}
}
