package com.webbricks.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBArticleCache;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBUriCache;
import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.GaeAdminDataStorage;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorageListener.AdminDataStorageOperation;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.HttpServletToolbox;

public class WBArticleController extends WBController implements AdminDataStorageListener<WBArticle>{
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBArticleValidator validator;
	private WBArticleCache wbArticleCache;
	public WBArticleController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = new GaeAdminDataStorage();
		validator = new WBArticleValidator();
		WBCacheFactory wbCacheFactory = new DefaultWBCacheFactory();
		wbArticleCache = wbCacheFactory.createWBArticleCacheInstance();
	}
	
	public void notify (WBArticle t, AdminDataStorageOperation o)
	{
		try
		{
			wbArticleCache.Refresh();
		} catch (WBIOException e)
		{
			// do nothing
		}
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBArticle article = (WBArticle)jsonObjectConverter.objectFromJSONString(jsonRequest, WBArticle.class);
			Map<String, String> errors = validator.validateCreate(article);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			article.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			article.setExternalKey(adminStorage.getUniqueId());
			WBArticle newArticle = adminStorage.add(article);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newArticle, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			List<WBArticle> allRecords = adminStorage.getAllRecords(WBArticle.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromListObjects(allRecords);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}
	}
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBArticle article = adminStorage.get(key, WBArticle.class);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(article, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			adminStorage.delete(key, WBArticle.class);
			
			WBArticle article = new WBArticle();
			article.setKey(key);
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(article, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBArticle article = (WBArticle)jsonObjectConverter.objectFromJSONString(jsonRequest, WBArticle.class);
			article.setKey(key);
			Map<String, String> errors = validator.validateUpdate(article);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			article.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			WBArticle newArticle = adminStorage.update(article);
			
			String jsonReturn = jsonObjectConverter.JSONStringFromObject(newArticle, null);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonReturn.toString(), errors);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, "", errors);			
		}		
	}


}
