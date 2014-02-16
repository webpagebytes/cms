package com.webbricks.datautility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.zip.CRC32;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.io.IOUtils;

public class WBLocalCloudFileStorage implements WBCloudFileStorage {
	private static final String publicDataFolder = "public";
	private static final String privateDataFolder = "private";
	private static final String publicMetaFolder = "public_meta";
	private static final String privateMetaFolder = "private_meta";	
	private String dataDirectory;
	private String basePublicUrlPath;
	private boolean isInitialized;
	
	public WBLocalCloudFileStorage(String dataDirectory,
								   String basePublicUrlPath)
	{
		this.dataDirectory = dataDirectory;
		this.basePublicUrlPath = basePublicUrlPath;
		isInitialized = false;
	}
	public String getDataDir()
	{
		return dataDirectory;
	}
	public boolean isInitialized()
	{
		return isInitialized;
	}
	private String getPathPublicDataDir()
	{
		return dataDirectory + File.separator + publicDataFolder;
	}
	private String getPathPrivateDataDir()
	{
		return dataDirectory +  File.separator + privateDataFolder;
	}
	private String getPathPublicMetaDir()
	{
		return dataDirectory +  File.separator + publicMetaFolder;
	}
	private String getPathPrivateMetaDir()
	{
		return dataDirectory +  File.separator + privateMetaFolder;
	}
	
