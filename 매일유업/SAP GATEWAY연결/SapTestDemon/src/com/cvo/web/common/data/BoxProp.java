package com.cvo.web.common.data;

import java.util.Properties;

@SuppressWarnings("serial")
public class BoxProp extends Properties {

	/**
	 * Instantiates a new box prop.
	 */
	public BoxProp() {
	}

	/**
	 * Instantiates a new box prop.
	 *
	 * @param properties the properties
	 */
	public BoxProp(Properties properties) {
		super(properties);
	}

	/**
	 * Put all.
	 *
	 * @param properties the properties
	 */
	public void putAll(Properties properties) {
		putAll(properties);
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String getString(String key) {
		return getProperty(key);
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public String getString(String key, String defaultValue) {
		return getProperty(key, defaultValue);
	}

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the int
	 */
	public int getInt(String key, int defaultValue) {
		String val = getProperty(key);
		if(val == null) {
			return defaultValue;
		}
		try {
			return Integer.parseInt(val);
		} catch(Exception e) {
			return defaultValue;
		}
	}

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @return the int
	 */
	public int getInt(String key) {
		return getInt(key, 0);
	}

	/**
	 * Gets the boolean.
	 *
	 * @param id the id
	 * @return the boolean
	 */
	public boolean getBoolean(String id) {
		Object value = get(id);
		if(value instanceof Boolean) {
			return ((Boolean)value).booleanValue();
		} else if(value instanceof String){
			return Boolean.valueOf((String)value);
		}
		return false;
	}

}
