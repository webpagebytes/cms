package com.webbricks.datautility;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;


import java.util.Map;
import java.util.zip.CRC32;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreInputStream;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.webbricks.exception.WBIOException;

public class WBGaeBlobHandler implements WBBlobHandler {
	public static final String FILE_PARAMETER_NAME = "file";
	
	private BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	private ImagesService imageService = ImagesServiceFactory.getImagesService();
	private BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
	
	public String storeBlob(byte[] blobBytes)
	{
		return null;
	}
	public WBBlobInfo storeBlob(HttpServletRequest request) throws WBIOException
	{
		Map<java.lang.String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
		if (blobs.containsKey(FILE_PARAMETER_NAME))
		{
			List<BlobKey> fileBlob = blobs.get(FILE_PARAMETER_NAME);
			if (fileBlob.size() == 1)
			{
				// not very nice, to produce CRC from MD5 but it should provide a minimum implementation level
				BlobInfo bi = blobInfoFactory.loadBlobInfo(fileBlob.get(0));
				CRC32 crc = new CRC32();
				crc.update(bi.getMd5Hash().getBytes());
				if (bi != null)
				{
					return new WBBlobInfoDefault(bi.getBlobKey().getKeyString(), bi.getSize(), bi.getFilename(), bi.getContentType(), crc.getValue());					
				}
			}
		}
		return null;
	}
	
	public String getUploadUrl(String returnUrl)
	{
		return blobstoreService.createUploadUrl(returnUrl);
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
