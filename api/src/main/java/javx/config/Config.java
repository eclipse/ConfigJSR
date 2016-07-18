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
package javx.config;

import java.util.List;
import java.util.Map;

import javx.config.spi.ConfigFilter;
import javx.config.spi.ConfigSource;
import javx.config.spi.ConfigSourceProvider;
import javx.config.spi.Converter;

/**
 * <p>Resolves configured values of properties by going through the list
 * of configured {@link ConfigSource}s and using the one with the highest ordinal.
 * If multiple {@link ConfigSource}s have the same ordinal, their order is undefined.</p>
 *
 * <p>You can provide your own lookup paths by implementing and registering additional
 * {@link ConfigSource}s and {@link ConfigSourceProvider} implementations.</p>
 *
 * <p>A configured value can be accessed with {@link #getValue(String)}.</p>
 *
 * <p>For accessing a coniguration in a dynamic way you can also use {@link #access(String)}.
 * This method returns a builder-style {@link ConfigValue} instance for the given key.
 * You can further specify a Type of the underlying configuration, a cache time, lookup paths and
 * many more.
 * </p>
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public interface Config {

    /**
     * Resolves the value configured for the given key.
     *
     * @param key the property key
     *
     * @return the configured property value from the {@link ConfigSource} with the highest ordinal
     *         or {@code null} if there is no configured value for it
     */
    String getValue(String key);

    /**
     * Create a {@link ConfigValue} to access the underlying configuration.
     *
     * @param key the property key
     */
    ConfigValue<String> access(String key);

    /**
     * Returns a Map of all properties from all scannable config sources. The values of the properties reflect the
     * values that would be obtained by a call to {@link #getValue(java.lang.String)}, that is, the value of the
     * property from the ConfigSource with the highest ordinal.
     *
     * @see ConfigSource#isScannable()
     */
    Map<String, String> getAllProperties();

    /**
     * Filter the configured value.
     * This can e.g. be used for decryption.
     * @return the filtered value
     */
    String filterConfigValue(String key, String value);

    /**
     * Filter the configured value for logging.
     * This can e.g. be used for displaying ***** instead of a real password.
     * @return the filtered value
     */
    String filterConfigValueForLog(String key, String value);

    /**
     * This method might be useful for debugging purpose or to
     * show the values of all ConfigSources in an admin page.
     *
     * @return all currently registered {@link ConfigSource}s
     */
    ConfigSource[] getConfigSources();

    /**
     * This method can be used for programmatically adding {@link ConfigSource}s
     * to this very Config.
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     *
     * @param configSourcesToAdd the ConfigSources to add
     */
    void addConfigSources(List<ConfigSource> configSourcesToAdd);

    /**
     * Add a {@link ConfigFilter} to this very Config
     *
     * It is not needed for normal 'usage' by end users, but only for Extension Developers!
     */
    void addConfigFilter(ConfigFilter configFilterToAdd);

    /**
     * Add a custom {@link Converter}
     *
     * @param converter
     */
    void addConverter(Converter<?> converter);
}
