/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package javx.config.spi;

/**
 * <p>
 * If you implement this interface, the property files with the given file name will be registered as
 * {@link ConfigSource}s.</p>
 *
 **
 * <h3>Automatic pickup via {@code java.util.ServiceLoader} mechanism</h3>
 * <p>You need to register the PropertyFileConfig via the {@code java.util.ServiceLoader} mechanism.
 *
 * <p>The {@code ServiceLoader} mechanism requires to have a file
 * <pre>
 *     META-INF/services/javax.config.spi.PropertyFileConfig
 * </pre>
 * containing the fully qualified Class name of your own {@code PropertyFileConfig} implementation class.
 * <pre>
 *     com.acme.my.own.SomeSpecialPropertyFileConfig
 * </pre>
 * The implementation will look like the following:
 * <pre>
 *     public class SomeSpecialPropertyFileConfig implements PropertyFileConfig {
 *         public String getPropertyFileName() {
 *             return "myconfig/specialconfig.properties"
 *         }
 *
 *         public boolean isOptional() {
 *             return false;
 *         }
 *     }
 * </pre>
 * </p>
 *
 */
public interface PropertyFileConfig
{
    /**
     * All the property files on the classpath which have this name will get picked up and registered as
     * {@link ConfigSource}s.
     *
     * @return the full file name (including path) of the property files to pick up.
     */
    String getPropertyFileName();

    /**
     * @return true if the file is optional, false if the specified file has to be in place.
     */
    boolean isOptional();
}
