package com.cvo.web.common.util;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.google.common.base.CaseFormat;
import com.google.common.base.Throwables;
import com.cvo.web.common.constants.CmnConst;
import com.cvo.web.common.data.Box;
import com.cvo.web.common.exception.BaseException;

public class StringUtil {

	/**
	 * Instantiates a new string util.
	 */
	private StringUtil() {
	}

	/**
	 * To string.
	 *
	 * @param o the o
	 * @return the string
	 */
	public static String toString(Object o) {
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	/**
	 * Checks if is empty.
	 *
	 * @param value the value
	 * @return true, if is empty
	 */
	public static boolean isEmpty(String value) {
		return (value == null || value.length() == 0);
	}

	/**
	 * Checks if is not empty.
	 *
	 * @param value the value
	 * @return true, if is not empty
	 */
	public static boolean isNotEmpty(String value) {
		return !isEmpty(value);
	}

	/**
	 * Trim.
	 *
	 * @param value the value
	 * @return the string
	 */
	public static String trim(String value) {
		if(value == null) {
			return null;
		}
		return value.trim();
	}

	/**
	 * Gets the only num.
	 *
	 * @param value the value
	 * @return the only num
	 */
	public static String getOnlyNum(String value) {
		String trimmedValue = StringUtil.trim(value);
		if(StringUtil.isEmpty(trimmedValue)) {
			return value;
		}
		return value.replaceAll("[^0-9]", "");
	}

	/**
	 * Gets the amount num.
	 *
	 * @param value the value
	 * @return the amount num
	 */
	public static String getAmountNum(String value) {
		String trimmedValue = StringUtil.trim(value);
		if(StringUtil.isEmpty(trimmedValue)) {
			return value;
		}
		return value.replaceAll("[^0-9\\.\\-]", "");
	}

	/**
	 * 문자열이 len 사이즈보다 작을 경우 pad 문자열을 len 길이만큼 왼쪽에 채움.
	 *
	 * @param value 원본문자열
	 * @param len 문자열길이
	 * @param pad 채울문자
	 * @return len 만큼 pad로 채워진 문자열
	 */
	public static String lpad(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		return StringUtils.leftPad(value, len, pad);
	}

	/**
	 * Rpad.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @return the string
	 */
	public static String rpad(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		return StringUtils.rightPad(value, len, pad);
	}

	/**
	 * Rpad Fix.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @return the string
	 */
	public static String rpadFix(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}

		String rtnStr = null;
		if (value.length() > len) {
			rtnStr = value.substring(value.length() - len, value.length());
		} else {
			rtnStr = value;
		}

		return StringUtil.rpad(rtnStr, len, pad);
	}

	/**
	 * Lpad Fix.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @return the string
	 */
	public static String lpadFix(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}

		String rtnStr = null;
		if (value.length() > len) {
			rtnStr = value.substring(value.length() - len, value.length());
		} else {
			rtnStr = value;
		}

