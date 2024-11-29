package com.cvo.web.common.data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cvo.web.common.util.DateUtil;
import com.cvo.web.common.util.MapUtil;
import com.cvo.web.common.util.StringUtil;

@SuppressWarnings("serial")
public abstract class BaseBox extends LinkedHashMap<String, Object> {

	protected Logger log = LoggerFactory.getLogger(this.getClass());

	/**
	 * Instantiates a new base box.
	 */
	public BaseBox() {
		super();
	}

	/**
	 * Instantiates a new base box.
	 *
	 * @param map the map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public BaseBox(Map map) {
		super(map);
	}

	/**
	 * Instantiates a new base box.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	public BaseBox(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Instantiates a new base box.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public BaseBox(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Gets the list.
	 *
	 * @param key the key
	 * @return the box
	 */
//	@SuppressWarnings("unchecked")
//	public List<Box> getList(String key) {
//		return (List<Box>)this.get(key);
//	}

	/**
	 * Gets the box.
	 *
	 * @param key the key
	 * @return the box
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Box getBox(String key) {
		Object obj = this.get(key);
		if(obj != null) {
			if(obj instanceof Box) {
				return (Box)obj;
			} else if(obj instanceof Map) {
				Box box = new Box((Map)obj);
				this.put(key, box);
				return box;
			}
		}
		return null;
	}

	/**
	 * Gets the list.
	 *
	 * @param key the key
	 * @return the box
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<Box> getList(String key) {
		List list = (List)this.get(key);
		Object obj = null;
		if (list != null) {
			for (int idx = 0; idx < list.size(); idx++) {
				obj = list.get(idx);
				if (obj instanceof Map) {
					Box box = new Box((Map)obj);
					list.set(idx, box);
				}
			}
			return list;
		}
		return null;
	}

	/**
	 * String 배열을 list로 변환하여 반환한다
	 *
	 * @param key the key
	 * @return the box
	 */
	public List<Box> arryToList(String key) {
		List<Box> list = new ArrayList<Box>();
		if ("".equals(this.nvl(key))) { return list; }

		String[] arry = this.getArry(key);
		log.debug("len:"+arry.length);
		for (int idx = 0; idx < arry.length; idx++)  {
			log.debug("val:"+arry[idx]);
			Box rowBox = new Box();
			rowBox.put("key", arry[idx]);
			list.add(rowBox);
		}
		return list;
	}

	/**
	 * Gets the string.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String getString(String key) {
		return MapUtil.getString(this, key);
	}

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @return the int
	 */
	public int getInt(String key) {
		return MapUtil.getInt(this, key);
	}

	/**
	 * Gets the int.
	 *
	 * @param key the key
	 * @return the int
	 */
	public BigInteger getBigInt(String key) {
		return new BigInteger(MapUtil.getString(this, key));
	}

	/**
	 * Gets the long.
	 *
	 * @param key the key
	 * @return the long
	 */
	public long getLong(String key) {
		return MapUtil.getLong(this, key);
	}

	/**
	 * Gets the double.
	 *
	 * @param key the key
	 * @return the double
	 */
	public double getDouble(String key) {
		return MapUtil.getDouble(this, key);
	}

	/**
	 * Gets the big decimal.
	 *
	 * @param key the key
	 * @return the big decimal
	 */
	public BigDecimal getBigDecimal(String key) {
		return MapUtil.getBigDecimal(this, key);
	}

	/**
	 * Gets the boolean.
	 *
	 * @param key the key
	 * @return the boolean
	 */
	public boolean getBoolean(String key) {
		return MapUtil.getBoolean(this, key);
	}

	/**
	 * Date 및 Timestamp값을 반환하며 값이 없는 경우는 null을 반환한다.
	 *
	 * @param key the key
	 * @return the date
	 */
	public Date getDate(String key) {
		Object src = this.get(key);
		if(src == null) {
			return null;
		}
		if(src instanceof Date) {
			return (Date)src;
		} else if(src instanceof Timestamp) {
			return (Timestamp)src;
		}
		return null;
	}

	/**
	 * Format.
	 *
	 * @param key the key
	 * @param format the format
	 * @return the string
	 */
	public String format(String key, String format) {
		Object src = this.get(key);
		if(src == null) {
			return null;
		}
		if(src instanceof Date) {
			return DateUtil.format((Date)src, format);
		} else if(src instanceof Timestamp) {
			return DateUtil.format((Timestamp)src, format);
		}
		return null;
	}

	/**
	 * 값이 Date객체 혹은 Timestamp객체인 경우 dateFormat대로 변환된다.
	 *
	 * @param key the key
	 * @param dateFormat the date format
	 * @return the date str
	 */
	public String getDateStr(String key, String dateFormat) {
		return format(key, dateFormat);
	}

	/* (non-Javadoc)
	 * @see java.util.HashMap#clone()
	 */
	@Override
	public BaseBox clone() {
		return (BaseBox)super.clone();
	}

	/**
	 * Gets the parameter.
	 *
	 * @param name the name
	 * @return the parameter
	 */
	public String getParameter(String name) {
		Object value = get(name);
		if (value == null) {
			return null;
		} else if (value instanceof CharSequence) {
			return value.toString();
		} else if (value instanceof String []) {
			String[] array = (String[])value;
			if (array.length > 0) {
				return array[0];
			} else {
				return null;
			}
		}
		return value.toString();
	}

	/**
	 * Gets the parameter.
	 *
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the parameter
	 */
	public String getParameter(String name, String defaultValue) {
		return StringUtil.nullToStr(getParameter(name), defaultValue);
	}

	/**
	 * Null to string.
	 *
	 * @param name the name
	 * @return the string
	 */
	@Deprecated
	public String nullToString(String name) {
		return getParameter(name, "");
	}

	/**
	 * Null to string.
	 *
	 * @param name the name
	 * @param defaultValue the default value
	 * @return the string
	 */
	@Deprecated
	public String nullToString(String name, String defaultValue) {
		return getParameter(name, defaultValue);
	}

	/**
	 * Gets the parameter values.
	 *
	 * @param name the name
	 * @return the parameter values
	 */
	public String[] getParameterValues(String name) {
		Object value = get(name);
		String[] array = null;
		if (value == null) {
			return array;
		} else if(value instanceof CharSequence) {
			array = new String[]{value.toString()};
		} else if (value instanceof String []) {
			array = (String[])value;
		}
		return array;
	}

	/**
	 * Gets the parameter values.
	 *
	 * @param name the name
	 * @return the parameter values
	 */
	public String[] getArry(String name) {
		return this.getParameterValues(name);
	}

	public String[] getArry(String name, String div) {
		if (this.nvl(name) == null || this.nvl(name).isEmpty()) {
			return null;
		}
		return this.nvl(name).split(div);
	}

	/**
	 * Nvl.
	 *
	 * @param key the key
	 * @param defaultValue the default value
	 * @return the string
	 */
	public String nvl(String key, String defaultValue) {
		return StringUtil.nvl(getString(key), defaultValue);
	}

	/**
	 * Nvl.
	 *
	 * @param key the key
	 * @return the string
	 */
	public String nvl(String key) {
		return nvl(key, "");
	}


}
