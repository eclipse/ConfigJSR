/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.geronimo.config.tck;

import javx.config.Config;
import javx.config.ConfigProvider;
import org.apache.geronimo.config.tck.converters.DuckConverter;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConverterTest {

    @Test
    public void testIntegerConverter() {
        Config config = ConfigProvider.getConfig();
        Integer value = config.access("tck.config.test.javaconfig.converter.integervalue").as(Integer.class).getValue();
        Assert.assertEquals(value, Integer.valueOf(1234));

    }

    @Test
    public void testFloatConverter() {
        Config config = ConfigProvider.getConfig();
        Float value = config.access("tck.config.test.javaconfig.converter.floatvalue").as(Float.class).getValue();
        Assert.assertEquals(value, Float.valueOf(12.34f));

    }

    @Test
    public void testManuallyAddedConverter() {
        Config config = ConfigProvider.getConfig();

        config.addConverter(new DuckConverter());
    }



}
