package com.webbricks.controllers;

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

import com.webbricks.cache.DefaultWBCacheFactory;
import com.webbricks.cache.WBCacheFactory;
import com.webbricks.cache.WBFilesCache;
import com.webbricks.cmsdata.WBFile;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.datautility.AdminDataStorage;
import com.webbricks.datautility.AdminDataStorageFactory;
import com.webbricks.datautility.AdminDataStorageListener;
import com.webbricks.datautility.WBCloudFile;
import com.webbricks.datautility.WBCloudFileInfo;
import com.webbricks.datautility.WBCloudFileStorage;
import com.webbricks.datautility.WBCloudFileStorageFactory;
import com.webbricks.datautility.WBImageProcessor;
import com.webbricks.datautility.WBImageProcessorFactory;
import com.webbricks.datautility.WBJSONToFromObjectConverter;
import com.webbricks.datautility.AdminDataStorage.AdminQueryOperator;
import com.webbricks.datautility.AdminDataStorage.AdminSortOperator;
import com.webbricks.exception.WBException;
import com.webbricks.exception.WBIOException;
import com.webbricks.utility.ContentTypeDetector;
import com.webbricks.utility.HttpServletToolbox;

public class WBFileControllerEx extends WBController implements AdminDataStorageListener<Object>{
	private HttpServletToolbox httpServletToolbox;
	private WBJSONToFromObjectConverter jsonObjectConverter;
	private AdminDataStorage adminStorage;
	private WBCloudFileStorage cloudFileStorage;
	private WBFileValidator validator;
	private WBFilesCache filesCache;
	private WBImageProcessor imageProcessor;
	
	public WBFileControllerEx()
	{
		httpServletToolbox = new HttpServletToolbox();
		jsonObjectConverter = new WBJSONToFromObjectConverter();
		adminStorage = AdminDataStorageFactory.getInstance();
		validator = new WBFileValidator();
		cloudFileStorage = WBCloudFileStorageFactory.getInstance();
		WBCacheFactory wbCacheFactory = DefaultWBCacheFactory.getInstance();
		filesCache = wbCacheFactory.createWBImagesCacheInstance();	
		adminStorage.addStorageListener(this);
		imageProcessor = WBImageProcessorFactory.getInstance();
	}
	
	public void notify (Object t, AdminDataStorageOperation o)
	{
		try
		{
			if (t instanceof WBFile)
			{
				filesCache.Refresh();
			}
		} catch (WBIOException e)
		{
			// TBD
		}
	}

