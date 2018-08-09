/*
 ********************************************************************************
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
 *   2015-04-30 - Ron Smeral
 *      Initially authored in Apache DeltaSpike 25b2b8cc0c955a28743f
 *   2016-07-14 - Mark Struberg
 *      JavaDoc + priority
 *   2016-12-01 - Emily Jiang / IBM Corp
 *      Marking as FunctionalInterface + JavaDoc + additional types
 *
 *******************************************************************************/

package javax.config.spi;

/**
 * <p>Interface for converting configured values from String to any Java type.
 **
 * <p>Converters for the following types are provided by default:
 * <ul>
 *     <li>{@code boolean} and {@code Boolean}, values for {@code true}: (case insensitive)
 *     &quot;true&quot;, &quot;yes&quot;, &quot;Y&quot;, &quot;on&quot;, &quot;1&quot;</li>
 *     <li>{@code int} and {@code Integer}</li>
 *     <li>{@code long} and {@code Long}</li>
 *     <li>{@code float} and {@code Float}, a dot '.' is used to separate the fractional digits</li>
 *     <li>{@code double} and {@code Double}, a dot '.' is used to separate the fractional digits</li>
 *     <li>{@code URL} as defined by {@link java.net.URL#URL(java.lang.String)}</li>
 *
 * </ul>

<p>
 * <p>Custom Converters will get picked up via the {@link java.util.ServiceLoader} mechanism and and can be registered by
 * providing a file<br>
 * <code>META-INF/services/javax.config.spi.Converter</code><br>
 * which contains the fully qualified {@code Converter} implementation class name as content.
 *
 * <p>A Converter can specify a {@code javax.annotation.Priority}.
 * If no priority is explicitly assigned, the value of 100 is assumed.
 * If multiple Converters are registered for the same type, the one with the highest priority will be used.
 * All Built In Converters have a {@code javax.annotation.Priority} of 1
 * A Converter should handle null values returning either null or a valid Object of the specified type.
 *
 * <h3>Implicit Converters</h3>
 *
 * <p>If no explicit Converter and no built-in Converter could be found for a certain type,
 * the {@code Config} provides an <em>Implicit Converter</em>, if</p>
 * <ul>
 *     <li>the target type {@code T} has a {@code static T of(String)} method, or</li>
 *     <li>the target type {@code T} has a {@code static T of(CharSequence)} method, or</li>
 *     <li>the target type {@code T} has a {@code static T valueOf(String)} method, or</li>
 *     <li>the target type {@code T} has a {@code static T valueOf(CharSequence)} method, or</li>
 *     <li>the target type {@code T} has a {@code static T parse(String)} method, or</li>
 *     <li>the target type {@code T} has a {@code static T parse(CharSequence)} method, or</li>
 *     <li>The target type {@code T} has a Constructor with a String parameter, or</li>
 *     <li>The target type {@code T} has a Constructor with a CharSequence parameter, or</li>
 * </ul>
 *
 * <p>The lookup will be done in the order of the above list.
 * <p>Note that every {@code java.time} type has a {@code parse(CharSequence)} method.
 * And every enum has a generated {@code valueOf(String)} method.
 * They are thus all covered by an implicit converter!
 * <p>If an Implicit Converter cannot convert a value, a {@code java.lang.IllegalArgumentException} is to be thrown.
 *
 * <h3>Array Converters</h3>
 *  The implementation must support the Array converter for each built-in converters, implicit converters and custom converters.
 *  The delimiter for the config value is ','. The escape character is '\'.
 *  <code>e.g. myPets=dog,cat,dog\,cat </code>
 * <p>
 *  For the property injection, List and Set are supported as well.
 *
 *  <p>
 *  Usage:
 *  <p>
 *  <code>
 *  String[] myPets = ConfigProvider.getValue("myPet", String[].class);
 *  </code>
 *
 *  <p>
 *  <code>
 *  {@code @Inject @ConfigProperty(name="myPets") String[] myPets};
 *  </code>
 *  <p>
 *  <code>
 *  {@code @Inject @ConfigProperty(name="myPets") List<String> myPets};
 *  </code>
 *  <p>
 *  <code>
 *  {@code @Inject @ConfigProperty(name="myPets") Set<String> myPets};
 *  </code>
 *  <p>
 *  myPets will be "dog", "cat", "dog,cat"
 *
 *  <h3>Cleanup</h3>
 *
 *  <p>If a Converter implements the {@link AutoCloseable} interface
 *  then the {@link AutoCloseable#close()} method will be called when
 *  the underlying {@link javax.config.Config} is being released.
 *
 * @author <a href="mailto:rsmeral@apache.org">Ron Smeral</a>
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:emijiang@uk.ibm.com">Emily Jiang</a>
 * @author <a href="mailto:john.d.ament@gmail.com">John D. Ament</a>
 */
public interface Converter<T> {
    /**
     * Configure the string value to a specified type
     * @param value the string representation of a property value.
     * @return the converted value or null
     *
     * @throws IllegalArgumentException if the value cannot be converted to the specified type.
     */
    T convert(String value);
}
