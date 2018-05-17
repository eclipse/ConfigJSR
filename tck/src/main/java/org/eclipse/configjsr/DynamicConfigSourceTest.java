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
package org.eclipse.configjsr;

import java.util.concurrent.TimeUnit;

import javax.config.Config;
import javax.config.ConfigAccessor;
import javax.config.spi.ConfigSource;
import javax.inject.Inject;

import org.eclipse.configjsr.dynamic.DynamicChangeConfigSource;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 *
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class DynamicConfigSourceTest extends Arquillian {

    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
                .create(JavaArchive.class, "dynamicValuesTest.jar")
                .addClass(DynamicConfigSourceTest.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsServiceProvider(ConfigSource.class, DynamicChangeConfigSource.class)
                .as(JavaArchive.class);


        WebArchive war = ShrinkWrap
                .create(WebArchive.class, "dynamicValuesTest.war")
                .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testBgCount() throws Exception {
        Integer value = config.getValue(DynamicChangeConfigSource.TEST_ATTRIBUTE, Integer.class);
        Thread.sleep(25L);
        Integer value2 = config.getValue(DynamicChangeConfigSource.TEST_ATTRIBUTE, Integer.class);
        assertTrue(value2 > value);
    }

    @Test
    public void testValueInvalidationOnConfigChange() throws Exception {
        ConfigAccessor<Integer> valCfg = config.access(DynamicChangeConfigSource.TEST_ATTRIBUTE)
            .as(Integer.class)
            .cacheFor(15, TimeUnit.MINUTES);

        // we try to read the same value for 30 consecutive times
        // it might happen that we have bad luck and the configsource switches values
        // just as we do this. In which case we need to start over again.
        //
        boolean restarted = false;
        Integer initialValue = valCfg.getValue();
        for (int i = 0; i< 30; i++) {
            if (initialValue != valCfg.getValue()) {
                if (restarted) {
                    Assert.fail("Value got changed mulitple times. This must not happen!");
                }
                else {
                    i = 0;
                    initialValue = valCfg.getValue();
                    restarted = true;
                }
            }
        }

        Thread.sleep(25L);
        assertTrue(valCfg.getValue() > initialValue);
    }

}
