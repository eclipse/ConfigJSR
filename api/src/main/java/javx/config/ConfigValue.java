package javx.config;

import javx.config.spi.Converter;

import java.util.concurrent.TimeUnit;

/**
 * Accessor to a configured value.
 * It follows a builder pattern.
 *
 * Accessing the configured value is finally done via {@link #getValue()}
 *
 * @author <a href="mailto:struberg@apache.org">Mark Struberg</a>
 * @author <a href="mailto:gpetracek@apache.org">Gerhard Petracek</a>
 * @author <a href="mailto:rsmeral@jboss.org">Ron Smeral</a>
 */
public interface ConfigValue<T> {

    /**
     * Sets the type of the configuration entry to the given class and returns this builder.
     * The default type of a ConfigValue is {@code String}.
     *
     * @param clazz The target type
     * @param <N> The target type
     * @return This builder as a typed ConfigValue
     */
    <N> ConfigValue<N> as(Class<N> clazz);

    /**
     * Sets the default value to use in case the resolution returns null.
     * @param value the default value
     * @return This builder
     */
    ConfigValue<T> withDefault(T value);

    /**
     * Sets the default value to use in case the resolution returns null. Converts the given String to the type of
     * this resolver using the same method as used for the configuration entries.
     * @param value string value to be converted and used as default
     * @return This builder
     */
    ConfigValue<T> withStringDefault(String value);

    /**
     * Specify that a resolved value will get cached for a certain amount of time.
     * After the time expires the next {@link #getValue()} will again resolve the value
     * from the underlying {@link Config}.
     *
     * @param value the amount of the TimeUnit to wait
     * @param timeUnit the TimeUnit for the value
     *
     * @return This builder
     */
    ConfigValue<T> cacheFor(long value, TimeUnit timeUnit);

    /**
     * Whether to evaluate variables in configured values.
     * A variable starts with '${' and ends with '}', e.g.
     * <pre>
     * mycompany.some.url=${myserver.host}/some/path
     * myserver.host=http://localhost:8081
     * </pre>
     * If 'evaluateVariables' is enabled, the result for the above key
     * {@code "mycompany.some.url"} would be:
     * {@code "http://localhost:8081/some/path"}
     * @param evaluateVariables whether to evaluate variables in values or not
     * @return This builder
     */
    ConfigValue<T> evaluateVariables(boolean evaluateVariables);

    /**
     * Appends the resolved value of the given property to the key of this builder.
     * TODO further explain.
     * @return This builder
     */
    ConfigValue<T> withLookupChain(String... postfixNames);

    /**
     * Whether to log picking up any value changes as INFO.
     *
     * @return This builder
     */
    ConfigValue<T> logChanges(boolean logChanges);

    /**
     * Returns the converted resolved filtered value.
     * @return the resolved value
     */
    T getValue();

    /**
     * Returns the key given in {@link Config#access(String)}.
     * @return the original key
     */
    String getKey();

    /**
     * Returns the actual key which led to successful resolution and corresponds to the resolved value. This applies
     * only when {@link #withLookupChain(String...)} is used.
     * Otherwise the resolved key should always be equal to the original key.
     * This method is provided for cases, when arameterized resolution is
     * requested but the value for such appended key is not found and some of the fallback keys is used.
     *
     * This should be called only after calling {@link #getValue()} otherwise the value is undefined (but likely
     * null).
     */
    String getResolvedKey();

    /**
     * Returns the default value provided by {@link #withDefault(Object)} or {@link #withStringDefault(String)}.
     * Returns null if no default was provided.
     * @return the default value or {@code null}
     */
    T getDefaultValue();
}
