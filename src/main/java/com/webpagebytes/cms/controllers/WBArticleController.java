package com.webpagebytes.cms.controllers;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.cache.DefaultWBCacheFactory;
import com.webpagebytes.cms.cache.WBArticlesCache;
import com.webpagebytes.cms.cache.WBCacheFactory;
import com.webpagebytes.cms.cmsdata.WBArticle;
import com.webpagebytes.cms.cmsdata.WBResource;
import com.webpagebytes.cms.cmsdata.WBUri;
import com.webpagebytes.cms.datautility.AdminDataStorage;
import com.webpagebytes.cms.datautility.AdminDataStorageFactory;
import com.webpagebytes.cms.datautility.AdminDataStorageListener;
import com.webpagebytes.cms.datautility.WBJSONToFromObjectConverter;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.datautility.AdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.exception.WBException;
import com.webpagebytes.cms.exception.WBIOException;
import com.webpagebytes.cms.utility.HttpServletToolbox;

public class WBArticleController extends WBController implements AdminDataStorageListener<Object>{
	private AdminDataStorage adminStorage;
	private WBArticleValidator validator;
	private WBArticlesCache wbArticleCache;
	public WBArticleController()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = AdminDataStorageFactory.getInstance();
		validator = new WBArticleValidator();
		WBCacheFactory wbCacheFactory = DefaultWBCacheFactory.getInstance();
		wbArticleCache = wbCacheFactory.createWBArticlesCacheInstance();
		adminStorage.addStorageListener(this);
	}
	
	public void notify (Object t, AdminDataStorageOperation o, Class type)
	{
		try
		{
			if (type.equals(WBArticle.class))
			{
				wbArticleCache.Refresh();
			}
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

			WBResource resource = new WBResource(newArticle.getExternalKey(), newArticle.getTitle(), WBResource.ARTICLE_TYPE);
			try
			{
				adminStorage.addWithKey(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newArticle));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);

			List<WBArticle> articles = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					articles = adminStorage.getAllRecords(WBArticle.class, sortParamProp, AdminSortOperator.ASCENDING);					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					articles = adminStorage.getAllRecords(WBArticle.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					articles = adminStorage.getAllRecords(WBArticle.class);					
				}
			} else
			{
				articles = adminStorage.getAllRecords(WBArticle.class);				
			}

			List<WBArticle> result = filterPagination(request, articles, additionalInfo);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBArticle article = adminStorage.get(key, WBArticle.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	
	public void getExt(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			String extKey = (String)request.getAttribute("key");
			List<WBArticle> articles = adminStorage.query(WBArticle.class, "externalKey", AdminQueryOperator.EQUAL, extKey);
			WBArticle article = (articles.size()>0) ? articles.get(0) : null;
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBArticle article = adminStorage.get(key, WBArticle.class);
			adminStorage.delete(key, WBArticle.class);
			
			try
			{
				if (article != null)
				{
					adminStorage.delete(article.getExternalKey(), WBResource.class);
				}
			} catch (Exception e)
			{
				// do not propagate further
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
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
			
			WBResource resource = new WBResource(newArticle.getExternalKey(), newArticle.getTitle(), WBResource.ARTICLE_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newArticle));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}


}
