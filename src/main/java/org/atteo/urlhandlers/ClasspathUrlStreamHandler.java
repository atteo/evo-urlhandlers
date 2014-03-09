/*
 * Copyright 2012 Atteo.
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
package org.atteo.urlhandlers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

@UrlHandler("classpath")
public class ClasspathUrlStreamHandler extends URLStreamHandler {
	@Override
	protected URLConnection openConnection(URL url) throws IOException {
		URL resourceUrl = Thread.currentThread().getContextClassLoader().getResource(url.getPath());

		if (resourceUrl == null) {
			resourceUrl = ClasspathUrlStreamHandler.class.getClassLoader().getResource(url.getPath());

			if (resourceUrl == null) {
				throw new FileNotFoundException("Resorce not found on classpath: " + url.getPath());
			}
		}

		return resourceUrl.openConnection();
	}
}
