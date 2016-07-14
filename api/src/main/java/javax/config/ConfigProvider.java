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
package javax.config;

import java.util.ServiceLoader;
import java.util.logging.Logger;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConfigProvider {

    private static final SPI instance = loadSpi();

    /**
     * Provide a {@link Config} based on all {@link javax.config.spi.ConfigSource}s
     * of the current Thread Context ClassLoader (TCCL)
     *
     * <p>There is exactly a single Config instance per ClassLoader</p>
     */
    public static Config getConfig() {
        return instance.getConfig();
    }

    /**
     * Provide a {@link Config} based on all {@link javax.config.spi.ConfigSource}s
     * of the given ClassLoader.
     *
     * <p>There is exactly a single Config instance per ClassLoader</p>
     */
    public static Config getConfig(ClassLoader forClassLoader) {
        return instance.getConfig(forClassLoader);
    }

    /**
     * Create a fresh {@link Config} instance.
     * This Config will initially contain no
     * {@link javax.config.spi.ConfigSource} nor any {@link javax.config.spi.ConfigFilter}.
     * Those have to be added manually.
     *
     * The ConfigProvider will not manage the Config instance internally
     */
    public static Config newConfig() {
        return instance.newConfig();
    }

    /**
     * A {@link Config} normally gets released if the ClassLoader it represents gets destroyed.
     * Invoke this method if you like to destroy the Config prematurely.
     */
    public static void releaseConfig(Config config) {
        instance.releaseConfig(config);
    }


    /**
     * This interface gets implemented internally by the Config library.
     */
    public interface SPI {
        Config getConfig();
        Config getConfig(ClassLoader forClassLoader);
        Config newConfig();
        void releaseConfig(Config config);
    }

    private static SPI loadSpi() {
        ServiceLoader<SPI> sl = ServiceLoader.load(SPI.class);
        SPI instance = null;
        for (SPI spi : sl) {
            if (instance != null) {
                Logger.getLogger(ConfigProvider.class.getName()).warning("Multiple ConfigResolver SPIs found. Ignoring " + spi.getClass().getName());
            }
            else {
                instance = spi;
            }
        }
        if (instance == null) {
            throw new IllegalStateException("No ConfigResolver SPI implementation found!");
        }
        return instance;
    }


}
