/*
 *   Copyright 2014 Webpagebytes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package com.webpagebytes.cms.utility;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.WPBAuthentication;
import com.webpagebytes.cms.WPBAuthenticationResult;

import java.util.Map;

public class HttpServletToolbox {

	public HttpServletToolbox()
	{
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
	
	public void writeBodyResponseAsJson(HttpServletResponse response, org.json.JSONObject data, Map<String, String> errors, WPBAuthenticationResult authenticationResult)
	{
		try
		{
			org.json.JSONObject jsonResponse = new org.json.JSONObject();
			org.json.JSONObject jsonErrors = new org.json.JSONObject();
			org.json.JSONObject jsonAuth = new org.json.JSONObject();
			String status = "200";
			if (errors != null && errors.keySet().size() > 0)
			{
				jsonResponse.put("status", "400");
				for(String key: errors.keySet())
				{			
					jsonErrors.put(key, errors.get(key));
				}
			}
			// leave the auth status the last
			if (authenticationResult != null && (authenticationResult.getUserIdentifier() == null || authenticationResult.getUserIdentifier().length() == 0))
			{
				status = "401";
				// make sure that if the request is not authenticated no data is returned
				data = new org.json.JSONObject();
			}
			
			if (authenticationResult != null)
			{
				jsonAuth.put(WPBAuthentication.CONFIG_LOGIN_PAGE_URL, authenticationResult.getLoginLink());
				jsonAuth.put(WPBAuthentication.CONFIG_LOGOUT_URL, authenticationResult.getLogoutLink());
				jsonAuth.put(WPBAuthentication.CONFIG_PROFILE_URL, authenticationResult.getUserProfileLink());
				jsonAuth.put(WPBAuthentication.CONFIG_USER_IDENTIFIER, authenticationResult.getUserIdentifier());
			}
			jsonResponse.put("status", status);
			jsonResponse.put("auth", jsonAuth);
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

	public String getBodyText(HttpServletRequest request) throws IOException
	{
		StringWriter writer = new StringWriter();
		InputStream is = request.getInputStream();	
		IOUtils.copy(is, writer, "UTF-8");	    
		return writer.toString();
	}	
	
}
