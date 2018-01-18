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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import javax.config.inject.ConfigProperty;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;
import javax.inject.Provider;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.asset.StringAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test cases for the following statement.
 * The configuration propertyName. If the property name contains `.` (e.g. com.ACME.size), the dot will be
 * automatically converted `_` (e.g. com_ACME_size) for matching among environment variables. In the first attempt of searching the property
 * among environment variables, the variable name needs to be the same with case sensitive (e.g. com_ACME_size). If not found, try to match
 * the case-insensitive environment variable (e.g. COM_ACME_SIZE or COME_ACME_Size)
 *
 * Prerequisite:
 * The following environment variables must be set prior to running this test:
 * "my_int_property" with the value of "45"
 * "MY_BOOLEAN_Property" with the value of "true"
 * "my_string_property" with the value of "haha"
 * "MY_STRING_PROPERTY" with the value of "woohoo"
 * @author Emily Jiang
 */
public class CDIPropertyNameMatchingTest extends Arquillian {


    @Deployment
    public static Archive deployment() {
        JavaArchive testJar = ShrinkWrap
            .create(JavaArchive.class, "CDIPropertyNameMatchingTest.jar")
            .addClasses(CDIPropertyNameMatchingTest.class, SimpleValuesBean.class)
            .addAsManifestResource(new StringAsset(
                    "my.int.property=3"+
                        "\nmy.string.property=fake" +
                        "\nmy.random.string.property=random"),
                "javaconfig.properties")
            .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
            .as(JavaArchive.class);

        WebArchive war = ShrinkWrap
            .create(WebArchive.class, "CDIPropertyNameMatchingTest.war")
            .addAsLibrary(testJar);
        return war;
    }

    @BeforeClass
    public void checkSetup() {
       //check whether the environment variables were populated by the executor correctly

        if (!"45".equals(System.getenv("my_int_property"))) {
         Assert.fail("Before running this test, the environment variable \"my_int_property\" must be set with the value of 45");
        }
        if (!"true".equals(System.getenv("MY_BOOLEAN_Property"))) {
            Assert.fail("Before running this test, the environment variable \"MY_BOOLEAN_Property\" must be set with the value of true");
        }
        if (!"haha".equals(System.getenv("my_string_property"))) {
            Assert.fail("Before running this test, the environment variable \"my_string_property\" must be set with the value of haha");
        }
        if (!"woohoo".equals(System.getenv("MY_STRING_PROPERTY"))) {
            Assert.fail("Before running this test, the environment variable \"MY_STRING_PROPERTY\" must be set with the value of woohoo");
        }

    }

    @Test
    public void testPropertyFromEnvironmentVariables() {
        SimpleValuesBean bean = getBeanOfType(SimpleValuesBean.class);

        assertThat(bean.stringProperty.get(), is(equalTo("haha")));
        assertThat(bean.booleanProperty.get(), is(true));
        assertThat(bean.intProperty.get(), is(equalTo(45)));
        assertThat(bean.randomStringProperty, is(equalTo("random")));
    }



    private <T> T getBeanOfType(Class<T> beanClass) {
        return CDI.current().select(beanClass).get();
    }

    @Dependent
    public static class SimpleValuesBean {

        @Inject
        @ConfigProperty(name="my.string.property")
        private Provider<String> stringProperty;

        @Inject
        @ConfigProperty(name="my.boolean.property")
        private Provider<Boolean> booleanProperty;

        @Inject
        @ConfigProperty(name="my.int.property")
        private Provider<Integer> intProperty;

        @Inject
        @ConfigProperty(name="my.random.string.property")
        private String randomStringProperty;
    }
  }
