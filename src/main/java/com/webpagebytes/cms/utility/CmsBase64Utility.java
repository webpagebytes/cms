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

import javax.xml.bind.DatatypeConverter;

public class CmsBase64Utility {
	public static String toBase64(byte[] value)
	{
		return DatatypeConverter.printBase64Binary(value);
	}
	public static byte[] fromBase64(String value)
	{
		return DatatypeConverter.parseBase64Binary(value);
	}
	public static String toSafePathBase64(byte[] value)
	{
		// base 64 might contain '/' so we need to replace that with a char not from base 64 set and is path friendly (like '-')
		return toBase64(value).replace('/', '-');
	}
	public static byte[] fromSafePathBase64(String value)
	{
		// base 64 might contain '/' so we need to replace that with a char not from base 64 set and is path friendly (like '-')
		// this is the reverse operation
		return fromBase64(value.replace('-', '/'));
	}
}
