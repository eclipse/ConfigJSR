package org.apache.geronimo.config.tck;

import javx.config.Config;
import javx.config.ConfigProvider;
import javx.config.ConfigValue;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.concurrent.TimeUnit;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConfigValueTest {

    private Config config = ConfigProvider.getConfig();

    @Test
    public void testGetValue() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.key1").getValue(), "value1");
    }

    @Test
    public void testGetValueWithDefault() {
        ConfigValue<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting")
                .as(Integer.class)
                .withDefault(Integer.valueOf(1234));

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testGetValueWithStringDefault() {
        ConfigValue<Integer> cfga = config.access("tck.config.test.javaconfig.configvalue.withdefault.notexisting")
                .as(Integer.class)
                .withStringDefault("1234");

        Assert.assertEquals(cfga.getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testIntegerConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.integer").as(Integer.class).getValue(), Integer.valueOf(1234));
    }

    @Test
    public void testLongConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.long").as(Long.class).getValue(), Long.valueOf(1234567890123456L));
    }

    @Test
    public void testFloatConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.float").as(Float.class).getValue(), Float.valueOf(12.34f));
    }

    @Test
    public void testDoubleonverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.double").as(Double.class).getValue(), Double.valueOf(12.34567890123456));
    }

    @Test
    public void testBooleanConverter() {
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.true_mixedcase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.false").as(Boolean.class).getValue(), Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.one").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.zero").as(Boolean.class).getValue(), Boolean.FALSE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.seventeen").as(Boolean.class).getValue(), Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.yes_mixedcase").as(Boolean.class).getValue(), Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.y_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.ja").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.ja_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.ja_mixedcase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.no_mixedcase").as(Boolean.class).getValue(), Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.j").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.j_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.n_uppercase").as(Boolean.class).getValue(), Boolean.FALSE);

        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.oui").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.oui_uppercase").as(Boolean.class).getValue(), Boolean.TRUE);
        Assert.assertEquals(config.access("tck.config.test.javaconfig.configvalue.boolean.oui_mixedcase").as(Boolean.class).getValue(), Boolean.TRUE);
    }

    @Test
    public void testCacheFor() throws Exception {
        String key = "tck.config.cachefor.key";
        System.setProperty(key, "firstvalue");
        ConfigValue<String> val = config.access(key).cacheFor(30, TimeUnit.MILLISECONDS);
        Assert.assertEquals(val.getValue(), "firstvalue");

        // immediately change the value
        System.setProperty(key, "secondvalue");

        // we should still see the first value, because it is cached!
        Assert.assertEquals(val.getValue(), "firstvalue");

        // but now let's wait a bit
        Thread.sleep(40);
        Assert.assertEquals(val.getValue(), "secondvalue");
    }

    @Test
    public void testWithVariable() throws Exception {
        ConfigValue<String> val = config.access("tck.config.test.javaconfig.configvalue.withvariable.key").evaluateVariables(true);
        Assert.assertEquals(val.getValue(), "This key needs the perfect value!");
    }
}
