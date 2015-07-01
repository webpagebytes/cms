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

package com.webpagebytes.cms.controllers;

import java.util.Calendar;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.webpagebytes.cms.WPBArticlesCache;
import com.webpagebytes.cms.WPBAuthenticationResult;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.cmsdata.WPBArticle;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.engine.WPBAdminDataStorageListener;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;

public class ArticleController extends Controller implements WPBAdminDataStorageListener{
	private ArticleValidator validator;
	private WPBArticlesCache wbArticleCache;
	public ArticleController()
	{
		validator = new ArticleValidator();
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		wbArticleCache = wbCacheFactory.getArticlesCacheInstance();
		adminStorage.addStorageListener(this);
	}
	
	public<T> void notify (T t, AdminDataStorageOperation o, Class<? extends Object> type)
	{
		try
		{
			if (type.equals(WPBArticle.class))
			{
				wbArticleCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// do nothing
		}
	}
	
	public void create(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBArticle article = (WPBArticle)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBArticle.class);
			Map<String, String> errors = validator.validateCreate(article);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			article.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			article.setExternalKey(adminStorage.getUniqueId());
			article.setVersion(UUID.randomUUID().toString());
			WPBArticle newArticle = adminStorage.addWithKey(article);

			WPBResource resource = new WPBResource(newArticle.getExternalKey(), newArticle.getTitle(), WPBResource.ARTICLE_TYPE);
			try
			{
				adminStorage.addWithKey(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}

			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newArticle));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_CREATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);

			List<WPBArticle> articles = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					articles = adminStorage.getAllRecords(WPBArticle.class, sortParamProp, AdminSortOperator.ASCENDING);					
				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					articles = adminStorage.getAllRecords(WPBArticle.class, sortParamProp, AdminSortOperator.DESCENDING);
				} else
				{
					articles = adminStorage.getAllRecords(WPBArticle.class);					
				}
			} else
			{
				articles = adminStorage.getAllRecords(WPBArticle.class);				
			}

			List<WPBArticle> result = filterPagination(request, articles, additionalInfo);
			
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			WPBArticle article = adminStorage.get(key, WPBArticle.class);
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}
	
	public void getExt(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String extKey = (String)request.getAttribute("key");
			List<WPBArticle> articles = adminStorage.query(WPBArticle.class, "externalKey", AdminQueryOperator.EQUAL, extKey);
			WPBArticle article = (articles.size()>0) ? articles.get(0) : null;
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			WPBArticle article = adminStorage.get(key, WPBArticle.class);
			adminStorage.delete(key, WPBArticle.class);
			
			try
			{
				if (article != null)
				{
					adminStorage.delete(article.getExternalKey(), WPBResource.class);
				}
			} catch (Exception e)
			{
				// do not propagate further
			}
			
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(article));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		org.json.JSONObject returnJson = new org.json.JSONObject();
		WPBAuthenticationResult authenticationResult = this.handleAuthentication(request);
		if (! isRequestAuthenticated(authenticationResult))
		{
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
			return ;
		}

		try
		{
			String key = (String)request.getAttribute("key");
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBArticle article = (WPBArticle)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBArticle.class);
			article.setExternalKey(key);
			Map<String, String> errors = validator.validateUpdate(article);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			article.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			article.setVersion(UUID.randomUUID().toString());
			
			WPBArticle newArticle = adminStorage.update(article);
			
			WPBResource resource = new WPBResource(newArticle.getExternalKey(), newArticle.getTitle(), WPBResource.ARTICLE_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do not propagate further
			}
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newArticle));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null, authenticationResult);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}		
	}


}
