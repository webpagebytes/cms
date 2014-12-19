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

package com.webpagebytes.cms.local;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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

import com.webpagebytes.cms.WPBFilePath;
import com.webpagebytes.cms.WPBFileStorage;
import com.webpagebytes.cms.cmsdata.WPBFileInfo;
import com.webpagebytes.cms.engine.WPBDefaultCloudFileInfo;
import com.webpagebytes.cms.utility.CmsBase64Utility;
import com.webpagebytes.cms.utility.CmsConfiguration;
import com.webpagebytes.cms.utility.CmsConfigurationFactory;
import com.webpagebytes.cms.utility.CmsConfiguration.WPBSECTION;

public class WPBLocalCloudFileStorage implements WPBFileStorage {
	private static final String publicDataFolder = "public";
	private static final String privateDataFolder = "private";
	private static final String publicMetaFolder = "public_meta";
	private static final String privateMetaFolder = "private_meta";	
	private String dataDirectory;
	private String basePublicUrlPath;
	private boolean isInitialized;
	private static final Logger log = Logger.getLogger(WPBLocalCloudFileStorage.class.getName());
	
	public WPBLocalCloudFileStorage()
	{
		try
		{
			initialize(true);
		} catch (Exception e)
		{
			log.log(Level.SEVERE, "Cannot initialize WBLocalCloudFileStorage for " + dataDirectory, e);
		}
	}
	
	public WPBLocalCloudFileStorage(String dataDirectory, String basePublicUrlPath)	
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
	
	private void initializeFileStorage(String dataDirectory) throws IOException, FileNotFoundException
	{
		File fileBaseData = new File(dataDirectory);
		if (!fileBaseData.exists() || !fileBaseData.isDirectory())
		{
			throw new FileNotFoundException();
		}
		
		File publicDataDir = new File (getPathPublicDataDir());
		if (!publicDataDir.exists())
		{
			log.log(Level.INFO, "Create public dir for WBLocalCloudFileStorage " + publicDataDir.getAbsolutePath());
			if (false == publicDataDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + publicDataDir.getPath());
			}
		}
		
