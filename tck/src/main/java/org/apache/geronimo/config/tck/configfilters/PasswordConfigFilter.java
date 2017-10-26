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
package org.apache.geronimo.config.tck.configfilters;

import javx.config.spi.ConfigFilter;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class PasswordConfigFilter implements ConfigFilter {
    @Override
    public String filterValue(String key, String value) {
        if (value != null && key.endsWith(".password")) {
            return decrypt(value);
        }
        return value;
    }

    @Override
    public String filterValueForLog(String key, String value) {
        if (value != null &&
            (key.contains("password") || key.contains("secret"))) {
            return "*******"; // simply star-out the password
        }
        return value;
    }

    private String decrypt(String value) {
        // Just to modify the string.
        // In reality the 'encryption' should be a bit stronger ;)
        return value.toLowerCase();
    }
}
