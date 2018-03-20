package com.java.utils;



/**
 * <p>
 * provides several commonly used data validation methods.
 * </p>
 */
// Noninstantiable utility class
public final class Validate {

	// Suppress default constructor for noninstantiability
	private Validate() {

		throw new AssertionError();
	}

	public static final boolean isEquals(final String s1, final String s2) {

		if (isBlank(s1) && isBlank(s2)) {
			return true;
		} else if (isBlank(s1)) {
			return false;
		}
		return s1.equals(s2);
	}

	public static final boolean isEquals(final Object o1, final Object o2) {

		if (o1 == null && o2 == null) {
			return true;
		} else if (o1 == null) {
			return false;
		}
		return o1.equals(o2);
	}

	/**
	 * returns true if the field is null or spaces.
	 */
	public static final boolean isBlank(final String s) {

		return s == null || s.trim().equals("");
	}

	/**
	 * returns true if the field is null.
	 */
	public static final boolean isNull(final String s) {

		return s == null || s.trim().equals("");
	}

	/**
	 * returns true if the array is null or length of 0.
	 */
	public static final boolean isEmpty(final Object[] s) {

		return s == null || s.length == 0;
	}

}
