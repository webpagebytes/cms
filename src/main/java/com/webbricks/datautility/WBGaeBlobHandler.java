package com.webbricks.datautility;

import java.io.IOException;


import java.io.InputStream;
import java.io.OutputStream;
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

import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import com.webbricks.exception.WBIOException;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class WBGaeBlobHandler implements WBBlobHandler {
	
	private String bucketName = "webpagebytes_bucket";
	public static final String FILE_PARAMETER_NAME = "file";
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imageService = ImagesServiceFactory.getImagesService();
	private GcsService gcsService = GcsServiceFactory.createGcsService(new RetryParams.Builder()
    																	.initialRetryDelayMillis(10)
    																	.retryMaxAttempts(10)
    																	.totalRetryPeriodMillis(15000)
    																	.build());
	
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
	
	public WBBlobInfo storeBlob(InputStream is) throws WBIOException
	{
		String objectName = java.util.UUID.randomUUID().toString();
		GcsFilename fileName = new GcsFilename(bucketName, objectName);
		long fileSize = 0L;
		CRC32 crc = new CRC32();
		
		GcsOutputChannel outputChannel = null;
		OutputStream os = null;
		try
		{
			outputChannel = gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			os = Channels.newOutputStream(outputChannel);
			byte[] buffer = new byte[4096];
			int len = 0;
			while ( (len = is.read(buffer)) != -1)
			{
				fileSize += len;
				os.write(buffer, 0, len);
				crc.update(buffer, 0, len);
				
			}
			os.flush();
			os.close();
			outputChannel.close();
		} catch (IOException e)
		{
			throw new WBIOException("Cannot write into cloud storage", e);
		}
		String gsFileName = "/gs/" + bucketName + "/" + objectName;
		BlobKey blobKey = blobstoreService.createGsBlobKey(gsFileName);
		return new WBBlobInfoDefault(blobKey.getKeyString(), fileSize, "", "", crc.getValue(), gsFileName);
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
