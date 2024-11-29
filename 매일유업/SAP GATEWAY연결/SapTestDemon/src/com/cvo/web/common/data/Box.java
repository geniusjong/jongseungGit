package com.cvo.web.common.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.jxpath.JXPathContext;
import org.springframework.ui.ModelMap;
import org.springframework.web.multipart.MultipartFile;

import com.cvo.web.common.constants.CmnConst;
import com.cvo.web.common.util.BoxUtil;
import com.cvo.web.common.util.NumberUtil;
import com.cvo.web.common.util.StringUtil;

public class Box extends BaseBox {

	private static final long serialVersionUID = -1275868958044369838L;

	/**
	 * 키값을 CAMEL 형식으로 변환해서 저장합니다.
	 */
	public static final int CAMEL_KEY = 0;
	/**
	 * 키값을 모두 대문자로 변환해서 저장합니다.
	 */
	public static final int UPPER_KEY = 1;
	/**
	 * 키값을 모두 소문자로 변환해서 저장합니다.
	 */
	public static final int LOWER_KEY = 2;
	/**
	 * 키값을 원래 대소문자로 저장합니다. 기본값입니다.
	 */
	public static final int NATIVE_KEY = 3;

//	private HttpSession session;

	private JXPathContext jxPathContext;

	private BoxProp boxProp;

	/**
	 * Instantiates a new box.
	 */
	public Box() {
		super();
	}

	/**
	 * Instantiates a new box.
	 *
	 * @param map the map
	 */
	public Box(Map<String, Object> map) {
		super(map);
	}

	/**
	 * request의 파라미터를 box에 담습니다.
	 *
	 * @param request
	 * @throws Exception
	 */
	public Box(HttpServletRequest request, int keyCase) {
		super();

		Enumeration<?> enumeration = request.getParameterNames();

		while (enumeration.hasMoreElements()) {
			String key = (String) enumeration.nextElement();
			String[] values = request.getParameterValues(key);
			if (values != null) {
				if (keyCase == UPPER_KEY) {
					key = key.toUpperCase();
				} else if (keyCase == LOWER_KEY) {
					key = key.toLowerCase();
				} else if (keyCase == CAMEL_KEY) {
					key = StringUtil.toCamel(key);
				}
				this.put(key, (values.length > 1) ? values : values[0]);
			}
		}

		/*** request객체의 필요값을 box에 담는다 **/
//		this.put("reqUrl", HttpUtil.getRequestURL(request));
//		this.put("reqIp", HttpUtil.getRemoteIpAddr(request));
//		this.put("reqMethod", request.getMethod());
//		this.put("refererUri", HttpUtil.getRefererUri(request));
	}

	public Box(HttpServletRequest request) {
		this(request, NATIVE_KEY);
	}

