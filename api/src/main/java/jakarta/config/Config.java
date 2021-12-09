/*
 *******************************************************************************
 * Copyright (c) 2011-2018 Contributors to the Eclipse Foundation
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
 * Contributors:
 *   2011-12-28 - Mark Struberg & Gerhard Petracek
 *      Initially authored in Apache DeltaSpike as ConfigResolver fb0131106481f0b9a8fd
 *   2015-04-30 - Ron Smeral
 *      Typesafe Config authored in Apache DeltaSpike 25b2b8cc0c955a28743f
 *   2016-07-14 - Mark Struberg
 *      Extracted the Config part out of Apache DeltaSpike and proposed as Microprofile-Config
 *   2016-11-14 - Emily Jiang / IBM Corp
 *      Experiments with separate methods per type, JavaDoc, method renaming
 *
 *******************************************************************************/

package jakarta.config;

import java.util.Optional;

import jakarta.config.spi.ConfigSource;

/**
 * <p>
 * Resolves the property value by searching through all configured
 * {@link ConfigSource ConfigSources}. If the same property is specified in multiple
 * {@link ConfigSource ConfigSources}, the value in the {@link ConfigSource} with the highest
 * ordinal will be used.
 * <p>If multiple {@link ConfigSource ConfigSources} are specified with
 * the same ordinal, the {@link ConfigSource#getName()} will be used for sorting.
 * <p>
 * The config objects produced via the injection model <pre>@Inject Config</pre> are guaranteed to be serializable, while
 * the programmatically created ones are not required to be serializable.
 *
 * <h2>Usage</h2>
 *
 * For accessing the config you can use the {@link ConfigProvider}:
 *
 * <pre>
 * public void doSomething(
 *   Config cfg = ConfigProvider.getConfig();
 *   String archiveUrl = cfg.getString("my.project.archive.endpoint", String.class);
 *   Integer archivePort = cfg.getValue("my.project.archive.port", Integer.class);
 * </pre>
 *
 * <p>For accessing a configuration in a dynamic way you can also use {@link #access(String, Class)}.
 * This method returns a builder-style {@link ConfigAccessor} instance for the given key.
 * You can further specify a Type of the underlying configuration, a cache time, lookup paths and
 * many more.
 *
 * <p>It is also possible to inject the Config if a DI container is available:
 *
 * <pre>
 * public class MyService {
 *     &#064;Inject
 *     private Config config;
 * }
 * </pre>
 *
 * <p>See {@link #getValue(String, Class)} and {@link #getOptionalValue(String, Class)} and
 * {@link #access(String, Class)} for accessing a configured value.
 *
 * <p>Configured values can also be accessed via injection.
 * See {@link jakarta.config.inject.ConfigProperty} for more information.
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:gpetracek@apache.org">Gerhard Petracek</a>
 * @author <a href="mailto:rsmeral@apache.org">Ron Smeral</a>
 * @author <a href="mailto:emijiang@uk.ibm.com">Emily Jiang</a>
 * @author <a href="mailto:gunnar@hibernate.org">Gunnar Morling</a>
 * @author <a href="mailto:manfred.huber@downdrown.at">Manfred Huber</a>
 * @author <a href="mailto:alexander.falb@rise-world.com">Alex Falb</a>
 *
 */
public interface Config {

    /**
     * Return the resolved property value with the specified type for the
     * specified property name from the underlying {@link ConfigSource ConfigSources}.
     *
     * If this method gets used very often then consider to locally store the configured value.
     *
     * <p>Note that no variable replacement like in {@link ConfigAccessor.Builder#evaluateVariables(boolean)} will be performed!
     *
     * @param <T>  the property type
     * @param key
     *             The configuration propertyName.
     * @param valueType
     *             The type into which the resolve property value should get converted
     * @return the resolved property value as an object of the requested type.
     * @throws IllegalArgumentException if the property cannot be converted to the specified type.
     * @throws java.util.NoSuchElementException if the property isn't present in the configuration.
     */
    <T> T getValue(String key, Class<T> valueType);

    /**
     * Return the resolved property value with the specified type for the
     * specified property name from the underlying {@link ConfigSource ConfigSources}.
     *
     * If this method is used very often then consider to locally store the configured value.
     *
     * <p>Note that no variable replacement like in {@link ConfigAccessor.Builder#evaluateVariables(boolean)} will be performed!
     *
     * @param <T>  the property type
     * @param key
     *             The configuration propertyName.
     * @param valueType
     *             The type into which the resolve property value should be converted
     * @return the resolved property value as an Optional of the requested type.
     *
     * @throws IllegalArgumentException if the property cannot be converted to the specified type.
     */
    <T> Optional<T> getOptionalValue(String key, Class<T> valueType);

    /**
     * Create a {@link ConfigAccessor} to access the underlying configuration.
     *
     * @param key the property key
     * @param valueType type into which the resolve property value should get converted
     * @param <T> the property type
     * @return a {@code ConfigAccessor} to access the given propertyName
     */
    <T> ConfigAccessor.Builder<T> access(String key, Class<T> valueType);

    /**
     * Return all property names used in any of the underlying {@link ConfigSource ConfigSources}.
     * @return the names of all configured keys of the underlying configuration.
     */
    Iterable<String> getPropertyNames();

    /**
     * @return all currently registered {@link ConfigSource ConfigSources} sorted by descending ordinal and ConfigSource name
     */
    Iterable<ConfigSource> getConfigSources();

}
