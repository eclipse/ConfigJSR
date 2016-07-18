package javx.config.spi;

/**
 * <p>A very simple interface for conversion of configuration values from String to any Java type.</p>
 *
 * <p>A Converter can specify a {@link javax.annotation.Priority}.
 * If no priority is explicitly assigned, the value of 100 is assumed.</p>
 *
 * <p>If multiple Converter get found the one with the highest priority will be used.</p>
 *
 * <p>The Converter for the following types are automatically enabled:
 * <ul>
 *     <li>Boolean, values for {@code true}: (case insensitive) &quot;true&quot;, &quot;1&quot;, &quot;YES&quot;, &quot;Y&quot; &quot;JA&quot; &quot;J&quot;, &quot;OUI&quot;</li>
 *     <li>Integer</li>
 *     <li>Long</li>
 *     <li>Float, a dot '.' is used to separate the fractional digits</li>
 *     <li>Double, a dot '.' is used to separate the fractional digits</>
 * </ul>
 *
 * </p>
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:gpetracek@apache.org">Gerhard Petracek</a>
 */
public interface Converter<T> {
    /**
     * Returns the converted value of the configuration entry.
     * @param value The String property value to convert
     * @return Converted value
     */
    T convert(String value);

}
