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

import java.io.ByteArrayInputStream;


import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.WPBAdminDataStorage;
import com.webpagebytes.cms.WPBCacheFactory;
import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.WPBFilesCache;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminQueryOperator;
import com.webpagebytes.cms.WPBAdminDataStorage.AdminSortOperator;
import com.webpagebytes.cms.cmsdata.WPBFileInfo;
import com.webpagebytes.cms.cmsdata.WPBFile;
import com.webpagebytes.cms.cmsdata.WPBResource;
import com.webpagebytes.cms.cmsdata.WPBUri;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageFactory;
import com.webpagebytes.cms.datautility.WPBAdminDataStorageListener;
import com.webpagebytes.cms.datautility.WPBCloudFileStorageFactory;
import com.webpagebytes.cms.datautility.WPBImageProcessorFactory;
import com.webpagebytes.cms.engine.DefaultImageProcessor;
import com.webpagebytes.cms.engine.DefaultWPBCacheFactory;
import com.webpagebytes.cms.exception.WPBException;
import com.webpagebytes.cms.exception.WPBIOException;
import com.webpagebytes.cms.utility.ContentTypeDetector;


public class FileController extends Controller implements WPBAdminDataStorageListener{
	public static final String PUBLIC_BUCKET = "public";
	
	private WPBAdminDataStorage adminStorage;
	private WPBFileStorage cloudFileStorage;
	private FileValidator validator;
	private WPBFilesCache filesCache;
	private DefaultImageProcessor imageProcessor;
	
	public FileController()
	{
		adminStorage = WPBAdminDataStorageFactory.getInstance();
		validator = new FileValidator();
		cloudFileStorage = WPBCloudFileStorageFactory.getInstance();
		WPBCacheFactory wbCacheFactory = DefaultWPBCacheFactory.getInstance();
		filesCache = wbCacheFactory.getFilesCacheInstance();	
		adminStorage.addStorageListener(this);
		imageProcessor = WPBImageProcessorFactory.getInstance();
	}
	
	public<T> void notify (T t, AdminDataStorageOperation o, Class<? extends Object> type)
	{
		try
		{
			if (type.equals(WPBFile.class))
			{
				filesCache.Refresh();
			}
		} catch (WPBIOException e)
		{
			// TBD
		}
	}

