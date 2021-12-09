/*
 * Copyright (c) 2016-2022 Contributors to the Eclipse Foundation
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

import javax.inject.Inject;

import org.eclipse.configjsr.base.AbstractTest;
import org.eclipse.configjsr.configsources.ConfigurableConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import jakarta.config.Config;
import jakarta.config.inject.ConfigProperty;
import jakarta.config.spi.ConfigSource;

/**
 * Test to check Pojo access.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class PojoConfigTest extends Arquillian {


    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "pojoConfigTest.jar")
            .addPackage(AbstractTest.class.getPackage())
            .addClass(ConfigAccessorTest.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsServiceProvider(ConfigSource.class, ConfigurableConfigSource.class)
            .as(JavaArchive.class);

        AbstractTest.addFile(testJar, "META-INF/jakartaconfig.properties");

        WebArchive war = ShrinkWrap
            .create(WebArchive.class, "configValueTest.war")
            .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testBasicPojoAccess() {
        final ServerEndpointPojoWithCt endpoint = config.access("tck.config.test.jakartaconfig.some.server", ServerEndpointPojoWithCt.class)
            .build()
            .getValue();
        Assert.assertNotNull(endpoint);
    }



    @Test
    public void testPojoAccessWithBeanConverter() {
        final ServerEndpointPojoWithCt endpoint = config.access("tck.config.test.jakartaconfig.some.server", ServerEndpointPojoWithCt.class)
            .withBeanConverter((cfg, key) -> new ServerEndpointPojoWithCt(
               cfg.getValue(key + "host", String.class),
               cfg.getValue(key + "port", Integer.class),
               cfg.getValue(key + "path", String.class)))
            .build()
            .getValue();
        Assert.assertNotNull(endpoint);
    }

    public static class ServerEndpointPojoWithCt {
        private String host;
        private Integer port;
        private String path;

        public ServerEndpointPojoWithCt(@ConfigProperty(name = "host") String host,
                                        @ConfigProperty(name = "port") Integer port,
                                        @ConfigProperty(name = "path") String path) {
            this.host = host;
            this.port = port;
            this.path = path;
        }

        public String getHost() {
            return host;
        }

        public Integer getPort() {
            return port;
        }

        public String getPath() {
            return path;
        }
    }
}
