package com.Alan.eva.tools;

public class PathUtils {
	public static final String getRegisterPath() {
		return "http://101.201.30.238/api/index.php/Home/User/newReg";
	}

	public static final String getLoginPath() {
		return "http://101.201.30.238/api/index.php/Home/User/login";
	}

	public static final String getForgetPath() {
		return "http://101.201.30.238/api/index.php/Home/User/newForget";
	}

	public static final String getSuggestPath() {
		return "http://101.201.30.238/api/index.php/Home/notice/feedBack";
	}
	/**
	 * 得到验证码
	 */
	public static final String getVerificationCode(){
		return "http://101.201.30.238/api/index.php/Home/User/validate";
	}

	/**
	 * 上传体温数组的接口
	 * 
	 * @return
	 */
	public static String upLoadTemps() {
		return "http://101.201.30.238/api/index.php/Home/temp/submitDay";
	}
	
	/**
	 * 上传发烧次数统计
	 */
	public static String upLoadTime() {
		return "http://101.201.30.238/api/index.php/Home/fever/submitFevers";
	}
	/**
	 * 获取发烧次数统计
	 */
	public static String getTime(){
		return "http://101.201.30.238/api/index.php/Home/fever/getFevers";
	}

	/**
	 * 统计每天体温数据接口
	 * 
	 * @return
	 */
	public static String getStatpath(String uid, String childPhone, String date) {
		return "http://101.201.30.238/api/index.php/Home/Temp/getCountByDay?uid=" + uid + "&childPhone=" + childPhone
				+ "&date=" + date;
	}
/**
 * 上传体温值
 * @param childPhone
 * @param temp
 * @return
 */
	public static String getPostTemp(String childPhone, String temp) {
		return "http://101.201.30.238/api/index.php/Home/cache/put?cp=" + childPhone + "&temp=" + temp;
	}
/**
 * 从网上获取体温值
 * @param uid
 * @param childPhone
 * @return
 */
	public static String getGetTem(String uid, String childPhone) {
		return "http://101.201.30.238/api/index.php/Home/cache/get?uid=" + uid + "&cp=" + childPhone;
	}
	/**
	 * 获取体温数据的接口
	 */
	public static String getTemps(String phone, String y, String mt, String d) {
		return "http://101.201.30.238/api/index.php/Home/Temp/getTempByDay?phone="+phone+"&y="+y+"&mt="+mt+"&d="+d;
	}

	/**
	 * 折线按年发烧次数的接口
	 * 
	 * @param uid
	 * @param childPhone
	 * @param year
	 * @return
	 */
	public static String getLineChartData(String uid, String childPhone, String year) {
		return "http://101.201.30.238/api/index.php/Home/Temp/getCordByMonth?uid=" + uid + "&childPhone=" + childPhone
				+ "&year=" + year;
	}

	public static String update(int versionCode) {
		return "http://39.105.40.32:9000/api/download" + versionCode;
	}

}
