/*
 * Copyright (c) 2016-2019 Contributors to the Eclipse Foundation
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
package org.eclipse.configjsr;

import jakarta.config.spi.ConfigProviderResolver;
import javax.inject.Inject;

import jakarta.config.Config;
import jakarta.config.spi.ConfigSource;
import jakarta.config.spi.ConfigSourceProvider;
import org.eclipse.configjsr.configsources.CustomConfigSourceProvider;
import org.eclipse.configjsr.configsources.CustomDbConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import static org.eclipse.configjsr.base.AbstractTest.addFile;
import static org.testng.Assert.assertEquals;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class CustomConfigSourceTest extends Arquillian {

    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "customConfigSourceTest.jar")
                .addClasses(CustomConfigSourceTest.class, CustomDbConfigSource.class, CustomConfigSourceProvider.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(ConfigSource.class, CustomDbConfigSource.class)
                .addAsServiceProvider(ConfigSourceProvider.class, CustomConfigSourceProvider.class)
                .as(JavaArchive.class);

        addFile(testJar, "META-INF/javaconfig.properties");

        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "customConfigSourceTest.war")
                .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testConfigSourceProvider() {
        assertEquals(config.getValue("tck.config.test.customDbConfig.key1", String.class), "valueFromDb1");
    }

    @Test
    public void testConfigSourceAutoClose() {
        CustomDbConfigSource customDbConfigSource = new CustomDbConfigSource();

        Assert.assertEquals(customDbConfigSource.getCloseCounter(), 0);

        Config config = ConfigProviderResolver.instance().getBuilder()
            .withSources(customDbConfigSource)
            .build();

        // just to trigger the config
        config.getOptionalValue("somekey", String.class);

        ConfigProviderResolver.instance().releaseConfig(config);

        Assert.assertEquals(customDbConfigSource.getCloseCounter(), 1);
    }
}
