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

package javax.config;

import javax.config.inject.ConfigProperty;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Specifies a configuration type as composed configuration.
 * Composed configuration types comprise multiple, potentially hierarchical, configuration values.
 *
 * <h2>Examples</h2>
 *
 * <h3>Composed, coherent configuration</h3>
 * <p>
 * The following example defines a coherent server socket configuration.
 *
 * <pre>
 * &#064;ComposedConfig
 * public class SocketConfig {
 *
 *     &#064;ConfigProperty(name = "name")
 *     private String name;
 *
 *     &#064;ConfigProperty(name = "protocol", defaultValue = "http")
 *     private String protocolName;
 *
 *     &#064;ConfigProperty(name = "port")
 *     private int port;
 *
 *     // getters & setters
 * }
 * </pre>
 * <p>
 * The {@code SocketConfig} configuration can be retrieved like any other configured value, by programmatic lookup, or dependency injection:
 *
 * <pre>
 * public class SomeBean {
 *
 *     &#064;Inject
 *     &#064;ConfigProperty(name = "server.socket")
 *     private SocketConfig socketConfig;
 *
 * }
 * </pre>
 * <p>
 * The example will resolve the configuration values as follows, provided by the corresponding property keys:
 *
 * <pre>
 * server.socket.name
 * server.socket.protocol
 * server.socket.port
 * </pre>
 *
 * <h3>Implicit property resolution</h3>
 * <p>
 * It's possible to omit the individual {@link ConfigProperty} annotations on the fields of the composed type.
 * In this case the composed property keys are derived from the field names:
 * <p>
 *
 * <pre>
 * public class SomeBean {
 *
 *     &#064;Inject
 *     &#064;ConfigProperty(name = "server.socket")
 *     private SocketConfig socketConfig;
 *
 *     &#064;ComposedConfig
 *     public static class SocketConfig {
 *
 *         private String name;
 *         private String protocol;
 *         private int port;
 *
 *         // getters & setters
 *     }
 * }
 * </pre>
 *
 * <p>
 * This example will result in the same configuration resolution as in the previous example, apart from the default value for {@code server.socket.protocol}.
 * <p>
 * If the property keys differ from the field names, they can be overridden individually via the {@link ConfigProperty} annotation.
 * The same is true for default values, as seen before.
 * <p>
 * The {@link ConfigProperty} annotation can be annotated on fields as well as on methods.
 * The latter is useful if interfaces instead of classes are defined as composed types.
 * Per default, methods are not implicitly taken to resolve composed configuration properties.
 * If both fields and methods are annotated within a single type, methods take precedence.
 * <p>
 * See the following example for a composed interface configuration type.
 * <p>
 *
 * <pre>
 * &#064;ComposedConfig
 * public interface SocketConfig {
 *
 *     &#064;ConfigProperty(name = "name")
 *     String name();
 *
 *     &#064;ConfigProperty(name = "protocol", defaultValue = "http")
 *     String protocolName();
 *
 *     &#064;ConfigProperty(name = "port")
 *     int getPort();
 * }
 * </pre>
 * <p>
 * This example will result in the same configuration as before.
 *
 * <h3>Hierarchical type resolution</h3>
 * <p>
 * The configuration properties of composed types are resolved hierarchically.
 * That is, properties in composed types are implicitly considered as possible composed types themselves, as well.
 * This allows developers to define complex configuration structures without repeating annotations.
 * <p>
 * The types of composed properties are therefore resolved as possible composed configuration types, if no built-in, custom, or implicit converters are defined.
 * <p>
 * The hierarchical resolution works both for implicitly resolved fields and explicitly annotated members.
 *
 * <pre>
 * &#064;ComposedConfig
 * public class ServerConfig {
 *
 *     private String host;
 *     private SocketConfig socket;
 *
 *     // getters & setters
 *
 *     public static class SocketConfig {
 *         private String name;
 *         private String protocol;
 *         private int port;
 *
 *         // getters & setters
 *     }
 *
 * }
 * </pre>
 * <p>
 * If a {@code ServerConfig} configuration type is retrieved, the property keys are resolved as follows:
 *
 * <pre>
 * public class SomeBean {
 *
 *     &#064;Inject
 *     &#064;ConfigProperty(name = "server")
 *     private ServerConfig serverConfig;
 *
 * }
 * </pre>
 * <p>
 * This leads to:
 *
 * <pre>
 * server.host
 * server.socket.name
 * server.socket.protocol
 * server.socket.port
 * </pre>
 * <p>
 * The property keys are resolved by the field names, or the names defined in {@link ConfigProperty}, respectively, and combined via dot ({@code .}).
 * <p>
 * The example above is congruent with annotating {@code SocketConfig} with {@link ComposedConfig}, as well.
 *
 * <h3>Collection resolution</h3>
 * <p>
 * Composed configuration types also resolve collections and array types.
 *
 * <pre>
 * &#064;ComposedConfig
 * public class MultiSocketServerConfig {
 *
 *     private String[] hosts;
 *     private List<SocketConfig> sockets;
 *
 *     // getters & setters
 *
 *     public static class SocketConfig {
 *         private String name;
 *         private String protocol;
 *         private int port;
 *
 *         // getters & setters
 *     }
 *
 * }
 * </pre>
 * <p>
 * If the {@code MultiSocketServerConfig} type is resolved by key {@code alternative-server}, it results in the following:
 *
 * <pre>
 * server.hosts.0
 * server.hosts.1
 *
 * server.sockets.0.name
 * server.sockets.0.protocol
 * server.sockets.1.name
 * </pre>
 * <p>
 * Element types of collections and arrays are resolved by an implicit zero-based index, which is part of the resulting, combined property key.
 * <p>
 * This collection resolution works for array types, and types that are assignable to {@link java.util.Collection}.
 * For unordered collection types, e.g. {@link java.util.Set}, the order in which the configured elements will be retrieved is non-deterministic, despite the (zero-based) indexed key names.
 * <p>
 * Similar to singular sub-types, the element types within the collection or array are resolved by potentially existent converters, and resolved recursively if no built-in, custom, or implicit converters are defined.
 *
 * @author <a href="mailto:mail@sebastian-daschner.com">Sebastian Daschner</a>
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface ComposedConfig {

    /**
     * Only valid for injection of dynamically readable values, e.g. {@code Provider<String>}!
     *
     * @return {@code TimeUnit} for {@link #cacheFor()}
     */
    @Nonbinding
    TimeUnit cacheTimeUnit() default TimeUnit.SECONDS;

    /**
     * Only valid for injection of dynamically readable values, e.g. {@code Provider<String>}!
     *
     * @return how long should dynamic values be locally cached. Measured in {@link #cacheTimeUnit()}.
     */
    @Nonbinding
    long cacheFor() default 0L;
}
