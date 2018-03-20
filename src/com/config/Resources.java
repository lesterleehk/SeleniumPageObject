package com.config;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.abstractclasses.TestObject;
import com.google.common.base.Function;

/**
 * Manages message strings.
 */
// Noninstantiable utility class
public final class Resources {

	/**
	 * Mapping of locales to resource bundles, according to the document, using ConcurrentHashMap don't need to lock
	 * when accessing
	 */
	private static final Map<Locale, ResourceBundleImpl> bundleMap = new ConcurrentHashMap<Locale, ResourceBundleImpl>();

	// Suppress default constructor for noninstantiability
	private Resources() {

		throw new AssertionError();
	}

	/**
	 * Returns a resource bundle appropriate for the specified locale. The resource bundles that are returned never
	 * throw <code>java.util.MissingResourceException</code>s; rather, if no resource can be found, the key itself is
	 * returned.
	 * 
	 * @param locale
	 *            generic Java abstraction for the locale.
	 * @return a resource bundle appropriate for the specified language.
	 */
	public static final ResourceBundleImpl bundle(final Locale locale) {

		final Locale nonNullLocale = TestObject.defaultIfNull(locale, Locale.getDefault());

		/*
		 * It's not sufficient to synchronize the individual map operations; this whole code block must be synchronized.
		 */
		synchronized (bundleMap) {
			if (bundleMap.containsKey(nonNullLocale))
				return bundleMap.get(nonNullLocale);
			else {
				final ResourceBundleImpl result = new ResourceBundleImpl(nonNullLocale);
				bundleMap.put(nonNullLocale, result);
				return result;
			}
		}
	}

	/**
	 * Returns the EKP message with the specified key, appropriate for the specified locale.
	 * 
	 * @param key
	 *            the message key.
	 * @param locale
	 *            the locale.
	 */
	public static final String string(final String key, final Locale locale) {

		return bundle(locale).getString(key);
	}

	public static final String string(final String key, final Locale locale, final Object... parameters) {

		return new MessageFormat(string(key, locale)).format(parameters);
	}

	/**
	 * It will return the corresponding Locale given a country code, if cannot be determined, it will return the default
	 * locale.
	 * 
	 * @param countryCode
	 *            3-letter ISO country Code
	 * @return the corresponding Locale given a country code, if cannot be determined, it will return the default
	 *         locale.
	 */
	public static final Locale locale(final String countryCode) {

		final Locale[] locales = Locale.getAvailableLocales();
		for (int i = 0; i < locales.length; i++)
			if (locales[i].getISO3Country().equalsIgnoreCase(countryCode))
				return locales[i];
		return Locale.getDefault();
	}

	public static final Function<Locale, String> message(final String key) {

		return new Function<Locale, String>() {

			public final String apply(final Locale locale) {

				return string(key, locale);
			}
		};
	}

}