	/**
	 * Instantiates a new box.
	 *
	 * @param initialCapacity the initial capacity
	 * @param loadFactor the load factor
	 */
	public Box(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor);
	}

	/**
	 * Instantiates a new box.
	 *
	 * @param initialCapacity the initial capacity
	 */
	public Box(int initialCapacity) {
		super(initialCapacity);
	}

	/**
	 * Gets the files.
	 *
	 * @param key the key
	 * @return the files
	 */
	public MultipartFile[] getFiles(String key) {
		Box fileBox = (Box)this.getProp(CmnConst.PARAM_PROP_FILE_BOX);
		MultipartFile[] files = null;
		if(fileBox != null) {
			Object fileObj = fileBox.get(key);
			if(fileObj instanceof MultipartFile) {
				files = new MultipartFile[]{(MultipartFile)fileObj};
			} else if(fileObj instanceof MultipartFile[]) {
				files = (MultipartFile[])fileObj;
			}
		}
		return files;
	}

	/**
	 * Gets the file.
	 *
	 * @param key the key
	 * @return the file
	 */
	public MultipartFile getFile(String key) {
		Box fileBox = (Box)this.getProp(CmnConst.PARAM_PROP_FILE_BOX);
		MultipartFile file = null;
		if(fileBox != null) {
			Object fileObj = fileBox.get(key);
			if(fileObj instanceof MultipartFile) {
				file = (MultipartFile)fileObj;
			} else if(fileObj instanceof MultipartFile[]) {
				MultipartFile[] files = (MultipartFile[])fileObj;
				if(files != null) {
					file = files[0];
				}
			}
		}
		return file;
	}

	/**
	 * Gets the path.
	 *
	 * @param path the path
	 * @return the path
	 */
	public Object getPath(String path) {
		if(jxPathContext == null) {
			jxPathContext = JXPathContext.newContext(this);
		}
		Object rtnValue = null;
		try {
			rtnValue = jxPathContext.getValue(path);
		} catch (Exception e) {
			rtnValue = null;
		}
		return rtnValue;
	}

	/**
	 * Gets the path string.
	 *
	 * @param path the path
	 * @return the path string
	 */
	public String getPathString(String path, String defaultValue) {
		if(jxPathContext == null) {
			jxPathContext = JXPathContext.newContext(this);
		}
		String rtnValue = null;
		try {
			rtnValue = StringUtil.nvl(jxPathContext.getValue(path), defaultValue);
		} catch (Exception e) {
			rtnValue = defaultValue;
		}
		return rtnValue;
	}

	public String getPathString(String path) {
		return this.getPathString(path, "");
	}
	public String path(String path, String defaultValue) {
		return this.getPathString(path, defaultValue);
	}

	public String path(String path) {
		return this.getPathString(path, "");
	}

	public Object pathObj(String path) {
		return this.getPath(path);
	}

	/**
	 * Gets the path int.
	 *
	 * @param path the path
	 * @return the path int
	 */
	public int getPathInt(String path) {
		if(jxPathContext == null) {
			jxPathContext = JXPathContext.newContext(this);
		}
		return NumberUtil.toInt(jxPathContext.getValue(path));
	}

	/**
	 * Put path.
	 *
	 * @param path the path
	 * @param value the value
	 */
	public void putPath(String path, Object value) {
		if(jxPathContext == null) {
			jxPathContext = JXPathContext.newContext(this);
		}
		jxPathContext.setValue(path, value);
	}

	/**
	 * Gets the prop.
	 *
	 * @return the prop
	 */
	public BoxProp getProp() {
		return boxProp;
	}

	/**
	 * Put all prop.
	 *
	 * @param properties the properties
	 */
	public void putAllProp(Properties properties) {
		if(boxProp == null) {
			boxProp = new BoxProp();
		}
		boxProp.putAll(properties);
	}

	/**
	 * Put prop.
	 *
	 * @param key the key
	 * @param value the value
	 */
	public void putProp(String key, Object value) {
		if(boxProp == null) {
			boxProp = new BoxProp();
		}
		boxProp.put(key, value);
	}

	/**
	 * Gets the prop.
	 *
	 * @param key the key
	 * @return the prop
	 */
	public Object getProp(String key) {
		if(boxProp == null) {
			return null;
		}
		return boxProp.get(key);
	}

	/**
	 * Gets the prop string.
	 *
	 * @param key the key
	 * @return the prop string
	 */
	public String getPropString(String key) {
		if(boxProp == null) {
			return null;
		}
		return boxProp.getString(key);
	}

	/**
	 * Gets the prop int.
	 *
	 * @param key the key
	 * @return the prop int
	 */
	public int getPropInt(String key) {
		if(boxProp == null) {
			return 0;
		}
		return boxProp.getInt(key);
	}

	/* (non-Javadoc)
	 * @see com.olleh.lupin.cmn.data.BaseBox#clone()
	 */
	public Box clone() {
		return (Box)super.clone();
	}

	public void setPaginate(PaginateModel paginateModel) {
		put(CmnConst.PARAM_PAGINATE, BoxUtil.toBox(paginateModel));
	}

	public PaginateModel getPaginate() {
		return (PaginateModel)get(CmnConst.PARAM_PAGINATE);
	}

	public void pgInit(int navTotCnt, ModelMap model) {
		PaginateModel paginateModel = getPaginate();
		if(paginateModel == null) {
			paginateModel = new PaginateModel();
			setPaginate(paginateModel);
		}
		paginateModel.init(this);
//		if (this.nvl("directCall").equals("Y")) { //대용량 엑셀 추출을 위한 직접 호출일 경우
//			if (this.getInt("maxRow") < navTotCnt) {
//				navTotCnt = this.getInt("maxRow");
//			}
//			paginateModel.setDirectTotalCount(navTotCnt, this.getInt("pageSize"));
//			if (this.getInt("maxRow") < paginateModel.getEndNum()) {
//				paginateModel.setEndNum(this.getInt("maxRow"));
//			}
//		} else {
			paginateModel.setTotalCount(navTotCnt);
//		}
		setPaginate(paginateModel);
		model.put("paging", this.getBox(CmnConst.PARAM_PAGINATE));
	}

	public boolean eq(String key, String ifStr) {
		if (null == ifStr) {
			if (null == this.getString(key)) {
				return true;
			} else {
				return false;
			}
		}

		if (ifStr.equals(this.nvl(key))) {
			return true;
		} else {
			return false;
		}
	}
	public boolean eq(String key, String... ifStrArry) {

		if (ifStrArry.length == 0) {
			if(this.eq(key, "")) {
				return true;
			}
		}
		for (String ifStr : ifStrArry) {
			if(this.eq(key, ifStr)) {
				return true;
			}
		}
		return false;
	}

	public boolean ne(String key, String ifStr) {
		if (this.eq(key, ifStr)) {
			return false;
		} else {
			return true;
		}
	}
	public boolean ne(String key, String... ifStrArry) {

		for (String ifStr : ifStrArry) {
			if(this.eq(key, ifStr)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * 오라클의 decode함수와 동일한 기능을 한다.
	 * box.put("iKindCd", "Y");
	 * box.decode("iKindCd", "1", "뻥", "Y", "번호존재", "N", "추가요망", "기타")); //번호존재
	 *
	 * @param key
	 * @param ifStrArry
	 * @return
	 */
	public String decode(String key, String... ifStrArry) {

		int ifStrCnt = ifStrArry.length;
		if (ifStrCnt < 3 || (ifStrCnt % 2) == 0) {
			return "";
		}

		String val = this.nvl(key);
		for (int idx = 0; idx < (ifStrCnt - 1) / 2; idx++) {
			int ifIdx = idx * 2;
			if (ifStrArry[ifIdx].equals(val)) {
				return ifStrArry[ifIdx+1];
			}
		}
		return ifStrArry[ifStrCnt-1];
	}


	/**
	 * 지정된 list의 내의 box의 key값의 앞뒤에 prefix, suffix 문자를 붙여서 반환한다.
	 * rowBox.put("carNo", "11허1111");
	 * list.add(rowBox);
	 * rowBox.put("carNo", "11허1112");
	 * list.add(rowBox);
	 * rowBox.put("carNo", "11허1113");
	 * list.add(rowBox);
	 * box.put("list", list);
	 *
	 * box.concat("list", "carNo", "*", ", ")); //*11허1111, *11허1112, *11허1113
	 * box.concat("list", "carNo", "'", "',", false)); //'11허1111','11허1112','11허1113',
	 *
	 * @param listKey
	 * @param key
	 * @param prefix
	 * @param suffix
	 * @param lastFlag
	 * @return
	 */
	public String concat(String listKey, String key, String prefix, String suffix, boolean lastFlag) {

		StringBuffer sb = new StringBuffer();
		int idx = 0;
		for (Box rowBox : this.getList(listKey)) {
			if (null != prefix && !"".equals(prefix)) {
				sb.append(prefix);
			}
			sb.append(rowBox.nvl(key));
			if (null != suffix && !"".equals(suffix)) {
				if (lastFlag && this.getList(listKey).size() == (idx+1)) {
					;
				} else {
					sb.append(suffix);
				}
			}
			idx++;
		}
		return null != sb && sb.length() > 0 ? sb.toString() : "";
	}

	public String concat(String listKey, String key, String prefix, String suffix) {
		return concat(listKey, key, prefix, suffix, true);
	}

	/**
	 * 해당 key의 값만큼 str 문자를 반환한다 (default str은 공백(" "))이다
	 * box.put("margin", 10);
	 * box.space("margin", " ");
	 * 반환값 : "		  "
	 *
	 * @param key
	 * @param str
	 * @return
	 */
	public String space(String key, String str) {
		if ("".equals(this.nvl(key))) {
			return "";
		}

		String rtnSpace = "";
		for (int idx = 0; idx < this.getInt(key); idx ++) {
			rtnSpace += str;
		}
		return rtnSpace;
	}

	public String space(String key) {
		return space(key, " ");
	}

	/**
	 * box안의 list 필터 (다중 조건) : AND (조건식을 모두 만족해야 처리)
	 * box.filter("list", "aplStrDate", "20130201", "aplEndDate", "20140302");
	 *
	 * @param listKey
	 * @param ifStrArry
	 * @return
	 */
	public List<Box> filter(String listKey, String... ifStrArry) {
		List<Box> filterList = new ArrayList<Box>();
		if (null == listKey || listKey.isEmpty()) {
			return filterList;
		}
		if (null == ifStrArry) {
			return filterList;
		}

		int ifStrCnt = ifStrArry.length;
		if (ifStrCnt < 2 || (ifStrCnt % 2) == 1) {
			return filterList;
		}

		for (Box rowBox : this.getList(listKey)) {
			boolean isFlag = true;
			Box filterBox = new Box();

			for (int idx = 0; idx < ifStrCnt / 2; idx++) {
				int ifIdx = idx * 2;
				String key = ifStrArry[ifIdx];
				String ifVal = ifStrArry[ifIdx + 1];
				if (rowBox.ne(key, ifVal)) {
					isFlag = false;
				}
			}

			if (true == isFlag) {
				filterBox.putAll(rowBox);
				filterList.add(filterBox);
			}
		}

		return filterList;
	}

	/**
	 * box안의 list 필터 (다중 조건) : OR (조건식중 하나만 만족하면 처리)
	 * box.filterOr("list", "aplStrDate", "20130201", "aplEndDate", "20140302");
	 *
	 * @param listKey
	 * @param ifStrArry
	 * @return
	 */
	public List<Box> filterOr(String listKey, String... ifStrArry) {
		List<Box> filterList = new ArrayList<Box>();
		if (null == listKey || listKey.isEmpty()) {
			return filterList;
		}
		if (null == ifStrArry) {
			return filterList;
		}

		int ifStrCnt = ifStrArry.length;
		if (ifStrCnt < 2 || (ifStrCnt % 2) == 1) {
			return filterList;
		}

		for (Box rowBox : this.getList(listKey)) {
			boolean isFlag = false;
			Box filterBox = new Box();

			for (int idx = 0; idx < ifStrCnt / 2; idx++) {
				int ifIdx = idx * 2;
				String key = ifStrArry[ifIdx];
				String ifVal = ifStrArry[ifIdx + 1];
				if (rowBox.eq(key, ifVal)) {
					isFlag = true;
				}
			}

			if (true == isFlag) {
				filterBox.putAll(rowBox);
				filterList.add(filterBox);
			}
		}

		return filterList;
	}

	/**
	 * box2를 box1에 넣을때 비교조건(ifStr)에 만족하는 값만 넣는다
	 * box1.putEq(box2, "1"); //box2안의 value가 "1"인 모든 데이터를 box1에 삽입
	 *
	 * @param box
	 * @param ifStr
	 * @return
	 */
	public Box putEq(Box inBox, String ifStr) {
		if (null == inBox || inBox.isEmpty()) {
			return this;
		}
		if (null == ifStr) {
			ifStr = "";
		}

		for (String key : inBox.keySet()) {
			if (ifStr.equals(inBox.nvl(key))) {
				this.put(key, inBox.nvl(key));
			}
		}
		return this;
	}

	public Box putEq(Box inBox) {
		return this.putEq(inBox, "");
	}

	/**
	 * box2를 box1에 넣을때 비교조건(ifStr)에 만족하지 않는 값만 넣는다
	 * box1.putNe(box2, ""); //box2안의 value가 ""인 모든 데이터를 box1에 삽입하지 않음
	 *
	 * @param box
	 * @param ifStr
	 * @return
	 */
	public Box putNe(Box inBox, String ifStr) {
		if (null == inBox || inBox.isEmpty()) {
			return this;
		}
		if (null == ifStr) {
			ifStr = "";
		}

		for (String key : inBox.keySet()) {
			if (!ifStr.equals(inBox.nvl(key))) {
				this.put(key, inBox.nvl(key));
			}
		}
		return this;
	}

	public Box putNe(Box inBox) {
		return this.putNe(inBox, "");
	}

	/**
	 * box내의 지정 key의 값이 빈값이 아닐경우 양쪽(both), 혹은 앞(before) 뒤(after)로 %문자를 삽입한다
	 * ex) box.likeBefore("key"), box.likeAfter("key"), box.likeBoth("key"), box.likeBoth("key1", "key2", "key3")
	 */
	public void likeBefore(String... keyArry) {
		if (null == keyArry || "".equals(keyArry)) {
			return;
		}

		for (String key : keyArry) {
			if (!"".equals(this.nvl(key))) {
				this.put(key, "%" + this.nvl(key));
			}
		}
	}
	public void likeAfter(String... keyArry) {
		if (null == keyArry || "".equals(keyArry)) {
			return;
		}

		for (String key : keyArry) {
			if (!"".equals(this.nvl(key))) {
				this.put(key, this.nvl(key) + "%");
			}
		}
	}
	public void likeBoth(String... keyArry) {
		if (null == keyArry || "".equals(keyArry)) {
			return;
		}

		for (String key : keyArry) {
			if (!"".equals(this.nvl(key))) {
				this.put(key, "%" + this.nvl(key) + "%");
			}
		}
	}

	/**
	 * String을 list로 변환하여 반환한다.
	 *
	 * @param key the key
	 * @return the box
	 */
	public List<Box> strToList(String key, String div, String defaultCd) {
		List<Box> list = new ArrayList<Box>();

		//2018.11.09 lsj : 값이 없을경우 전체로 간주하여 세션의 권한센터를 반환한다)
		if (null == defaultCd) {
			if ("".equals(this.nvl(key))) {
				this.put(key, this.path("sBox/authCenterCd"));
			}
		} else if (defaultCd.equals("Y")) {
			return list;
		} else {
			if ("".equals(this.nvl(key))) {
				this.put(key, defaultCd);
			}
		}

		if (null == div || "".equals(div) || div.isEmpty()) {
			div = ",";
		}

		String[] arry = this.nvl(key).split(div);
		for (int idx = 0; idx < arry.length; idx++)  {
			Box rowBox = new Box();
			rowBox.put("key", arry[idx]);
			list.add(rowBox);
		}
		return list;
	}

	public List<Box> strToList(String key) {
		return this.strToList(key, null, null);
	}
}
