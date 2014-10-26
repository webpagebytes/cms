package com.webpagebytes.cms.datautility.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;
import javax.xml.bind.DatatypeConverter;
import org.apache.commons.io.IOUtils;

import com.webpagebytes.cms.datautility.WBCloudFile;
import com.webpagebytes.cms.datautility.WBCloudFileInfo;
import com.webpagebytes.cms.datautility.WBCloudFileStorage;
import com.webpagebytes.cms.datautility.WBDefaultCloudFileInfo;
import com.webpagebytes.cms.utility.WBBase64Utility;
import com.webpagebytes.cms.utility.WBConfiguration;
import com.webpagebytes.cms.utility.WBConfigurationFactory;
import com.webpagebytes.cms.utility.WBConfiguration.SECTION;

public class WBLocalCloudFileStorage implements WBCloudFileStorage {
	private static final String publicDataFolder = "public";
	private static final String privateDataFolder = "private";
	private static final String publicMetaFolder = "public_meta";
	private static final String privateMetaFolder = "private_meta";	
	private String dataDirectory;
	private String basePublicUrlPath;
	private boolean isInitialized;
	private static final Logger log = Logger.getLogger(WBLocalCloudFileStorage.class.getName());
	
	public WBLocalCloudFileStorage()
	{
		try
		{
			initialize(true);
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot initialize WBLocalCloudFileStorage for " + dataDirectory, e);
		}
	}
	
	public WBLocalCloudFileStorage(String dataDirectory, String basePublicUrlPath)	
	{
		
		this.dataDirectory = dataDirectory;
		
		if (!basePublicUrlPath.endsWith("/"))
		{
			basePublicUrlPath = basePublicUrlPath + "/";
		}
		this.basePublicUrlPath = basePublicUrlPath;
		try
		{
			initialize(false);
		} catch (Exception e)
		{
			// log the exception here
			log.log(Level.SEVERE, "Cannot initialize WBLocalCloudFileStorage for " + dataDirectory, e);
		}	
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
	
	private void initialize(boolean paramsFromConfig) throws IOException, FileNotFoundException
	{
		if (paramsFromConfig)
		{
			WBConfiguration config = WBConfigurationFactory.getConfiguration();
			Map<String, String> params = config.getSectionParams(SECTION.SECTION_FILESTORAGE);
		
			dataDirectory = params.get("dataDirectory");
			basePublicUrlPath = params.get("basePublicUrlPath");
		
			if (!basePublicUrlPath.endsWith("/"))
			{
				basePublicUrlPath = basePublicUrlPath + "/";
			}
		}
		
		log.log(Level.INFO, "Initialize for WBLocalCloudFileStorage with dir: " + dataDirectory);
		
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
		log.log(Level.INFO, "Create public dir for WBLocalCloudFileStorage " + publicDataDir.getAbsolutePath());
		File privateDataDir = new File (getPathPrivateDataDir());
		if (!privateDataDir.exists())
		{
			if (false == privateDataDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateDataDir.getPath());
			}
		}
		log.log(Level.INFO, "Create private dir for WBLocalCloudFileStorage " + privateDataDir.getAbsolutePath());
		
		File publicMetaDir = new File (getPathPublicMetaDir());
		if (!publicMetaDir.exists())
		{
			if (false == publicMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + publicMetaDir.getPath());
			}
		}
		log.log(Level.INFO, "Create public meta dir for WBLocalCloudFileStorage " + publicMetaDir.getAbsolutePath());
		
		File privateMetaDir = new File (getPathPrivateMetaDir());
		if (!privateMetaDir.exists())
		{
			if (false == privateMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateMetaDir.getPath());
			}
		}
		log.log(Level.INFO, "Create private meta dir for WBLocalCloudFileStorage " + privateMetaDir.getAbsolutePath());
		
		isInitialized = true;
	}
	/*
	 * sanitizeCloudFilePath will return a safe path that can be part of a file name
	 * The path will be converted to base64  
	 */
	private String sanitizeCloudFilePath(String path)
	{
		return WBBase64Utility.toBase64(path.getBytes(Charset.forName("UTF-8")));
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
			IOUtils.closeQuietly(fos);
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
	
	public String getPublicFileUrl(WBCloudFile file)	
	{
		String partialPath = file.getBucket() + "/" + sanitizeCloudFilePath(file.getPath());
		return basePublicUrlPath + partialPath;
	}
	
	public void clearContent(String bucket) throws IOException
	{
		
	}

}