	public void initialize() throws IOException, FileNotFoundException
	{
		isInitialized = false;
		//check to see that the dataDirectory exists
		File fileBaseData = new File(dataDirectory);
		if (!fileBaseData.exists() || !fileBaseData.isDirectory())
		{
			throw new FileNotFoundException();
		}
		File publicDataDir = new File (getPathPublicDataDir());
		if (!publicDataDir.exists())
		{
			if (false == publicDataDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + publicDataDir.getPath());
			}
		}
		File privateDataDir = new File (getPathPrivateDataDir());
		if (!privateDataDir.exists())
		{
			if (false == privateDataDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateDataDir.getPath());
			}
		}
		File publicMetaDir = new File (getPathPublicMetaDir());
		if (!publicMetaDir.exists())
		{
			if (false == publicMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + publicMetaDir.getPath());
			}
		}
		File privateMetaDir = new File (getPathPrivateMetaDir());
		if (!privateMetaDir.exists())
		{
			if (false == privateMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateMetaDir.getPath());
			}
		}
		isInitialized = true;
	}
	/*
	 * sanitizeCloudFilePath will return a safe path that can be part of a file name
	 * The path will be converted to base64  
	 */
	private String sanitizeCloudFilePath(String path)
	{
		return DatatypeConverter.printBase64Binary(path.getBytes());
	}
	
	private String getLocalFullDataPath(WBCloudFile file)
	{
		if (file.getBucket().equals(publicDataFolder)) 
		{
			return getPathPublicDataDir() +  File.separator +  sanitizeCloudFilePath(file.getPath());
		}
		if (file.getBucket().equals(privateDataFolder)) 
		{
			return getPathPrivateDataDir() +  File.separator +  sanitizeCloudFilePath(file.getPath());
		}
		return null;
	}
	private String getLocalFullMetaPath(WBCloudFile file)
	{
		if (file.getBucket().equals(publicDataFolder)) 
		{
			return getPathPublicMetaDir() +  File.separator +  sanitizeCloudFilePath(file.getPath());
		}
		if (file.getBucket().equals(privateDataFolder)) 
		{
			return getPathPrivateMetaDir() +  File.separator +  sanitizeCloudFilePath(file.getPath());
		}
		return null;
	}

	public void storeFile(InputStream is, WBCloudFile file) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (new File(fullFilePath).exists())
		{
			throw new IOException("file already exists");
		}
		FileOutputStream fos = new FileOutputStream(fullFilePath);
		byte[] buffer = new byte[4096];
		CRC32 crc = new CRC32();
		MessageDigest md = null;
		try 
		{
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e)
		{
			throw new IOException("Cannot calculate md5 to store the file", e);
		}

		int count = 0;
		int size = 0;
		while ((count = is.read(buffer)) != -1)
		{
			size += count;
			fos.write(buffer, 0, count);
			crc.update(buffer, 0, count);
			md.update(buffer, 0, count);
		}
		
		IOUtils.closeQuietly(is);
		IOUtils.closeQuietly(fos);
		
		String metaPath = getLocalFullMetaPath(file);
		Properties props = new Properties();
		props.put("path", file.getPath());
		props.put("contentType", "application/octet-stream");
		props.put("crc32", String.valueOf(crc.getValue()));
		props.put("md5", DatatypeConverter.printBase64Binary(md.digest()));
		props.put("creationTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().getTime()));
		props.put("size", String.valueOf(size));
		
		FileOutputStream os = new FileOutputStream(metaPath);
		props.storeToXML(os, "", "UTF-8");
		IOUtils.closeQuietly(os);
		
	}
	public WBCloudFileInfo getFileInfo(WBCloudFile file) 
	{
		String metaPath = getLocalFullMetaPath(file);
		String dataPath = getLocalFullDataPath(file);
		File metaFile = new File(metaPath);
		Properties props = new Properties();
		FileInputStream fis = null;
		try
		{
			fis = new FileInputStream(metaFile);
			props.loadFromXML(fis);
			String contentType = props.getProperty("contentType");
			int size = Integer.valueOf(props.getProperty("size"));
			String md5 = props.getProperty("md5");
			long crc32 = Long.valueOf(props.getProperty("crc32"));
			long creationTime = Long.valueOf(props.getProperty("creationTime"));
			
			
			boolean fileExists = new File(dataPath).exists();
			WBCloudFileInfo fileInfo = new WBDefaultCloudFileInfo(file, contentType, fileExists, size, md5, crc32, creationTime);
			props.remove("path");
			props.remove("contentType");
			props.remove("size");
			props.remove("md5");
			props.remove("crc32");
			props.remove("creationTime");
			
			for(Object key : props.keySet())
			{
				String strKey = (String) key;
				fileInfo.setProperty(strKey, props.getProperty(strKey));
			}
			return fileInfo;
		} catch (Exception e)
		{
			return null;
		}
		
	}
	
	public boolean deleteFile(WBCloudFile file) throws IOException
	{
		String filePath = getLocalFullDataPath(file);
		File dataFile = new File(filePath);
		boolean del1 = true;
		if (dataFile.exists())
		{
			del1 = dataFile.delete();
		}
		String metaPath = getLocalFullMetaPath(file);
		File metaFile = new File(metaPath);
		boolean del2 = true;
		if (metaFile.exists())
		{
			del2 = metaFile.delete();
		}
		return del1 && del2;
		
	}
	public InputStream getFileContent(WBCloudFile file) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! new File(fullFilePath).exists())
		{
			throw new IOException("file does not exists");
		}
		return new FileInputStream(fullFilePath);
	}
	
	public byte[] getFileContent(WBCloudFile file, int startIndex, int endIndex) throws IOException
	{
		return null;
	}
	
	public void updateContentType(WBCloudFile file, String contentType) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! new File(fullFilePath).exists())
		{
			throw new IOException("file does not exists");
		}
		
		String metaPath = getLocalFullMetaPath(file);
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(metaPath));

		props.put("contentType", contentType);
		
		FileOutputStream os = new FileOutputStream(metaPath);
		props.storeToXML(os, "", "UTF-8");
		IOUtils.closeQuietly(os);				
	}
	
	public void updateFileCustomProperties(WBCloudFile file, Map<String, String> customProps) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! new File(fullFilePath).exists())
		{
			throw new IOException("file does not already exists");
		}
		
		String metaPath = getLocalFullMetaPath(file);
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(metaPath));

		customProps.remove("path");
		customProps.remove("contentType");
		customProps.remove("size");
		customProps.remove("md5");
		customProps.remove("crc32");
		customProps.remove("creationTime");
		
		props.putAll(customProps);
		
		FileOutputStream os = new FileOutputStream(metaPath);
		props.storeToXML(os, "", "UTF-8");
		IOUtils.closeQuietly(os);		
	}
	
	public void getPublicFileUrl(WBCloudFile file)	
	{
		
	}

}
