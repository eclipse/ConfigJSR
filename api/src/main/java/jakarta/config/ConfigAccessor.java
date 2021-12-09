/*
 ******************************************************************************
 * Copyright (c) 2009-2018 Contributors to the Eclipse Foundation
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
 *      Contributed to Apache DeltaSpike fb0131106481f0b9a8fd
 *   2016-07-07 - Mark Struberg
 *      Extracted the Config part out of DeltaSpike and proposed as Microprofile-Config 8ff76eb3bcfaf4fd
 *
 *******************************************************************************/
package jakarta.config;


import java.time.Duration;
import java.util.Optional;
import java.util.function.BiFunction;

import jakarta.config.spi.Converter;


/**
 * Accessor to a configured value.
 *
 * It follows a builder-like pattern to define in which ways to access the configured value
 * of a certain property name.
 *
 * Accessing the configured value is finally done via {@link #getValue()}
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:gpetracek@apache.org">Gerhard Petracek</a>
 * @author <a href="mailto:tomas.langer@oracle.com">Tomas Langer</a>
 */
public interface ConfigAccessor<T> {


    /**
     * Returns the converted resolved filtered value.
     * @return the resolved value
     *
     * @throws IllegalArgumentException if the property cannot be converted to the specified type.
     * @throws java.util.NoSuchElementException if the property isn't present in the configuration.
     */
    T getValue();

    /**
     * Returns the converted resolved filtered value.
     * @return the resolved value as Optional
     *
     * @throws IllegalArgumentException if the property cannot be converted to the specified type.
     */
    Optional<T> getOptionalValue();

    /**
     * Returns the property name key given in {@link Config#access(String, Class)}.
     * @return the original property name
     */
    String getPropertyName();

    /**
     * Returns the actual key which led to successful resolution and corresponds to the resolved value.
     * This is useful when {@link Builder#addLookupSuffix(String)} is used.
     * Otherwise the resolved key should always be equal to the original key.
     * This method is provided for cases, when parameterized resolution is
     * requested and some of the fallback keys is used.
     *
     * This should be called only after calling {@link #getValue()} otherwise the value is undefined (but likely
     * null).
     *
     * Note that this will only give you the resolved key from the last non-cached value resolving.
     * @return the actual property name which led to successful resolution and corresponds to the resolved value.
     */
    String getResolvedPropertyName();

    /**
     * Returns the default value provided by {@link Builder#withDefault(Object)}
     * or {@link Builder#withStringDefault(String)}.
     *
     * @return the default value or {@code null} if no default was provided.
     */
    T getDefaultValue();


    /**
     * Accessor to a configured value.
     *
     * It follows a builder-like pattern to define in which ways to access the configured value
     * of a certain property name.
     *
     * Accessing the configured value is finally done via {@link ConfigAccessor#getValue()}
     *
     */
    interface Builder<T> {

        /**
         * Defines a specific {@link Converter} to be used instead of applying the default Converter resolving logic.
         * This method only applies if a single config key needs to be converted.
         *
         * @param converter The converter for the target type
         * @return This builder as a typed ConfigAccessor
         */
        Builder<T> withPropertyConverter(Converter<T> converter);

        /**
         * Defines a specific bean converter function to be used.
         * Use this method if the target type consists of information taken from multiple config keys.
         * If you consider the following type:
         * <pre>
         *  public record ServerEndpoint(String host, Integer port, String path)
         *
         *  config.properties:
         *  my.app.some.server.host=myserver
         *  my.app.some.server.port=80
         *  my.app.some.server.path=/myapp/endpoint1
         *  my.app.other.server.host=otherserver
         *  my.app.other.server.port=443
         *  my.app.other.server.path=/otherapp/endpoint2
         *
         * </pre>
         * This function will have access to all the underlying properties by using the given Config.
         * When resolving the target type the system makes sure that access to all requested properties is atomic.
         *
         * @param converter
         * @return This builder
         */
        Builder<T> withBeanConverter(BiFunction<Config /* the config itself */, String /* base key for the configured bean */, T> converter);

        /**
         * Sets the default value to use in case the resolution returns null.
         * @param value the default value
         * @return This builder
         */
        Builder<T> withDefault(T value);

        /**
         * Sets the default value to use in case the resolution returns null. Converts the given String to the type of
         * this resolver using the same method as used for the configuration entries.
         * @param value string value to be converted and used as default
         * @return This builder
         */
        Builder<T> withStringDefault(String value);

        /**
         * Specify that a resolved value will get cached for a certain maximum amount of time.
         * After the time expires the next {@link ConfigAccessor#getValue()} will again resolve the value
         * from the underlying {@link Config}.
         *
         * Note that that the cache will get flushed if a {@code ConfigSource} notifies
         * the underlying {@link Config} about a value change.
         * This is done by invoking the callback provided to the {@code ConfigSource} via
         * {@link jakarta.config.spi.ConfigSource#setAttributeChangeCallback(java.util.function.Consumer)}.
         *
         * @param duration the maximum amount of the time to cache this value.
         * @return This builder
         */
        Builder<T> cacheFor(Duration duration);

        /**
         * Whether to evaluate variables in configured values.
         * A variable starts with '${' and ends with '}', e.g.
         * <pre>
         * mycompany.some.url=${myserver.host}/some/path
         * myserver.host=http://localhost:8081
         * </pre>
         * If 'evaluateVariables' is enabled, the result for the above key
         * {@code "mycompany.some.url"} would be:
         * {@code "http://localhost:8081/some/path"}
         *
         * <p><b>ATTENTION:</b> This defaults to {@code true}! That means variable replacement is enabled by default!</p>
         *
         * @param evaluateVariables whether to evaluate variables in values or not
         *
         * @return This builder
         */
        Builder<T> evaluateVariables(boolean evaluateVariables);

        /**
         * The methods {@link Builder#addLookupSuffix(String)}
         * append the given parameters as optional suffixes to the {@link ConfigAccessor#getPropertyName()}.
         * Those methods can be called multiple times.
         * Each time the given suffix will be added to the end of suffix chain.
         *
         * This very version
         *
         * <p>Usage:
         * <pre>
         * String tenant = getCurrentTenant();
         *
         * Integer timeout = config.access("some.server.url", Integer.class)
         *                         .addLookupSuffix(tenant)
         *                         .addLookupSuffix(config.access("jakarta.config.projectStage").build())
         *                         .build()
         *                         .getValue();
         * </pre>
         *
         * Given the current tenant name is 'myComp' and the property
         * {@code jakartaconfig.projectStage} is 'Production' this would lead to the following lookup order:
         *
         * <ul>
         *     <li>"some.server.url.myComp.Production"</li>
         *     <li>"some.server.url.myComp"</li>
         *     <li>"some.server.url.Production"</li>
         *     <li>"some.server.url"</li>
         * </ul>
         *
         * The algorithm to use in {@link ConfigAccessor#getValue()} is a binary count down.
         * Every parameter is either available (1) or not (0).
         * Having 3 parameters, we start with binary {@code 111} and count down to zero.
         * The first combination which resolves to a result is being treated as result.
         *
         * @param suffixValue fixed String to be used as suffix
         * @return This builder
         */
        Builder<T> addLookupSuffix(String suffixValue);

        /**
         *
         * @param suffixAccessor {@link ConfigAccessor} to be used to resolve the suffix.
         * @return This builder
         * @see #addLookupSuffix(String)
         */
        Builder<T> addLookupSuffix(ConfigAccessor<String> suffixAccessor);

        /**
         * Build a ConfigAccessor
         * @return the configAccessor
         */
        ConfigAccessor<T> build();

    }
}
