/*
 * Copyright (c) 2016-2018 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.eclipse.configjsr.base;

import java.net.URL;

import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.asset.UrlAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;


/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class AbstractTest extends Arquillian {


    public static void addFile(JavaArchive archive, String originalPath) {
        String resName = "internal/" + originalPath;
        URL resource = Thread.currentThread().getContextClassLoader().getResource(resName);
        if (resource == null) {
            throw new IllegalStateException("could not load test resource " + resName);
        }
        archive.addAsResource(new UrlAsset(resource),
                originalPath);
    }

    public static void addFile(JavaArchive archive, String originalFile, String targetFile) {
        URL resource = Thread.currentThread().getContextClassLoader().getResource(originalFile);
        if (resource == null) {
            throw new IllegalStateException("could not load test resource " + originalFile);
        }
        archive.addAsResource(new UrlAsset(resource),
                targetFile);
    }

}
