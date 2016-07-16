package org.apache.geronimo.config.tck;

import javx.config.Config;
import javx.config.ConfigProvider;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 */
public class ConfigFilterTest {

    @Test
    public void testConfigFiltering() {
        Config config = ConfigProvider.getConfig();

        // unfiltered
        Assert.assertEquals(config.getValue("tck.config.test.javaconfig.configfilter.my.secret"), "SOME_SECRET");

        // filtered
        Assert.assertEquals(config.getValue("tck.config.test.javaconfig.configfilter.my.password"), "some_password");
    }

    @Test
    public void testConfigFiltering_ForLogging() {
        Config config = ConfigProvider.getConfig();

        Assert.assertEquals(
                config.filterConfigValueForLog("tck.config.test.javaconfig.configfilter.my.password",
                        config.getValue("tck.config.test.javaconfig.configfilter.my.password")), "*******");
    }
}
