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

import javax.servlet.http.HttpServlet;

public class WPBServletUtility {

	public String getInitParameter(String name, HttpServlet servlet)
	{
		return servlet.getInitParameter(name);
	}
	public String getContextParameter(String name, HttpServlet servlet)
	{
		return servlet.getServletContext().getInitParameter(name);
	}
	public String getContextPath(HttpServlet servlet)
	{
		return servlet.getServletContext().getContextPath();
	}
}