	public void upload(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			  ServletFileUpload upload = new ServletFileUpload();
		      
		      FileItemIterator iterator = upload.getItemIterator(request);
		      while (iterator.hasNext()) {
		        FileItemStream item = iterator.next(); 
		        if (!item.isFormField() && item.getFieldName().equals("file")) {
		          InputStream stream = item.openStream();
		          WBFile wbFile = null;
		          if (request.getAttribute("key") != null)
		          {
		        	  // this is an upload as update for an existing file
		        	  Long key = Long.valueOf((String)request.getAttribute("key"));
		        	  wbFile = adminStorage.get(key, WBFile.class);
		        	  
		        	  //old file need to be deleted from cloud
		              String oldFilePath = wbFile.getBlobKey();
			          WBCloudFile oldCloudFile = new WBCloudFile("public", oldFilePath);
			          cloudFileStorage.deleteFile(oldCloudFile);
			          
			          // if there is a thumbnail then that needs to be deleted too
			          if (wbFile.getThumbnailBlobKey() != null)
			          {
			        	  WBCloudFile oldThumbnail = new WBCloudFile("public", wbFile.getThumbnailBlobKey());
			        	  cloudFileStorage.deleteFile(oldThumbnail);
			          }
		          } else
		          {
		        	  // this is a new upload
		        	  wbFile = new WBFile();
		        	  wbFile.setExternalKey(adminStorage.getUniqueId());    			        
		          }
		          String uniqueId = adminStorage.getUniqueId();
		          String filePath = uniqueId + "/" + item.getName();
		          WBCloudFile cloudFile = new WBCloudFile("public", filePath);
		          cloudFileStorage.storeFile(stream, cloudFile);
		          cloudFileStorage.updateContentType(cloudFile, ContentTypeDetector.fileNameToContentType(item.getName()));
		          
		          WBCloudFileInfo fileInfo = cloudFileStorage.getFileInfo(cloudFile);
		          wbFile.setBlobKey(cloudFile.getPath());
		          wbFile.setHash(fileInfo.getCrc32());
		          wbFile.setFileName(item.getName());
		          wbFile.setName( request.getParameter("name") != null ? request.getParameter("name"): item.getName());
		          wbFile.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
		          wbFile.setSize(fileInfo.getSize());
		          wbFile.setContentType(fileInfo.getContentType());
		          wbFile.setAdjustedContentType(wbFile.getContentType());
		          wbFile.setShortType(ContentTypeDetector.contentTypeToShortType(wbFile.getContentType()));
		          wbFile.setPublicUrl(cloudFileStorage.getPublicFileUrl(cloudFile));
		          
		          String thumbnailfilePath = uniqueId + "/thumbnail/" + uniqueId + ".jpg";
		          WBCloudFile thumbnailCloudFile = new WBCloudFile("public", thumbnailfilePath);
		          ByteArrayOutputStream bos = new ByteArrayOutputStream();
		          imageProcessor.resizeImage(cloudFileStorage, cloudFile, 60, "jpg", bos);
		          ByteArrayInputStream bis = new ByteArrayInputStream(bos.toByteArray());
		          cloudFileStorage.storeFile(bis, thumbnailCloudFile);
		          cloudFileStorage.updateContentType(thumbnailCloudFile, ContentTypeDetector.fileNameToContentType(item.getName()));
		          wbFile.setThumbnailPublicUrl(cloudFileStorage.getPublicFileUrl(thumbnailCloudFile));
		          wbFile.setThumbnailBlobKey(thumbnailCloudFile.getPath());
		          
		          if (wbFile.getKey() != null)
		          {
		        	  wbFile = adminStorage.update(wbFile);
		          } else
		          {
		        	  wbFile = adminStorage.add(wbFile);
		          }
		          
				org.json.JSONObject returnJson = new org.json.JSONObject();
				returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbFile));			
				httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
		        }
		      }		
		} catch (Exception e)
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}
	}

	public void update(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			String jsonRequest = httpServletToolbox.getBodyText(request);
			WBFile wbimage = (WBFile)jsonObjectConverter.objectFromJSONString(jsonRequest, WBFile.class);
			wbimage.setKey(key);
			Map<String, String> errors = validator.validateUpdate(wbimage);
			
			if (errors.size()>0)
			{
				httpServletToolbox.writeBodyResponseAsJson(response, "", errors);
				return;
			}
			WBFile existingImage = adminStorage.get(key, WBFile.class);
			existingImage.setLastModified(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime());
			existingImage.setName(wbimage.getName());
			existingImage.setAdjustedContentType(wbimage.getAdjustedContentType());
			WBFile newImage = adminStorage.update(existingImage);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(newImage));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
	
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_UPDATE_RECORD);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);			
		}				
	}
	
	public void delete(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBFile tempFile = adminStorage.get(key, WBFile.class);
			if (tempFile != null)
			{
				if (tempFile.getBlobKey() != null)
				{
					WBCloudFile cloudFile = new WBCloudFile("public", tempFile.getBlobKey());
					cloudFileStorage.deleteFile(cloudFile);
				}
				if (tempFile.getThumbnailBlobKey() != null)
				{
					WBCloudFile cloudThumbnailFile = new WBCloudFile("public", tempFile.getThumbnailBlobKey());
					cloudFileStorage.deleteFile(cloudThumbnailFile);
				}
			}
			adminStorage.delete(key, WBFile.class);
			
			WBFile param = new WBFile();
			param.setKey(key);
			
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(param));			
			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_DELETE_RECORD);
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

			String shortType = request.getParameter("type");
			List<WBFile> files = null;
			
			if (sortParamDir != null && sortParamProp != null)
			{
				if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_ASC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_ASC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WBFile.class, sortParamProp, AdminSortOperator.ASCENDING);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.queryWithSort(WBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType, sortParamProp, AdminSortOperator.ASCENDING);
					}

				} else if (sortParamDir.equals(SORT_PARAMETER_DIRECTION_DSC))
				{
					additionalInfo.put(SORT_PARAMETER_DIRECTION, SORT_PARAMETER_DIRECTION_DSC);
					additionalInfo.put(SORT_PARAMETER_PROPERTY, sortParamProp);
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WBFile.class, sortParamProp, AdminSortOperator.DESCENDING);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.queryWithSort(WBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType, sortParamProp, AdminSortOperator.DESCENDING);
					}

				} else
				{
					if (null == shortType)
					{
						files = adminStorage.getAllRecords(WBFile.class);
					} else
					{
						shortType = shortType.toLowerCase();
						files = adminStorage.query(WBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType);
					}
				}
			} else
			{
				if (null == shortType)
				{
					files = adminStorage.getAllRecords(WBFile.class);
				} else
				{
					shortType = shortType.toLowerCase();
					files = adminStorage.query(WBFile.class, "shortType", AdminQueryOperator.EQUAL, shortType);
				}
			}

			List<WBFile> result = filterPagination(request, files, additionalInfo);
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
			WBFile wbFile = adminStorage.get(key, WBFile.class);
			org.json.JSONObject returnJson = new org.json.JSONObject();
			returnJson.put(DATA, jsonObjectConverter.JSONFromObject(wbFile));	
			
			String includeLinks = request.getParameter("include_links");
			if (includeLinks != null && includeLinks.equals("1"))
			{
				List<WBUri> uris = adminStorage.query(WBUri.class, "resourceExternalKey", AdminQueryOperator.EQUAL, wbFile.getExternalKey());
				org.json.JSONArray arrayUris = jsonObjectConverter.JSONArrayFromListObjects(uris);
				org.json.JSONObject additionalData = new org.json.JSONObject();
				additionalData.put("uri_links", arrayUris);
				returnJson.put(ADDTIONAL_DATA, additionalData);			
			}

			httpServletToolbox.writeBodyResponseAsJson(response, returnJson, null);
			
		} catch (Exception e)		
		{
			Map<String, String> errors = new HashMap<String, String>();		
			errors.put("", WBErrors.WB_CANT_GET_RECORDS);
			httpServletToolbox.writeBodyResponseAsJson(response, jsonObjectConverter.JSONObjectFromMap(null), errors);		
		}		
	}

	public void downloadResource(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBFile wbfile = adminStorage.get(key, WBFile.class);
			WBCloudFile cloudFile = new WBCloudFile("public", wbfile.getBlobKey());
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

	public void serveResource(HttpServletRequest request, HttpServletResponse response, String requestUri) throws WBException
	{
		try
		{
			Long key = Long.valueOf((String)request.getAttribute("key"));
			WBFile wbfile = adminStorage.get(key, WBFile.class);
			WBCloudFile cloudFile = new WBCloudFile("public", wbfile.getBlobKey());
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
