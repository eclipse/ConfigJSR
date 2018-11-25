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
import javax.config.ConfigAccessor;
import javax.config.spi.ConfigSource;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConfigAccessorTest extends Arquillian {

    private @Inject Config config;

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "configValueTest.jar")
            .addPackage(AbstractTest.class.getPackage())
            .addClass(ConfigAccessorTest.class)
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
        ConfigAccessor<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting",Integer.class)
                .withDefault(Integer.valueOf(1234));

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testGetValueWithStringDefault() {
        ConfigAccessor<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting",Integer.class)
                .withStringDefault("1234");

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    /**
     * Checks whether variable substitution works.
     * The following situation is configured in javaconfig.properties:
     * <pre>
     * tck.config.variable.baseHost = some.host.name
     * tck.config.variable.firstEndpoint = http://${tck.config.variable.baseHost}/endpointOne
     * tck.config.variable.secondEndpoint = http://${tck.config.variable.baseHost}/endpointTwo
     * </pre>
     */
    @Test
    public void testVariableReplacement() {
        Assert.assertEquals(config.access("tck.config.variable.firstEndpoint").getValue(),
                "http://some.host.name/endpointOne");

        Assert.assertEquals(config.access("tck.config.variable.secondEndpoint").getValue(),
                "http://some.host.name/endpointTwo");

        // variables in Config.getValue and getOptionalValue do not get evaluated otoh
        Assert.assertEquals(config.getValue("tck.config.variable.firstEndpoint", String.class),
                "http://${tck.config.variable.baseHost}/endpointOne");

        Assert.assertEquals(config.getOptionalValue("tck.config.variable.firstEndpoint", String.class).get(),
                "http://${tck.config.variable.baseHost}/endpointOne");
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
        ConfigAccessor<String> cv = config.access("com.foo.myapp")
            .addLookupSuffix("mycorp")
            .addLookupSuffix(config.access("javaconfig.projectStage"));

        Assert.assertFalse(cv.getOptionalValue().isPresent());

        ConfigurableConfigSource.configure(config, "com.foo.myapp", "TheDefault");
        Assert.assertEquals(cv.getValue(), "TheDefault");
        Assert.assertEquals(cv.getResolvedPropertyName(), "com.foo.myapp");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.Production", "BasicWithProjectStage");
        Assert.assertEquals(cv.getValue(), "BasicWithProjectStage");
        Assert.assertEquals(cv.getResolvedPropertyName(), "com.foo.myapp.Production");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.mycorp", "WithTenant");
        Assert.assertEquals(cv.getValue(), "WithTenant");
        Assert.assertEquals(cv.getResolvedPropertyName(), "com.foo.myapp.mycorp");

        ConfigurableConfigSource.configure(config, "com.foo.myapp.mycorp.Production", "WithTenantAndProjectStage");
        Assert.assertEquals(cv.getValue(), "WithTenantAndProjectStage");
        Assert.assertEquals(cv.getResolvedPropertyName(), "com.foo.myapp.mycorp.Production");
    }

    @Test
    public void testIntegerConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.integer").getValue(),
            Integer.valueOf(1234));
    }

    @Test
    public void testLongConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.long").getValue(),
            Long.valueOf(1234567890123456L));
    }

    @Test
    public void testFloatConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.float").getValue(),
            Float.valueOf(12.34f));
    }

    @Test
    public void testDoubleonverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.double").getValue(),
            Double.valueOf(12.34567890123456));
    }

    @Test
    public void testBooleanConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_uppercase").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_mixedcase").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.false").getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.one").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.zero").getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.seventeen").getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_uppercase").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_mixedcase").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y_uppercase").getValue(),
            Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.no").getValue(),
            Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.no_mixedcase").getValue(),
            Boolean.FALSE);

    }

    @Test
    public void testCacheFor() throws Exception {
        String key = "tck.config.test.javaconfig.cachefor.key";
        System.setProperty(key, "firstvalue");
        ConfigAccessor<String> val = config.access(key).cacheFor(30, TimeUnit.MILLISECONDS);
        Assert.assertEquals(val.getValue(), "firstvalue");

        // immediately change the value
        System.setProperty(key, "secondvalue");

        // we should still see the first value, because it is cached!
        Assert.assertEquals(val.getValue(), "firstvalue");

        // but now let's wait a bit
        Thread.sleep(60);
        Assert.assertEquals(val.getValue(), "secondvalue");
    }

    @Test
    public void testDefaultValue() {
        String key = "tck.config.test.javaconfig.somerandom.default.key";

        ConfigAccessor<String> val = config.access(key);
        Assert.assertNull(val.getDefaultValue());

        ConfigAccessor<String> val2 = config.access(key).withDefault("abc");
        Assert.assertEquals(val2.getDefaultValue(), "abc");
        Assert.assertEquals(val2.getValue(), "abc");

        ConfigAccessor<Integer> vali = config.access(key,Integer.class).withDefault(123);
        Assert.assertEquals(vali.getDefaultValue(), Integer.valueOf(123));
        Assert.assertEquals(vali.getValue(), Integer.valueOf(123));

        ConfigAccessor<Integer> vali2 = config.access(key,Integer.class).withStringDefault("123");
        Assert.assertEquals(vali2.getDefaultValue(), Integer.valueOf(123));
        Assert.assertEquals(vali2.getValue(), Integer.valueOf(123));

        System.setProperty(key, "666");
        Assert.assertEquals(vali2.getDefaultValue(), Integer.valueOf(123));
        Assert.assertEquals(vali2.getValue(), Integer.valueOf(666));


    }
}
