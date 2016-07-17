package javx.config.spi;

/**
 * A very simple interface for conversion of configuration values from String to any Java type.
 *
 * A Converter can specify a {@link javax.annotation.Priority}.
 * If no priority is explicitly assigned, the value of 100 is assumed.
 *
 * If multiple Converter get found the one with the highest priority will be used.
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
