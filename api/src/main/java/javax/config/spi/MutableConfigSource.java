/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package javax.config.spi;

import java.util.Collection;
import java.util.function.BiConsumer;

/**
 * Config source that supports changes - e.g. the content may change over time,
 * based on some trigger (file system change, database change etc.).
 */
public interface MutableConfigSource extends ConfigSource {
    /**
     * Add a listener for changes on this config source.
     * The MutableConfigSource can either return the same instance or a new instance of a config source, though it must
     * copy the listener(s) to the new instance (e.g. config will NOT re-subscribe).
     *
     * @param listener listener that gets an instance with the latest configuration values and with a collection of modified keys
     */
    void listen(BiConsumer<MutableConfigSource, Collection<String>> listener);
}
