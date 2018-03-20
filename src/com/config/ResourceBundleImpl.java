package com.config;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.commons.lang3.StringEscapeUtils;

import com.java.utils.Validate;

/**
 * Resource bundle backed by a map that does not throw <code>MissingResourceException</code>s.
 */
public final class ResourceBundleImpl extends ResourceBundle {

	/** The map of keys to resources. */
	private final Map<String, Object> map = new HashMap<String, Object>();

	/** The locale. */
	private final Locale locale;

	/**
	 * Creates a new instance.
	 * 
	 * @param locale
	 *            the locale.
	 */
	public ResourceBundleImpl(final Locale locale) {

		super();

        // Standard properties file
		final ResourceBundle standardBundle = getBundle("com.netdimen.locale.standard", locale);
		putAll(standardBundle);
		
		
		this.locale = standardBundle.getLocale();
	}

	/**
	 * Adds resources from the specified bundle to the specified map.
	 * 
	 * @param bundle
	 *            the resource bundle from which resources are to be added.
	 */
	private final void putAll(final ResourceBundle bundle) {

		for (final Enumeration<String> e = bundle.getKeys(); e.hasMoreElements();) {
			final String key = e.nextElement();
			map.put(key, bundle.getObject(key));
		}
	}


	
	
	/**
	 * Returns an enumeration of the keys. The enumeration returned contains precisely the keys from the underlying map.
	 * 
	 * @return an enumeration of the keys.
	 */
	public final Enumeration<String> getKeys() {

		return Collections.enumeration(map.keySet());
	}

	/**
	 * Gets an object from the resource bundle. The object returned is precisely the value associated with the key in
	 * the underlying map. If the map does not contain the key, then the key itself is returned; hence
	 * <code>java.util.MissingResourceException<code>s are never thrown by this
	 * method.
	 */
	protected final Object handleGetObject(final String key) {

		return Validate.isBlank(key) ? "" : (map.containsKey(key) ? map.get(key) : key);
	}

	/**
	 * Returns the locale for this resource bundle.
	 * 
	 * @return the locale for this resource bundle.
	 */
	public final Locale getLocale() {

		return locale;
	}

	public String getEscapedJavascriptString(final String key) {

		return StringEscapeUtils.escapeEcmaScript(getString(key));
	}
	
	public synchronized final void replace(final String key, final String value) {
	    map.put(key, value);
	}
}
