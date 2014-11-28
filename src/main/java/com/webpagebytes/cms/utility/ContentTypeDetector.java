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

package com.webpagebytes.cms.utility;

import java.util.Map;
import java.util.HashMap;

public class ContentTypeDetector {
	
	private static Map<String, String> mapTypes = new HashMap<String, String>();
	public static final String DEFAULT_TYPE = "application/octet-stream";
	public static final String IMAGE_CONTENT_NAME = "image";
	public static final String VIDEO_CONTENT_NAME = "video";
	public static final String AUDIO_CONTENT_NAME = "audio";
	public static final String APP_CONTENT_NAME   = "application";

	static {
		mapTypes.put(".txt", "text/csv");
		mapTypes.put(".csv", "text/plain");
		mapTypes.put(".css", "text/css");
		mapTypes.put(".htm", "text/html");
		mapTypes.put(".html", "text/html");
		mapTypes.put(".js", "application/javascript");
		mapTypes.put(".png", "image/png");
		mapTypes.put(".bmp", "image/bmp");
		mapTypes.put(".jpg", "image/jpeg");
		mapTypes.put(".exe", "application/octet-stream");
		mapTypes.put(".bin", "application/octet-stream");
		mapTypes.put(".zip", "application/zip");
		mapTypes.put(".pdf", "application/pdf");
		mapTypes.put(".swf", "application/x-shockwave-flash");
		mapTypes.put(".7z", "application/x-7z-compressed");
		mapTypes.put(".apk", "application/vnd.android.package-archive");
		mapTypes.put(".avi", "video/x-msvideo");
		mapTypes.put(".bz", "application/x-bzip");
		mapTypes.put(".cer", "application/pkix-cert");
		mapTypes.put(".cab", "application/vnd.ms-cab-compressed");	
		mapTypes.put(".wmv", "video/x-ms-wmv");
		mapTypes.put(".mp4", "video/mp4");
	};
	
	public static String contentTypeToShortType(String contentType)
	{
		contentType = contentType.toLowerCase();
		if (contentType.startsWith(IMAGE_CONTENT_NAME))
		{
			return IMAGE_CONTENT_NAME;
		} else
		if (contentType.startsWith(VIDEO_CONTENT_NAME))
		{
			return VIDEO_CONTENT_NAME;
		} else
		if (contentType.startsWith(AUDIO_CONTENT_NAME))
		{
			return AUDIO_CONTENT_NAME;
		} else
		if (contentType.startsWith(APP_CONTENT_NAME))
		{
			return APP_CONTENT_NAME;
		}
		return APP_CONTENT_NAME;		
	}

	public static String fileNameToContentType(String fileName)
	{
		int index = fileName.lastIndexOf(".");
		if (index >=0) 
		{
			String name = fileName.substring(index).trim().toLowerCase();
			if (mapTypes.containsKey(name))
			{
				return mapTypes.get(name);
			}
		}
		return DEFAULT_TYPE;
	}
}
