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
package org.eclipse.configjsr.converters;

import jakarta.config.spi.Converter;

/**
 * @author <a href="mailto:emijiang@uk.ibm.com">Emily Jiang</a>
 */
public class PizzaConverter implements Converter<Pizza> {

    @Override
    public Pizza convert(String value) {
        String[] parts = value.split(":");
        if (parts.length ==2) {
            String size = parts[0];
            String flavor = parts[1];
            return new Pizza(flavor, size);
        }

        return null;
    }
}
