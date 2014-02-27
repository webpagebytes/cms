package com.webbricks.datautility;

import java.io.InputStream;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.webbricks.exception.WBIOException;

public interface WBBlobHandler {

	public WBBlobInfo storeBlob(HttpServletRequest request) throws WBIOException;
	public String getUploadUrl(String returnUrl);
	public void deleteBlob(String blobKey) throws WBIOException;
	public void serveBlob(String blobKey, HttpServletResponse response) throws WBIOException;
	public void serveBlobAsImage(String blobKey, int imageWidth, HttpServletResponse response) throws WBIOException;
	public InputStream getBlobData(String blobKey) throws WBIOException;
	public WBBlobInfo storeBlob(InputStream is) throws WBIOException;

}
