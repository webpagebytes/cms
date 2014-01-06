package com.webbricks.datautility;

import java.io.IOException;

import java.io.InputStream;
import java.util.List;


import java.util.Map;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.webbricks.exception.WBIOException;

public class WBGaeBlobHandler implements WBBlobHandler {
	
	private String bucketName = "webpagebytes_bucket";
	public static final String FILE_PARAMETER_NAME = "file";
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imageService = ImagesServiceFactory.getImagesService();
	
	public WBBlobInfo storeBlob(HttpServletRequest request) throws WBIOException
	{
		Map<java.lang.String, List<FileInfo>> fileInfos = blobstoreService.getFileInfos(request);
		if (fileInfos.containsKey(FILE_PARAMETER_NAME))
		{
			List<FileInfo> fileInfoList = fileInfos.get(FILE_PARAMETER_NAME);
			if (fileInfoList.size() == 1)
			{
				FileInfo fileInfo = fileInfoList.get(0);
				CRC32 crc = new CRC32();
				crc.update(fileInfo.getMd5Hash().getBytes());
				BlobKey key = blobstoreService.getUploads(request).get(FILE_PARAMETER_NAME).get(0);
				return new WBBlobInfoDefault(key.getKeyString(), fileInfo.getSize(), fileInfo.getFilename(), fileInfo.getContentType(), crc.getValue(), fileInfo.getGsObjectName());					
			}
		}
		return null;
	}
	
	public String getUploadUrl(String returnUrl)
	{
		UploadOptions uploadOptions = UploadOptions.Builder.withGoogleStorageBucketName(bucketName);		
		return blobstoreService.createUploadUrl(returnUrl, uploadOptions);
	}

	public void deleteBlob(String blobKey) throws WBIOException
	{
		BlobKey key = new BlobKey(blobKey);
		blobstoreService.delete(key);
	}
	
	public void serveBlob(String blobKey, HttpServletResponse response) throws WBIOException
	{
		try
		{
		BlobKey key = new BlobKey(blobKey);
        blobstoreService.serve(key, response);
		} catch (Exception e)
		{
			//LOG exception
		}
	}

	public String serveBlobUrl(String blobKey, int imageSize) throws WBIOException
	{
		if (imageSize > 0)
		{
			return imageService.getServingUrl(new BlobKey(blobKey), imageSize, false);
		} 
		return imageService.getServingUrl(new BlobKey(blobKey)).concat("=s0");
	}
	
	public InputStream getBlobData(String blobKey) throws WBIOException
	{
		try
		{
			return new BlobstoreInputStream(new BlobKey (blobKey));
		} catch (IOException e)
		{
			throw new WBIOException("cannot get blob input stream " + e.getMessage());
		}
	}
	
}
