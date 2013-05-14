package com.webbricks.datautility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.webbricks.cmsdata.WBArticle;
import com.webbricks.cmsdata.WBImage;
import com.webbricks.cmsdata.WBMessage;
import com.webbricks.cmsdata.WBParameter;
import com.webbricks.cmsdata.WBUri;
import com.webbricks.cmsdata.WBWebPage;
import com.webbricks.cmsdata.WBWebPageModule;
import com.webbricks.exception.WBIOException;

public class DataStoreImporterExporter {
	public static final String URI_ENTRY = "wburi.json";
	public static final String PAGES_ENTRY = "wbpages.json";
	public static final String PAGE_MODULES_ENTRY = "wbmodules.json";
	public static final String PARAMETERS_ENTRY = "wbparameters.json";
	public static final String SETTINGS_ENTRY = "wbsettings.json";
	public static final String IMAGES_ENTRY = "wbimages.json";
	public static final String ARTICLES_ENTRY = "wbarticles.json";
	public static final String MESSAGES_ENTRY = "wbmessages.json";
	
	protected WBJSONToFromObjectConverter objectConverter;
	
	public DataStoreImporterExporter()
	{
		objectConverter = new WBJSONToFromObjectConverter();		
	}
	
	protected String readFromStream(InputStream is) throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte array[] = new byte[4096]; 
		int len = 0;
		while ((len = is.read(array)) != -1) 
		{
			baos.write(array, 0, len);
		}
		return new String(baos.toByteArray(), "UTF-8");
	}
	
	protected void importUris(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBUri> records = objectConverter.listObjectsFromJSONString(buffer, WBUri.class);
			for(WBUri record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import uris " + e.getMessage());
		}
	}

	protected void importPages(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBWebPage> records = objectConverter.listObjectsFromJSONString(buffer, WBWebPage.class);
			for(WBWebPage record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import web pages " + e.getMessage());
		}
	}

	protected void importArticles(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBArticle> records = objectConverter.listObjectsFromJSONString(buffer, WBArticle.class);
			for(WBArticle record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import article " + e.getMessage());
		}
	}
	

	protected void importModules(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBWebPageModule> records = objectConverter.listObjectsFromJSONString(buffer, WBWebPageModule.class);
			for(WBWebPageModule record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import page modules " + e.getMessage());
		}
	}

	protected void importParameters(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBParameter> records = objectConverter.listObjectsFromJSONString(buffer, WBParameter.class);
			for(WBParameter record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import parameters " + e.getMessage());
		}
	}

	protected void importMessages(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBMessage> records = objectConverter.listObjectsFromJSONString(buffer, WBMessage.class);
			for(WBMessage record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import messages " + e.getMessage());
		}
	}

	protected void importImages(AdminDataStorage adminStorage, ZipInputStream zis) throws WBIOException
	{
		try
		{
			String buffer = readFromStream(zis);
			List<WBImage> records = objectConverter.listObjectsFromJSONString(buffer, WBImage.class);
			for(WBImage record: records)
			{
				record.setKey(null);
				adminStorage.add(record);
			}
		} catch (IOException e)
		{
			throw new WBIOException("cannot import parameters " + e.getMessage());
		}
	}

	
	protected void exportUris(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry ze = new ZipEntry(URI_ENTRY);
		try
		{
			zos.putNextEntry(ze);		
			List<WBUri> uriRecords = adminStorage.getAllRecords(WBUri.class);
			String uriRecordsString = objectConverter.JSONStringFromListObjects(uriRecords);		
			zos.write(uriRecordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export uris " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export uris " + e.getMessage());	
		}		
	}

	protected void exportPages(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry ze = new ZipEntry(PAGES_ENTRY);
		try
		{
			zos.putNextEntry(ze);		
			List<WBWebPage> records = adminStorage.getAllRecords(WBWebPage.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export pages " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export pages " + e.getMessage());	
		}		
	}

	protected void exportArticles(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry ze = new ZipEntry(ARTICLES_ENTRY);
		try
		{
			zos.putNextEntry(ze);		
			List<WBArticle> records = adminStorage.getAllRecords(WBArticle.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export articles " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export articles " + e.getMessage());	
		}		
	}

	protected void exportModules(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry ze = new ZipEntry(PAGE_MODULES_ENTRY);
		try
		{
			zos.putNextEntry(ze);		
			List<WBWebPageModule> records = adminStorage.getAllRecords(WBWebPageModule.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export page modules " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export page modules " + e.getMessage());	
		}		
	}
	
	protected void exportParameters(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry zeUri = new ZipEntry(PARAMETERS_ENTRY);
		try
		{
			zos.putNextEntry(zeUri);		
			List<WBParameter> records = adminStorage.getAllRecords(WBParameter.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export parameters " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export parameters " + e.getMessage());	
		}		
	}

	protected void exportMessages(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry zeMessages = new ZipEntry(MESSAGES_ENTRY);
		try
		{
			zos.putNextEntry(zeMessages);		
			List<WBMessage> records = adminStorage.getAllRecords(WBMessage.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export messages " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export messages " + e.getMessage());	
		}		
	}

	protected void exportImages(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		ZipEntry ze = new ZipEntry(IMAGES_ENTRY);
		try
		{
			zos.putNextEntry(ze);		
			List<WBImage> records = adminStorage.getAllRecords(WBImage.class);
			String recordsString = objectConverter.JSONStringFromListObjects(records);		
			zos.write(recordsString.getBytes("UTF-8"));
			zos.closeEntry();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export images " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw new WBIOException("cannot export images " + e.getMessage());	
		}		
	}
	
	protected void exportSettings(AdminDataStorage adminStorage, ZipOutputStream zos) throws WBIOException
	{
		
	}	

	public void exportToZip(AdminDataStorage adminStorage, OutputStream os) throws WBIOException
	{
		ZipOutputStream zos = new ZipOutputStream(os);
		
		try
		{
			exportUris(adminStorage, zos);
			exportPages(adminStorage, zos);
			exportModules(adminStorage, zos);
			exportArticles(adminStorage, zos);
			exportParameters(adminStorage, zos);
			exportImages(adminStorage, zos);
			exportMessages(adminStorage, zos);
			exportSettings(adminStorage, zos);
			
			zos.close();
		} catch (IOException e)
		{
			throw new WBIOException("cannot export project " + e.getMessage());
		}
		catch (WBIOException e)
		{
			throw e;
		}
	}

	public void importfromZip(AdminDataStorage adminStorage, InputStream is) throws WBIOException
	{
		try
		{
			ZipInputStream zis = new ZipInputStream(is);
			ZipEntry ze = null;
			while ((ze = zis.getNextEntry()) != null)
			{
				String name = ze.getName();
				if (name.compareTo(URI_ENTRY) == 0)
				{
					importUris(adminStorage, zis);
				} else if (name.compareTo(PAGES_ENTRY) == 0)
				{
					importPages(adminStorage, zis);
				} else if (name.compareTo(ARTICLES_ENTRY) == 0)
				{
					importArticles(adminStorage, zis);
				} else if (name.compareTo(PAGE_MODULES_ENTRY) == 0)
				{
					importModules(adminStorage, zis);
				} else if (name.compareTo(IMAGES_ENTRY) == 0)
				{
					importImages(adminStorage, zis);
				} else if (name.compareTo(PARAMETERS_ENTRY) == 0)
				{
					importParameters(adminStorage, zis);
				} else if (name.compareTo(MESSAGES_ENTRY) == 0)
				{
					importMessages(adminStorage, zis);
				}
				zis.closeEntry();
			}
			zis.close();
		} catch (IOException e)
		{
			throw new WBIOException("cannot import project " + e.getMessage());
		}
	}

}
