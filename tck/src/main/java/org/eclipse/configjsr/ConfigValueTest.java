/*
 * Copyright (c) 2016-2017 Contributors to the Eclipse Foundation
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
 */
package org.eclipse.configjsr;

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

import javax.config.Config;
import javax.config.ConfigValue;
import javax.config.spi.ConfigSource;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConfigValueTest extends Arquillian {

    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "configValueTest.jar")
            .addPackage(AbstractTest.class.getPackage())
            .addClass(ConfigValueTest.class)
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .addAsServiceProvider(ConfigSource.class, ConfigurableConfigSource.class)
            .as(JavaArchive.class);

        AbstractTest.addFile(testJar, "META-INF/javaconfig.properties");

        WebArchive war = ShrinkWrap
            .create(WebArchive.class, "configValueTest.war")
            .addAsLibrary(testJar);
        return war;
    }


    @Test
    public void testGetValue() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.key1").getValue(), "value1");
    }

    @Test
    public void testGetValueWithDefault() {
        ConfigValue<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting")
                .as(Integer.class)
                .withDefault(Integer.valueOf(1234));

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testGetValueWithStringDefault() {
        ConfigValue<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting")
                .as(Integer.class)
                .withStringDefault("1234");

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testLookupChain() {
        // set the projectstage to 'Production'
        ConfigurableConfigSource.configure(config, "javaconfig.projectStage", "Production");

        /**
         * 1  1 -> com.foo.myapp.mycorp.Production
         * 1  0 -> com.foo.myapp.mycorp
         * 0  1 -> com.foo.myapp.Production
         * 0  0 -> com.foo.myapp
         *
         */
        ConfigValue<String> cv = config.access("com.foo.myapp")
            .withLookupChain("mycorp", "${javaconfig.projectStage}");

        Assert.assertFalse(cv.getOptionalValue().isPresent());

        ConfigurableConfigSource.configure(config, "com.foo.myapp", "TheDefault");
        Assert.assertEquals(cv.getValue(), "TheDefault");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.Production", "BasicWithProjectStage");
        Assert.assertEquals(cv.getValue(), "BasicWithProjectStage");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.mycorp", "WithTenant");
        Assert.assertEquals(cv.getValue(), "WithTenant");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.mycorp.Production", "WithTenantAndProjectStage");
        Assert.assertEquals(cv.getValue(), "WithTenantAndProjectStage");
    }

    @Test
    public void testIntegerConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.integer").as(Integer.class).getValue(),
            Integer.valueOf(1234));
    }

    @Test
    public void testLongConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.long").as(Long.class).getValue(),
            Long.valueOf(1234567890123456L));
    }

    @Test
    public void testFloatConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.float").as(Float.class).getValue(),
            Float.valueOf(12.34f));
    }

    @Test
    public void testDoubleonverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.double").as(Double.class).getValue(),
            Double.valueOf(12.34567890123456));
    }

    @Test
    public void testBooleanConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_uppercase").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_mixedcase").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.false").as(Boolean.class).getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.one").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.zero").as(Boolean.class).getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.seventeen").as(Boolean.class).getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_uppercase").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_mixedcase").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y_uppercase").as(Boolean.class).getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.no").as(Boolean.class).getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.no_mixedcase").as(Boolean.class).getValue(),
            Boolean.FALSE);

    }

    @Test
    public void testCacheFor() throws Exception {
        String key = "tck.config.test.javaconfig.cachefor.key";
        System.setProperty(key, "firstvalue");
        ConfigValue<String> val = config.access(key).cacheFor(30, TimeUnit.MILLISECONDS);
        Assert.assertEquals(val.getValue(), "firstvalue");

        // immediately change the value
        System.setProperty(key, "secondvalue");

        // we should still see the first value, because it is cached!
        Assert.assertEquals(val.getValue(), "firstvalue");

        // but now let's wait a bit
        Thread.sleep(60);
        Assert.assertEquals(val.getValue(), "secondvalue");
    }
}
