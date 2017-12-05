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

import org.eclipse.configjsr.converters.implicit.*;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.testng.Assert;
import org.testng.annotations.Test;

import javax.config.Config;
import javax.inject.Inject;

import static org.eclipse.configjsr.base.AbstractTest.addFile;

/**
 * Test the implicit converter handling for private visibility.
 *
 * @author <a href="mail@sebastian-daschner.com">Sebastian Daschner</a>
 */
public class PrivateImplicitConverterTest extends Arquillian {

    @Deployment
    public static WebArchive deploy() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "privateImplicitConverterTest.jar")
            .addPackage(ConvTestTypeWPrivateCharSequenceCt.class.getPackage())
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .as(JavaArchive.class);

        addFile(testJar, "META-INF/javaconfig.properties");

        WebArchive war = ShrinkWrap
            .create(WebArchive.class, "privateImplicitConverterTest.war")
            .addAsLibrary(testJar);
        return war;
    }

    private @Inject
    Config config;

    @Test
    public void testImplicitConverterCharSequenceCt() {
        ConvTestTypeWPrivateCharSequenceCt value = config.getValue("tck.config.test.javaconfig.converter.implicit.charSequenceCt",
            ConvTestTypeWPrivateCharSequenceCt.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "charSequenceCt");
    }

    @Test
    public void testImplicitConverterCharSequenceParse() {
        ConvTestTypeWPrivateCharSequenceParse value = config.getValue("tck.config.test.javaconfig.converter.implicit.charSequenceParse",
            ConvTestTypeWPrivateCharSequenceParse.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "charSequenceParse");
    }

    @Test
    public void testImplicitConverterCharSequenceValueOf() {
        ConvTestTypeWPrivateCharSequenceValueOf value = config.getValue("tck.config.test.javaconfig.converter.implicit.charSequenceValueOf",
            ConvTestTypeWPrivateCharSequenceValueOf.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "charSequenceValueOf");
    }

    @Test
    public void testImplicitConverterStringCt() {
        ConvTestTypeWPrivateStringCt value = config.getValue("tck.config.test.javaconfig.converter.implicit.stringCt",
            ConvTestTypeWPrivateStringCt.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "stringCt");
    }

    @Test
    public void testImplicitConverterStringParse() {
        ConvTestTypeWPrivateStringParse value = config.getValue("tck.config.test.javaconfig.converter.implicit.stringParse",
            ConvTestTypeWPrivateStringParse.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "stringParse");
    }

    @Test
    public void testImplicitConverterStringValueOf() {
        ConvTestTypeWPrivateStringValueOf value = config.getValue("tck.config.test.javaconfig.converter.implicit.stringValueOf",
            ConvTestTypeWPrivateStringValueOf.class);
        Assert.assertNotNull(value);
        Assert.assertEquals(value.getVal(), "stringValueOf");
    }
}
