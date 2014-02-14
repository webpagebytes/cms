package com.webbricks.datautility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


import java.util.Map;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.api.blobstore.UploadOptions;
import com.google.appengine.api.images.Image;
import com.google.appengine.api.images.Image.Format;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.Transform;

import java.nio.ByteBuffer;
import java.nio.channels.Channels;

import com.webbricks.exception.WBIOException;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsInputChannel;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;
import com.google.appengine.tools.cloudstorage.RetryParams;

public class WBGaeBlobHandler implements WBBlobHandler {
	
	private String publicBucketName = "webpagebytes_bucket";
	private String privateBucketName = "private_bucket";
	
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
		UploadOptions uploadOptions = UploadOptions.Builder.withGoogleStorageBucketName(publicBucketName);		
		return blobstoreService.createUploadUrl(returnUrl, uploadOptions);
	}

	public void deleteBlob(String blobKey) throws WBIOException
	{
		String path = "/gs/" + publicBucketName + "/" + blobKey;
		BlobKey key = blobstoreService.createGsBlobKey(path);
		blobstoreService.delete(key);
	}
	
	public void serveBlob(String blobKey, HttpServletResponse response) throws WBIOException
	{
		String path = "/gs/" + publicBucketName + "/" + blobKey;
		try
		{
			BlobKey key = blobstoreService.createGsBlobKey(path);
			blobstoreService.serve(key, response);
		} catch (Exception e)
		{
			throw new WBIOException("cannot serve: " + path, e);
		}
	}

	public void serveBlobAsImage(String blobKey, int imageWidth, HttpServletResponse response) throws WBIOException
	{
		GcsFilename fileName = new GcsFilename(publicBucketName, blobKey);		
		GcsInputChannel inputChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 4096);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		InputStream is = Channels.newInputStream(inputChannel);
		try
		{
			IOUtils.copy(is, bos);	
			Image image = ImagesServiceFactory.makeImage(bos.toByteArray());
			if (imageWidth>0)
			{
				Transform tr = ImagesServiceFactory.makeResize(imageWidth, imageWidth);
				image = imageService.applyTransform(tr, image);
			}
			response.getOutputStream().write(image.getImageData());
			Image.Format format = image.getFormat();
			if (format.equals(Image.Format.BMP))
			{
				response.setContentType("image/bmp");
			} else if (format.equals(Image.Format.PNG))
			{
				response.setContentType("image/png");
			} else if (format.equals(Image.Format.GIF))
			{
				response.setContentType("image/gif");
			} else if (format.equals(Image.Format.TIFF))
			{
				response.setContentType("image/tiff");
			} else if (format.equals(Image.Format.JPEG))
			{
				response.setContentType("image/jpeg");
			} else if (format.equals(Image.Format.ICO))
			{
				response.setContentType("image/ico");
			}
						
		} catch (IOException e)
		{
			throw new WBIOException("cannot resize " + blobKey + "to size " + String.valueOf(imageWidth), e);
		}
	}

	
	public WBBlobInfo storeBlob(InputStream is, boolean storeAsPublic, String contentType) throws WBIOException
	{
		String objectName = java.util.UUID.randomUUID().toString();
		
		GcsFilename fileName = null;
		if (storeAsPublic)
		{
			fileName = new GcsFilename(publicBucketName, objectName);
		} else {
			fileName = new GcsFilename(privateBucketName, objectName);
		}
		long fileSize = 0L;
		CRC32 crc = new CRC32();
		
		GcsOutputChannel outputChannel = null;
		OutputStream os = null;
		try
		{
			outputChannel = gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			os = Channels.newOutputStream(outputChannel);
			byte[] buffer = new byte[10*4096];
			int len = 0;
			while ( (len = is.read(buffer)) != -1)
			{
				fileSize += len;
				os.write(buffer, 0, len);
				crc.update(buffer, 0, len);		
			}
		} catch (IOException e)
		{
			throw new WBIOException("Cannot write into cloud storage", e);
		}
		finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(outputChannel);
		}
		
		return new WBBlobInfoDefault(objectName, fileSize, objectName, contentType, crc.getValue(), "");
	}

	public WBBlobInfo storeBlob(InputStream is) throws WBIOException
	{
		String objectName = java.util.UUID.randomUUID().toString();
		GcsFilename fileName = new GcsFilename(publicBucketName, objectName);
		long fileSize = 0L;
		CRC32 crc = new CRC32();
		
		GcsOutputChannel outputChannel = null;
		OutputStream os = null;
		try
		{
			outputChannel = gcsService.createOrReplace(fileName, GcsFileOptions.getDefaultInstance());
			os = Channels.newOutputStream(outputChannel);
			byte[] buffer = new byte[10*4096];
			int len = 0;
			while ( (len = is.read(buffer)) != -1)
			{
				fileSize += len;
				os.write(buffer, 0, len);
				crc.update(buffer, 0, len);		
			}
		} catch (IOException e)
		{
			throw new WBIOException("Cannot write into cloud storage", e);
		}
		finally {
			IOUtils.closeQuietly(os);
			IOUtils.closeQuietly(outputChannel);
		}
		return new WBBlobInfoDefault(objectName, fileSize, "", "", crc.getValue(), "");
	}
	
	public InputStream getBlobData(String blobKey) throws WBIOException
	{
			GcsFilename fileName = new GcsFilename(publicBucketName, blobKey);
			
			GcsInputChannel inputChannel = gcsService.openPrefetchingReadChannel(fileName, 0, 100*1024);
			
			return Channels.newInputStream(inputChannel);
	}
	
}
