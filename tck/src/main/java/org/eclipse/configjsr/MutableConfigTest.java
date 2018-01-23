/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 */
package org.eclipse.configjsr;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.config.Config;
import javax.config.spi.ConfigBuilder;
import javax.config.spi.ConfigProviderResolver;
import javax.config.spi.MutableConfigSource;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Test mutable config source and mutable config.
 */
public class MutableConfigTest {
    @Test
    public void testMutableConfigSource() {
        String propertyName = "theKey";

        Map<String, String> props = new HashMap<>();
        props.put(propertyName, "originalValue");
        MyConfigSource mcf = new MyConfigSource(props);

        ConfigBuilder builder = ConfigProviderResolver.instance().getBuilder();
        Config config = builder.withSources(mcf).build();

        // test listener on whole config
        config.addListener(changedKeys -> {
            Assert.assertEquals(changedKeys, Collections.singleton(propertyName));
            Assert.assertEquals(config.getValue(propertyName, String.class), "newValue", "New config after mutation");
        });

        // test listener on a single property name
        config.addNameListener(propertyName, prop -> {
            Assert.assertEquals(prop, propertyName);
            Assert.assertEquals(config.getValue(propertyName, String.class), "newValue", "New config after mutation");
        });

        // test listener on a collection of property names
        config.addNameListener(Collections.singleton(propertyName), changedKeys -> {
            Assert.assertEquals(changedKeys, Collections.singleton(propertyName));
            Assert.assertEquals(config.getValue(propertyName, String.class), "newValue", "New config after mutation");
        });

        // test current value
        Assert.assertEquals(config.getValue("theKey", String.class), "originalValue", "Before mutation");

        mcf.setProperty("theKey", "newValue");

        // test new value - we cannot do this, as Config may run the mutation in a different thread and may delay it from
        // actaul config source trigger
        //Assert.assertEquals(config.getValue("theKey", String.class), "originalValue", "newValue");
    }

    // ignoring concurrency for the sake of example
    private static class MyConfigSource implements MutableConfigSource {
        private final Map<String, String> props;
        private final List<BiConsumer<MutableConfigSource, Collection<String>>> listeners = new LinkedList<>();

        private MyConfigSource(Map<String, String> props) {
            this.props = new HashMap<>(props);
        }

        private void setProperty(String key, String value) {
            if (listeners.isEmpty()) {
                return;
            }

            Map<String, String> newProps = new HashMap<>(props);
            String put = newProps.put(key, value);
            if (null != put && put.equals(value)) {
                return;
            }
            MyConfigSource newInstance = new MyConfigSource(newProps);
            newInstance.listeners.addAll(listeners); // as long as this source belongs to a single config instance, we are safe to do
            // this

            listeners.forEach(listener -> listener.accept(newInstance, Collections.singleton(key)));

            // and this
            this.listeners.clear();
        }

        @Override
        public void listen(BiConsumer<MutableConfigSource, Collection<String>> listener) {
            this.listeners.add(listener);
        }

        @Override
        public Map<String, String> getProperties() {
            return Collections.unmodifiableMap(props);
        }

        @Override
        public String getValue(String propertyName) {
            return props.get(propertyName);
        }

        @Override
        public String getName() {
            return "tck:mutable";
        }
    }
}
