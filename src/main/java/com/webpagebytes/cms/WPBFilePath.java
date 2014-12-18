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

package com.webpagebytes.cms;

/**
 * WPBFilePath represents an file location based on bucket and a path relative to that bucket.
 */
public class WPBFilePath {
	private String bucket;
	private String path;
	
	/**
	 * Contructor that takes bucket and path parameters.
	 * @param bucket Storage bucket
	 * @param path Path relative to the bucket
	 */
	public WPBFilePath(String bucket, String path)
	{
		this.bucket = bucket;
		this.path = path;
	}
	/**
	 * Returns the file path
	 * @return Returns the file path
	 */
	public String getPath()
	{
		return path;
	}
	/**
	 * Returns the file bucket
	 * @return Returns the file bucket
	 */
	public String getBucket()
	{
		return bucket;
	}

}
