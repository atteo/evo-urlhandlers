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
package org.atteo.evo.urlhandlers;

import java.net.URLStreamHandler;

import org.atteo.evo.classindex.ClassIndex;

import net.sf.cglib.core.NamingPolicy;
import net.sf.cglib.core.Predicate;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.NoOp;

public class UrlHandlers {
	public static final String KEY = "java.protocol.handler.pkgs";
	public static final String PACKAGE_NAME = UrlHandlers.class.getPackage().getName() + ".handlers";

	public static void initialize() {
		synchronized (System.getProperties()) {
			if (System.getProperties().contains(KEY)) {
				String current = System.getProperty(KEY);
				if (!current.contains(PACKAGE_NAME)) {
					System.setProperty(KEY, current + "|" + PACKAGE_NAME);
				}
			} else {
				System.setProperty(KEY, PACKAGE_NAME);
			}
		}

	}

	public static void registerUrlHandler(final String protocol, Class<? extends URLStreamHandler> klass) {
		initialize();

        Enhancer e = new Enhancer();
        e.setNamingPolicy(new NamingPolicy() {
            @Override
            public String getClassName(String prefix, String source, Object key, Predicate names)
            {
                return PACKAGE_NAME + "." + protocol + ".Handler";
            }
        });
        e.setSuperclass(klass);
        e.setCallbackType(NoOp.class);
        e.createClass();
	}

	public static void registerAnnotatedHandlers() {
		for (Class<?> klass : ClassIndex.getAnnotated(UrlHandler.class)) {
			if (!(URLStreamHandler.class.isAssignableFrom(klass))) {
				throw new RuntimeException("Classes marked with @" + UrlHandler.class.getSimpleName() + " annotation"
						+ " must extend " + URLStreamHandler.class.getCanonicalName());
			}
			registerUrlHandler(klass.getAnnotation(UrlHandler.class).value(),
					(Class<? extends URLStreamHandler>) klass);
		}
	}
}