		return StringUtil.lpad(rtnStr, len, pad);
	}

	/**
	 * Rpad btye Fix.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @return the string
	 */
	public static String rpadByteFix(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		if("".equals(pad)) {
			pad = " ";
		}

		String rtnStr = null;
		if (value.length() > len) {
			rtnStr = value.substring(value.length() - len, value.length());
		} else {
			rtnStr = value;
		}

		return StringUtil.rpadByte(rtnStr, len, pad.getBytes()[0], CmnConst.EUCKR);
	}

	/**
	 * Lpad byte Fix.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @return the string
	 */
	public static String lpadByteFix(String value, int len, String pad) {
		if(value == null) {
			value = "";
		}
		if("".equals(pad)) {
			pad = " ";
		}

		String rtnStr = null;
		if (value.length() > len) {
			rtnStr = value.substring(value.length() - len, value.length());
		} else {
			rtnStr = value;
		}

		return StringUtil.lpadByte(rtnStr, len, pad.getBytes()[0], CmnConst.EUCKR);
	}
	/**
	 * Lpad byte.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @param charset the charset
	 * @return the string
	 */
	public static String lpadByte(String value, int len, byte pad, String charset) {
		if(value == null) {
			value = "";
		}
		try {
			byte[] valueByte = value.getBytes(charset);
			int valueLen = valueByte.length;
			if(valueLen > len) {
				value = StringUtil.subByte(value, len, charset);
				valueByte = value.getBytes(charset);
				valueLen = value.getBytes(charset).length;
			}
			if(valueLen < len) {
				for(int i=valueLen; i<len; i++) {
					valueByte = ArrayUtils.add(valueByte, 0, pad);
				}
			}
			value = new String(valueByte, charset);
		} catch (Exception e) {
			throw new BaseException(e);
		}
		return value;
	}

	/**
	 * Rpad byte.
	 *
	 * @param value the value
	 * @param len the len
	 * @param pad the pad
	 * @param charset the charset
	 * @return the string
	 */
	public static String rpadByte(String value, int len, byte pad, String charset) {
		if(value == null) {
			value = "";
		}
		try {
			byte[] valueByte = value.getBytes(charset);
			int valueLen = valueByte.length;
			if(valueLen > len) {
				value = StringUtil.subByte(value, len, charset);
				valueByte = value.getBytes(charset);
				valueLen = value.getBytes(charset).length;
			}
			if(valueLen < len) {
				for(int i=valueLen; i<len; i++) {
					valueByte = ArrayUtils.add(valueByte, pad);
				}
			}
			value = new String(valueByte, charset);
		} catch (Exception e) {
			throw new BaseException(e);
		}
		return value;
	}

	/**
	 * Replace all.
	 *
	 * @param src the src
	 * @param from the from
	 * @param to the to
	 * @return the string
	 */
	public static String replaceAll(String src, String from, String to) {
		if (isEmpty(src)) {
			return src;
		}
		if (isEmpty(from)) {
			return src;
		}
		if (to == null) {
			to = "";
		}
		StringBuffer buf = new StringBuffer();
		for (int pos;(pos = src.indexOf(from)) >= 0;) {
			buf.append(src.substring(0, pos));
			buf.append(to);
			src = src.substring(pos + from.length());
		}
		buf.append(src);
		return buf.toString();
		// return src.replaceAll(regex, replacement);
	}

    /**
     * 주어진 문자열에서 지정한 문자열값을 지정한 문자열로 치환후 그결과 문자열을 리턴함.
     *
     * @param src the src
     * @param off the off
     * @param from the from
     * @param to the to
     * @return 문자열
     */
    public static String replace(String src, int off, String from, String to) {
    	if (isEmpty(src)) {
			return src;
		}
		if (isEmpty(from)) {
			return src;
		}
        off = src.indexOf(from, off);
        if(off==-1) {
        	return src;
        }
        if (to == null) {
			to = "";
		}
        StringBuffer buff = new StringBuffer(src);
        buff.replace(off, off+from.length(), to);
        return buff.toString();
    }

    /**
     * Null to string.
     *
     * @param value the value
     * @return the string
     */
    public static String nullToStr(String value) {
        return nullToStr(value, "");
    }

    /**
     * Null to string.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the string
     */
    public static String nullToStr(String value, String defaultValue) {
    	if(isEmpty(value)) {
    		return defaultValue;
    	}
    	return value;
    }

    /**
     * Nvl.
     *
     * @param value the value
     * @return the string
     */
    public static String nvl(String value) {
        return nvl(value, "");
    }

    /**
     * Nvl.
     *
     * @param value the value
     * @param defaultValue the default value
     * @return the string
     */
    public static String nvl(String value, String defaultValue) {
    	if(isEmpty(value) || "null".equals(value)) {
    		return defaultValue;
    	}
    	return value;
    }

    /**
     * Nvl.
     *
     * @param valueObj the value obj
     * @return the string
     */
    public static String nvl(Object valueObj) {
    	return nvl(valueObj, "");
    }

    /**
     * Nvl.
     *
     * @param valueObj the value obj
     * @param defaultValue the default value
     * @return the string
     */
    public static String nvl(Object valueObj, String defaultValue) {
    	String value = toString(valueObj);
    	if(isEmpty(value) || "null".equals(value)) {
    		return defaultValue;
    	}
    	return value;
    }

    /**
     * Checks if is min byte.
     *
     * @param value the value
     * @param minByte the min byte
     * @return true, if is min byte
     */
    public static boolean isMinByte(String value, int minByte) {
		if(StringUtil.isEmpty(value)) {
			return (minByte<1);
		}
		boolean isValid = false;
		int byteLen = 0;
		int asc;
		int length = value.length();
		for(int i=0; i<length; i++) {
			asc = (int)value.charAt(i);
			if(asc > 127) {
				byteLen += 2;
			} else {
				byteLen++;
			}
			if(byteLen >= minByte) {
				isValid = true;
				break;
			}
		}
		return isValid;
	}

	/**
	 * Checks if is max byte.
	 *
	 * @param value the value
	 * @param maxByte the max byte
	 * @return true, if is max byte
	 */
	public static boolean isMaxByte(String value, int maxByte) {
		if(StringUtil.isEmpty(value)) {
			return (maxByte<1);
		}
		boolean isValid = true;
		int byteLen = 0;
		int asc;
		int length = value.length();
		for(int i=0; i<length; i++) {
			asc = (int)value.charAt(i);
			if(asc > 127) {
				byteLen += 2;
			} else {
				byteLen++;
			}
			if(byteLen > maxByte) {
				isValid = false;
				break;
			}
		}
		return isValid;
	}

	/**
	 * input String 값을 split 을 이용해서 자르고, trim 하여 배열로 변환.
	 *
	 * @param input the input
	 * @param delemeter the delemeter
	 * @return the string[]
	 */
	public static String[] splitStringTrim(String input, String delemeter) {
		if ( input == null ) {
			return null;
		}
		String[] arr = input.split(delemeter);
		for(int i=0; i<arr.length; i++) {
			arr[i] = trim(arr[i]);
		}
		return arr;
	}

	/**
	 * input String 값을 split 을 이용해서 자르고,마지막 값만 변환.
	 *
	 * @param input the input
	 * @param delemeter the delemeter
	 * @return the string[]
	 */
	public static String splitLast(String input, String delemeter) {
		if ( input == null ) {
			return null;
		}
		String[] arr = input.split(delemeter);
		for(int i=0; i<arr.length; i++) {
			arr[i] = trim(arr[i]);
		}

		return arr[arr.length-1];
	}

	/**
	 * Camel to lower.
	 *
	 * @param name the name
	 * @return the string
	 */
	public static String camelToLower(String name) {
    	return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, name);
    }

	/**
	 * Camel to lower.(map)
	 *
	 * @param name the name
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	public static Map<String, Object> toCamel(Map<String, Object> map) {
		if (null == map || map.isEmpty()) {
			return map;
		}

		Map<String, Object> rtnMap = new HashMap<String, Object>();
		for (String key : map.keySet()) {
			if (map.get(key) instanceof String) {
				String keyStr = key.toString();
//				System.out.println(toCamel(keyStr));
				rtnMap.put(toCamel(keyStr), map.get(keyStr));
			} else if (map.get(key) instanceof ArrayList) {
				List<Map<String, Object>> list = (ArrayList<Map<String, Object>>)map.get(key);
				List<Map<String, Object>> rtnInnerList = new ArrayList<Map<String, Object>>();
				if (null != list && !list.isEmpty()) {
					for (Map<String, Object> innerMap : list) {
						if (null == innerMap || innerMap.isEmpty()) {
							break;
						}
						Map<String, Object> rtnInnerMap = new HashMap<String, Object>();
						for (String innerKey : innerMap.keySet()) {
							String innerKeyStr = innerKey.toString();
	//						System.out.println(toCamel(innerKeyStr));
							rtnInnerMap.put(toCamel(innerKeyStr), innerMap.get(innerKeyStr));
						}
						rtnInnerList.add(rtnInnerMap);
					}
				}
				rtnMap.put(toCamel(key), rtnInnerList);
			}
		}
		return rtnMap;
    }

	/**
	 * Camel to lower.(map)
	 *
	 * @param name the name
	 * @return the string
	 */
	@SuppressWarnings("unchecked")
	public static Box toCamel(Box box) {
		if (null == box || box.isEmpty()) {
			return box;
		}

		Box rtnBox = new Box();
		for (String key : box.keySet()) {
			if (box.get(key) instanceof String) {
				String keyStr = key.toString();
//				System.out.println(toCamel(keyStr));
				rtnBox.put(toCamel(keyStr), box.get(keyStr));
			} else if (box.get(key) instanceof ArrayList) {
				List<Box> list = (ArrayList<Box>)box.get(key);
				List<Box> rtnInnerList = new ArrayList<Box>();
				if (null != list && !list.isEmpty()) {
					for (Map<String, Object> innerMap : list) {
						if (null == innerMap || innerMap.isEmpty()) {
							break;
						}
						Box rtnInnerBox = new Box();
						for (String innerKey : innerMap.keySet()) {
							String innerKeyStr = innerKey.toString();
	//						System.out.println(toCamel(innerKeyStr));
							rtnInnerBox.put(toCamel(innerKeyStr), innerMap.get(innerKeyStr));
						}
						rtnInnerList.add(rtnInnerBox);
					}
				}
				rtnBox.put(toCamel(key), rtnInnerList);
			}
		}
		return rtnBox;
    }

    /**
     * Camel to upper.
     *
     * @param name the name
     * @return the string
     */
    public static String camelToUpper(String name) {
    	return CaseFormat.LOWER_CAMEL.to(CaseFormat.UPPER_UNDERSCORE, name);
    }

    /**
     * underscore ('_') 가 포함되어 있는 문자열을 Camel Case ( 낙타등
     * 표기법 - 단어의 변경시에 대문자로 시작하는 형태. 시작은 소문자) 로 변환해주는
     * utility 메서드 ('_' 가 나타나지 않고 첫문자가 대문자인 경우도 변환 처리
     * 함.)
     * @param underScore
     *        - '_' 가 포함된 변수명
     * @return Camel 표기법 변수명
     */
    public static String toCamel(String underScore) {

        // '_' 가 나타나지 않으면 이미 camel case 로 가정함.
        // 단 첫째문자가 대문자이면 camel case 변환 (전체를 소문자로) 처리가
        // 필요하다고 가정함. --> 아래 로직을 수행하면 바뀜
        if (underScore.indexOf('_') < 0
            && Character.isLowerCase(underScore.charAt(0))) {
            return underScore;
        }
        StringBuilder result = new StringBuilder();
        boolean nextUpper = false;
        int len = underScore.length();

        for (int i = 0; i < len; i++) {
            char currentChar = underScore.charAt(i);
            if (currentChar == '_') {
                nextUpper = true;
            } else {
                if (nextUpper) {
                    result.append(Character.toUpperCase(currentChar));
                    nextUpper = false;
                } else {
                    result.append(Character.toLowerCase(currentChar));
                }
            }
        }
        return result.toString();
    }

    /**
	 * Left pad str.
	 *
	 * @param str the str
	 * @param size the size
	 * @param strFillText the str fill text
	 * @return the string
	 */
	public static String leftPadStrForEuckr(String str, int size, String strFillText) {
    	if (str == null){
    		str = " ";
    	}

    	byte[] bytes = null;

    	try {
		    bytes  = str.getBytes("EUC-KR");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

    	if(bytes.length > size) {
    		str = str.substring(0, size);
    	} else {
    		for(int i = bytes.length; i < size; i++) {
    			str = strFillText + str;
    		}
    	}

        return str;
    }


    /**
     * len til String String padStr the left side of the filling method.
     *
     * @param num int
     * @param size int
     * @param strFillText String
     * @return String
     */
    public static String leftPadStrForEuckr(int num, int size, String strFillText) {
        String src = Integer.toString(num);
        return leftPadStrForEuckr(src, size, strFillText);
    }



    /**
     * len til String String padStr the left side of the filling method.
     *
     * @param str String
     * @param size int
     * @param strFillText String
     * @return String
     */
    public static String rightPadStrForEuckr(String str, int size, String strFillText) {
    	byte[] bytes = null;

    	if (str == null){
    		str = " ";
    	}
    	try {
		    bytes  = str.getBytes(CmnConst.EUCKR);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

    	if(bytes.length > size) {
//    		str = str.substring(0, size);
    		str = StringUtil.rpadByte(str, size, (byte)strFillText.charAt(0), CmnConst.EUCKR);
    	} else {
    		for(int i =bytes.length; i < size; i++) {
    			str += strFillText;
    		}
    	}

        return str;
    }


    /**
     * len til String String padStr the right side of the filling method.
     *
     * @param num the num
     * @param size the size
     * @param strFillText the str fill text
     * @return String
     */
    public static String rigthPadStrForEuckr(int num, int size, String strFillText) {
          String src = Integer.toString(num);
          return rightPadStrForEuckr(src, size, strFillText);
    }


    /**
     * Removes the utf8 spcial char.
     *
     * @param inStr the in str
     * @return the string
     */
    public static String removeUtf8SpcialChar(String inStr) {
    	// EUC-KR로 인코딩이 가능한 문자만 Append
    	String outStr = "";

        if(inStr == null || inStr.trim().equals("")){
            return "";
        }

    	Charset charSet = Charset.forName(CmnConst.EUCKR);
        CharsetEncoder charEnc = charSet.newEncoder();

	    for( int i=0; i< inStr.length(); i++){
	        char ch = inStr.charAt(i);

	        if(charEnc.canEncode(ch)){
	        	outStr += ch;
	        }

	    }

	   return outStr;

    }

    /**
     * Convert to string array.
     *
     * @param dataString the data string
     * @param paramGubun the param gubun
     * @param cnt the cnt
     * @return the string[]
     */
    public static String[] convertToStringArray(String dataString, String paramGubun, int cnt) {
		String[] dataArray = new String[cnt];
		dataArray = dataString.split(paramGubun);
		return dataArray;
	}

    /**
     * Null zero.
     *
     * @param str the str
     * @return the string
     */
    public static String nullZero(String str) {
        if (str == null || str.equals("null"))
                return "0";
        if (str.trim().equals(""))
                return "0";
        return str.trim();
    }

    /**
     * Substr.
     *
     * @param str the str
     * @param start the start
     * @param end the end
     * @return the string
     */
    public static String substr(String str, int start, int end) {
    	return StringUtils.substring(str, start, end);
    }

    /**
     * Substr.
     *
     * @param str the str
     * @param start the start
     * @return the string
     */
    public static String substr(String str, int start) {
    	return StringUtils.substring(str, start);
    }

    /**
     * Subbyte.
     *
     * @param str the str
     * @param start the start
     * @param end the end
     * @param charset the charset
     * @return the string
     */
    public static String subbyte(String str, int start, int end, String charset) {
    	String subStr = null;
    	try {
    		subStr = new String(ArrayUtil.subbyte(str, start, end, charset), charset);
		} catch (UnsupportedEncodingException e) {
			throw new BaseException(e);
		}
    	return subStr;
    }

    /**
     * Subbyte.
     *
     * @param str the str
     * @param start the start
     * @param end the end
     * @return the string
     */
    public static String subbyte(String str, int start, int end) {
    	return StringUtil.subbyte(str, start, end, CmnConst.CHARSET);
    }

	/**
	 * Sub byte.
	 * 한글 안 깨지게 byte로 자름
	 *
	 * @param str the str
	 * @param len the len
	 * @param encoding the encoding
	 * @return the string
	 */
	public static String subByte(String str, int len, String encoding) {
		try {
			if (StringUtil.isEmpty(str)) {
				return str;
			}
			byte[] strBytes = str.getBytes(encoding);
			int strLength = strBytes.length;
			int minusByteNum = 0;
			int offset = 0;
			int hangulByteNum = encoding.equals(CmnConst.CHARSET) ? 3 : 2;
			if (strLength > len) {
				minusByteNum = 0;
				offset = len;
				for (int j = 0; j < offset; j++) {
					if (((int) strBytes[j] & 0x80) != 0) {
						minusByteNum++;
					}
				}
				if (minusByteNum % hangulByteNum != 0) {
					offset -= minusByteNum % hangulByteNum;
				}
				return new String(strBytes, 0, offset, encoding);
			} else {
				return str;
			}
		} catch (Exception e) {
			throw new BaseException(e);
		}
	}

	/**
	 * Sub byte.
	 * 한글 안 깨지게 byte로 자름
	 *
	 * @param str the str
	 * @param len the len
	 * @return the string
	 */
	public static String subByte(String str, int len) {
		return StringUtil.subByte(str, len, CmnConst.CHARSET);
	}

    /**
     * Str to byte.
     *
     * @param str the str
     * @param size the size
     * @param value the value
     * @param pos the pos
     * @param charset the charset
     * @return the byte[]
     */
    public static byte[] strToByte(String str, int size, byte[] value, int pos, String charset) {
		byte[] src = str.getBytes(Charset.forName(charset));
	    byte[] dest = new byte[size];
//	    System.out.println(dest);
	    System.arraycopy(src, 0, dest, 0, src.length);
//	    System.out.println(dest);
	    System.arraycopy(dest, 0, value, pos, size);
	    return dest;
	}

	/**
	 * Str charset bytes.
	 *
	 * @param str the str
	 * @param size the size
	 * @param value the value
	 * @param pos the pos
	 * @return the byte[]
	 */
	public static byte[] strToByte(String str, int size, byte[] value, int pos) {
		return strToByte(str, size, value, pos, CmnConst.UTF8);
	}

	/**
	 * First char to upper case.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String firstCharToUpperCase(String str) {
		if(StringUtil.isNotEmpty(str)) {
			String firstStr = str.substring(0, 1);
			str = str.replaceFirst(firstStr, firstStr.toUpperCase());
		}
		return str;
	}

	/**
	 * To upper case.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String toUpperCase(String str) {
		if(StringUtil.isNotEmpty(str)) {
			return str.toUpperCase();
		}
		return str;
	}

	/**
	 * To lower case.
	 *
	 * @param str the str
	 * @return the string
	 */
	public static String toLowerCase(String str) {
		if(StringUtil.isNotEmpty(str)) {
			return str.toLowerCase();
		}
		return str;
	}

	/**
	 * 주어진 문자열을 대상으로하여 주어진 길이만큼의 문자열을 생성하여 리턴함.
	 * <p>
	 * <pre>
	 * (예)
	 *	String str = "abcd";
	 *	LOG.debug(StringUtil.fitString(str,6,StringUtil.RIGHT));
	 *	출력 = "  abcd"
	 * </pre>
	 * @param str 대상 문자열
	 * @param size 만들고자 하는 문자열의 길이
	 * @param align 정열기준의 방향(RIGHT, LEFT)
	 * @param fit 채워줄 문자열
	 * @return 주어진 길이만큼의 문자
	 */
	public static String ipgFitString(String str, int size, int align, String fit) {
		byte[] bts = str.getBytes();
		int len = bts.length;
		if (len == size) {
			return str;
		}
		if (len > size) {
			String s = new String(bts, 0, size);
			if (s.length() == 0) {
				StringBuffer sb = new StringBuffer(size);
				for (int idx = 0; idx < size; idx++) {
					sb.append("?");
				}
				s = sb.toString();
			}
			return s;
		}
		if (len < size) {
			int cnt = size - len;
			char[] values = new char[cnt];
			for (int idx = 0; idx < cnt; idx++) {
				if ("".equals(fit)) {
					values[idx] = ' ';
				} else {
					values[idx] = fit.toCharArray()[0];
				}

			}
			if (align == 1) {
				return String.copyValueOf(values) + str;
			} else {
				return str + String.copyValueOf(values);
			}
		}
		return str;
	}

	public static String convertToPstn(String str) {

		if(str.length() != 12) return "";

		String val1 = str.substring(0, 4);
		String val2 = str.substring(4, 8);
		String val3 = str.substring(8);

		if(val2.startsWith("0")) val2 = val2.substring(1);

		if(val1.equals("0002")) val1 = "02";
		else val1 = val1.substring(1);

		return val1.concat(val2).concat(val3);
	}

	public static String getExceptionStr(Object o) {
		if (null == o) {
			return "";
		}
		Throwable throwable = (Throwable)o;
		return StringUtil.subByte(Throwables.getStackTraceAsString(throwable), 4000);
	}
}