		File privateDataDir = new File (getPathPrivateDataDir());
		if (!privateDataDir.exists())
		{
			log.log(Level.INFO, "Create private dir for WBLocalCloudFileStorage " + privateDataDir.getAbsolutePath());
			if (false == privateDataDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateDataDir.getPath());
			}
		}
		
		File publicMetaDir = new File (getPathPublicMetaDir());
		if (!publicMetaDir.exists())
		{
			log.log(Level.INFO, "Create public meta dir for WBLocalCloudFileStorage " + publicMetaDir.getAbsolutePath());
			if (false == publicMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + publicMetaDir.getPath());
			}
		}
		
		File privateMetaDir = new File (getPathPrivateMetaDir());
		if (!privateMetaDir.exists())
		{
			log.log(Level.INFO, "Create private meta dir for WBLocalCloudFileStorage " + privateMetaDir.getAbsolutePath());
			if (false == privateMetaDir.mkdir())
			{
				throw new IOException("Cannot create dir: " + privateMetaDir.getPath());
			}
		}
		
		isInitialized = true;

	}
	
	private void initialize(boolean paramsFromConfig) throws IOException, FileNotFoundException
	{
		if (paramsFromConfig)
		{
			CmsConfiguration config = CmsConfigurationFactory.getConfiguration();
			Map<String, String> params = config.getSectionParams(WPBSECTION.SECTION_FILESTORAGE);
		
			dataDirectory = params.get("dataDirectory");
			basePublicUrlPath = params.get("basePublicUrlPath");
		
			if (!basePublicUrlPath.endsWith("/"))
			{
				basePublicUrlPath = basePublicUrlPath + "/";
			}
		}
		
		log.log(Level.INFO, "Initialize for WBLocalCloudFileStorage with dir: " + dataDirectory);
		
		initializeFileStorage(dataDirectory);
	}
	/*
	 * sanitizeCloudFilePath will return a safe path that can be part of a file name
	 * The path will be converted to base64  
	 */
	private String sanitizeCloudFilePath(String path)
	{
		return CmsBase64Utility.toBase64(path.getBytes(Charset.forName("UTF-8")));
	}
	
	private String getLocalFullDataPath(WPBFilePath file)
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
	private String getLocalFullMetaPath(WPBFilePath file)
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
	
	private OutputStream createStorageOutputStream(String path) throws IOException
	{
		if (new File(path).exists())
		{
			throw new IOException("file already exists");
		}
		return new FileOutputStream(path);
	}

	private InputStream createStorageInputStream(String path) throws IOException
	{
	
		return new FileInputStream(path);
	}

	
	private void storeFileProperties(Properties props, String filePath) throws IOException 
	{
		FileOutputStream os = new FileOutputStream(filePath);
		props.storeToXML(os, "", "UTF-8");
		IOUtils.closeQuietly(os);
	}
	
	private Properties getFileProperties(String filePath) throws IOException
	{
		Properties props = new Properties();
		FileInputStream fis = null;
		fis = new FileInputStream(filePath);
		props.loadFromXML(fis);
		return props;
	}
	
	private boolean checkIfFileExists(String filePath) throws IOException 
	{
		return new File(filePath).exists();
	}
	
	public void storeFile(InputStream is, WPBFilePath file) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		OutputStream fos = createStorageOutputStream(fullFilePath);
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
		
		Properties props = new Properties();
		props.put("path", file.getPath());
		props.put("contentType", "application/octet-stream");
		props.put("crc32", String.valueOf(crc.getValue()));
		props.put("md5", DatatypeConverter.printBase64Binary(md.digest()));
		props.put("creationTime", String.valueOf(Calendar.getInstance(TimeZone.getTimeZone("GMT")).getTime().getTime()));
		props.put("size", String.valueOf(size));
		
		String metaPath = getLocalFullMetaPath(file);
		storeFileProperties(props, metaPath);
		
	}
	
	public WPBFileInfo getFileInfo(WPBFilePath file) 
	{
		String metaPath = getLocalFullMetaPath(file);
		String dataPath = getLocalFullDataPath(file);
		Properties props = new Properties();
		try
		{
			props = getFileProperties(metaPath);
			
			String contentType = props.getProperty("contentType");
			int size = Integer.valueOf(props.getProperty("size"));
			String md5 = props.getProperty("md5");
			long crc32 = Long.valueOf(props.getProperty("crc32"));
			long creationTime = Long.valueOf(props.getProperty("creationTime"));
			
			
			boolean fileExists = checkIfFileExists(dataPath);
			WPBFileInfo fileInfo = new WPBDefaultCloudFileInfo(file, contentType, fileExists, size, md5, crc32, creationTime);
			props.remove("path");
			props.remove("contentType");
			props.remove("size");
			props.remove("md5");
			props.remove("crc32");
			props.remove("creationTime");
			props.remove("filePath");
			// add the custom properties of the file
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
	
	public boolean deleteFile(WPBFilePath file) throws IOException
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
	public InputStream getFileContent(WPBFilePath file) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! checkIfFileExists(fullFilePath))
		{
			throw new IOException("file does not exists");
		}
		return createStorageInputStream(fullFilePath);
	}
	
	
	public void updateContentType(WPBFilePath file, String contentType) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! checkIfFileExists(fullFilePath))
		{
			throw new IOException("file does not exists");
		}
		
		String metaPath = getLocalFullMetaPath(file);
		Properties props = getFileProperties(metaPath);

		props.put("contentType", contentType);
		
		storeFileProperties(props, metaPath);
	}
	
	public void updateFileCustomProperties(WPBFilePath file, Map<String, String> customProps) throws IOException
	{
		String fullFilePath = getLocalFullDataPath(file);
		if (! checkIfFileExists(fullFilePath))
		{
			throw new IOException("file does not already exists");
		}
		
		String metaPath = getLocalFullMetaPath(file);
		Properties props = getFileProperties(metaPath);

		customProps.remove("path");
		customProps.remove("contentType");
		customProps.remove("size");
		customProps.remove("md5");
		customProps.remove("crc32");
		customProps.remove("creationTime");
		customProps.remove("filePath");
		
		props.putAll(customProps);
		
	    storeFileProperties(props, metaPath);
	}
	
	public String getPublicFileUrl(WPBFilePath file)	
	{
		String partialPath = file.getBucket() + "/" + sanitizeCloudFilePath(file.getPath());
		return basePublicUrlPath + partialPath;
	}
	

}