	public void upload(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			  ServletFileUpload upload = new ServletFileUpload();
		      upload.setHeaderEncoding("UTF-8");
		      FileItemIterator iterator = upload.getItemIterator(request);
		      while (iterator.hasNext()) {
		        FileItemStream item = iterator.next(); 
		        if (!item.isFormField() && item.getFieldName().equals("file")) {
		          InputStream stream = item.openStream();
		          WPBFile wbFile = null;
		          if (request.getAttribute("key") != null)
		          {
		        	  // this is an upload as update for an existing file
		        	  Long key = Long.valueOf((String)request.getAttribute("key"));
		        	  wbFile = adminStorage.get(key, WPBFile.class);
		        	  
		        	  //old file need to be deleted from cloud
		              String oldFilePath = wbFile.getBlobKey();
			          WPBFilePath oldCloudFile = new WPBFilePath(PUBLIC_BUCKET, oldFilePath);
			          cloudFileStorage.deleteFile(oldCloudFile);
			          
			          // if there is a thumbnail then that needs to be deleted too
			          if (wbFile.getThumbnailBlobKey() != null)
			          {
			        	  WPBFilePath oldThumbnail = new WPBFilePath(PUBLIC_BUCKET, wbFile.getThumbnailBlobKey());
			        	  cloudFileStorage.deleteFile(oldThumbnail);
			          }
		          } else
		          {
		        	  // this is a new upload
		        	  wbFile = new WPBFile();
		        	  wbFile.setExternalKey(adminStorage.getUniqueId());    			        
		          }
		          String uniqueId = adminStorage.getUniqueId();
		          String filePath = uniqueId + "/" + item.getName();
		          WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, filePath);
		          cloudFileStorage.storeFile(stream, cloudFile);
		          cloudFileStorage.updateContentType(cloudFile, ContentTypeDetector.fileNameToContentType(item.getName()));
		          
		          WPBFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
		          wbFile.setBlobKey(cloudFile.getPath());
		          wbFile.setHash(fileInfo.getCrc32());
		          wbFile.setFileName(item.getName());
		          wbFile.setName( request.getParameter("name") != null ? request.getParameter("name"): item.getName());
		          wbFile.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
		          wbFile.setSize(fileInfo.getSize());
		          wbFile.setContentType(fileInfo.getContentType());
		          wbFile.setAdjustedContentType(wbFile.getContentType());
		          wbFile.setShortType(ContentTypeDetector.contentTypeToShortType(wbFile.getContentType()));
		          
		          String thumbnailfilePath = uniqueId + "/thumbnail/" + uniqueId + ".jpg";
		          WPBFilePath thumbnailCloudFile = new WPBFilePath(PUBLIC_BUCKET, thumbnailfilePath);
		          ByteArrayOutputStream bos = new ByteArrayOutputStream();
		          imageProcessor.resizeImage(cloudFileStorage, cloudFile, 60, "jpg", bos);
		          ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		          cloudFileStorage.storeFile(bis, thumbnailCloudFile);
		          cloudFileStorage.updateContentType(thumbnailCloudFile, "image/jpg");
		          wbFile.setThumbnailBlobKey(thumbnailCloudFile.getPath());
		          
		  		WPBResource resource = new WPBResource(wbFile.getExternalKey(), wbFile.getName(), WPBResource.FILE_TYPE);

		          if (wbFile.getPrivkey() != null)
		          {
		        	  wbFile = adminStorage.update(wbFile);		        	  
						try
						{
							adminStorage.update(resource);
						} catch (Exception e)
						{
							// do not propagate further
						}

		          } else
		          {
		        	  wbFile = adminStorage.add(wbFile);
						try
						{
							adminStorage.addWithKey(resource);
						} catch (Exception e)
						{
							// do not propagate further
						}

		          }
		          
				org.json.JSONObject returnJson = new org.json.JSONObject();
				returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbFile));			
				httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
		        }
		      }		
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WPBFile wbimage = (WPBFile)jsonObjectConverter.objectFromJSONString(jsonRequest, WPBFile.class);
			wbimage.setPrivkey(key);
			Map<String, String> errors = validator.validateUpdate(wbimage);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			WPBFile existingImage = adminStorage.get(key, WPBFile.class);
			existingImage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			existingImage.setName(wbimage.getName());
			existingImage.setAdjustedContentType(wbimage.getAdjustedContentType());
			WPBFile newFile = adminStorage.update(existingImage);
			
			WPBResource resource = new WPBResource(newFile.getExternalKey(), newFile.getName(), WPBResource.FILE_TYPE);
			try
			{
				adminStorage.update(resource);
			} catch (Exception e)
			{
				// do nothing
			}
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newFile));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}				
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBFile tempFile = adminStorage.get(key, WPBFile.class);
			if (tempFile != null)
			{
				if (tempFile.getBlobKey() != null)
				{
					WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, tempFile.getBlobKey());
					cloudFileStorage.deleteFile(cloudFile);
				}
				if (tempFile.getThumbnailBlobKey() != null)
				{
					WPBFilePath cloudThumbnailFile = new WPBFilePath(PUBLIC_BUCKET, tempFile.getThumbnailBlobKey());
					cloudFileStorage.deleteFile(cloudThumbnailFile);
				}
				
				try
				{
					adminStorage.delete(tempFile.getExternalKey(), WPBResource.class);
				} catch (Exception e)
				{
					// do not propagate further
				}

			}
			adminStorage.delete(key, WPBFile.class);
			
			WPBFile param = new WPBFile();
			param.setPrivkey(key);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(param));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_DELETE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);	
		}		
	}

	public void getAll(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Map<String, Object> additionalInfo = new HashMap<String, Object> ();			
			String sortParamDir = request.getParameter(SORT_PARAMETER_DIRECTION);
			String sortParamProp = request.getParameter(SORT_PARAMETER_PROPERTY);

			String shortType = request.getParameter("type");
			List<WPBFile> files = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WPBFile.class, sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.queryWithSort(WPBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType, sortParamProp, AdminSortOperator.ASCENDING);
					}

				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WPBFile.class, sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.queryWithSort(WPBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType, sortParamProp, AdminSortOperator.DESCENDING);
					}

				} else
				{
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WPBFile.class);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.query(WPBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType);
					}
				}
			} else
			{
				if (null == shortType)
				{
					files = adminStorage.getAllRecords(WPBFile.class);
				} else
				{
					shortType = shortType.toLowerCase();
					files = adminStorage.query(WPBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType);
				}
			}

			List<WPBFile> result = filterPagination(request, files, additionalInfo);
			for(WPBFile wbFile: result)
			{
				setPublicFilePath(wbFile, cloudFileStorage);
			}
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONArrayFromListObjects(result));
			returnJson.put(ADDTIONAL_DATA, jsonObjectConverter.JSONObjectFromMap(additionalInfo));
			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}
	}


	private static void setPublicFilePath(WPBFile wbFile, WPBFileStorage cloudFileStorage)
	{
		wbFile.setPublicUrl(cloudFileStorage.getPublicFileUrl(new WPBFilePath(PUBLIC_BUCKET, wbFile.getBlobKey())));
		wbFile.setThumbnailPublicUrl(cloudFileStorage.getPublicFileUrl(new WPBFilePath(PUBLIC_BUCKET, wbFile.getThumbnailBlobKey())));
	}
	private org.json.JSONObject get(HttpServletRequest request, HttpServletResponse response, WPBFile wbFile) throws WPBException
	{
		try
		{
			setPublicFilePath(wbFile, cloudFileStorage);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbFile));	
			
			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				List<WPBUri> uris = adminStorage.query(WPBUri.class, "resourceExternalKey", AdminQueryOperator.EQUAL, wbFile.getExternalKey());
				org.json.JSONArray arrayUris = jsonObjectConverter.JSONArrayFromListObjects(uris);
				org.json.JSONObject additionalData = new org.json.JSONObject();
				additionalData.put("uri_links", arrayUris);
				returnJson.put(ADDTIONAL_DATA, additionalData);			
			}
			return returnJson;
		} catch (Exception e)
		{
			throw new WPBException("cannot get file details", e);
		}
	}
	public void get(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBFile wbFile = adminStorage.get(key, WPBFile.class);			
			org.json.JSONObject returnJson = get(request, response, wbFile);
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}

	public void getExt(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			String extKey = (String)request.getAttribute("key");
			List<WPBFile> wbFiles = adminStorage.query(WPBFile.class, "externalKey", AdminQueryOperator.EQUAL, extKey);			
			WPBFile wbFile = (wbFiles.size()>0) ? wbFiles.get(0) : null; 
			org.json.JSONObject returnJson = get(request, response, wbFile);
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WPBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}

	public void downloadResource(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBFile wbfile = adminStorage.get(key, WPBFile.class);
			WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, wbfile.getBlobKey());
			InputStream is = cloudFileStorage.getFileContent(cloudFile);
			response.setContentType(wbfile.getContentType());			
			response.setHeader("Content-Disposition", "attachment; filename=\"" + wbfile.getFileName() + "\"");
			response.setContentLength(wbfile.getSize().intValue());
			OutputStream os = response.getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		} catch (Exception e)		
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
		
	}

	public void serveResource(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WPBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WPBFile wbfile = adminStorage.get(key, WPBFile.class);
			WPBFilePath cloudFile = new WPBFilePath(PUBLIC_BUCKET, wbfile.getBlobKey());
			InputStream is = cloudFileStorage.getFileContent(cloudFile);
			response.setContentType(wbfile.getContentType());
			response.setContentLength(wbfile.getSize().intValue());
			OutputStream os = response.getOutputStream();
			IOUtils.copy(is, os);
			os.flush();
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(os);
		} catch (Exception e)		
		{
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		}
	}


}
