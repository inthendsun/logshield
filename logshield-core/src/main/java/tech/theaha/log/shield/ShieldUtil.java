package tech.theaha.log.shield;

import org.apache.commons.lang3.StringUtils;

public class ShieldUtil {
	
	/**
	 * @param str
	 * @param idx 掩码起始位置，基数=0。 -1代表最后一位，-2倒数第二位。
	 * @return
	 */
	public static String shield(String str, int idx) {
		if(str == null) {
			return str;
		}
		return shield(str, idx, str.length(), '*');
	}
	
	
	/**
	 * @param str
	 * @param idx 掩码起始位置，基数=0。 -1代表最后一位，-2倒数第二位。
	 * @param mark 掩码字符
	 * @return
	 */
	public static String shield(String str, int idx, char mark) {
		if(str == null) {
			return str;
		}
		return shield(str, idx, str.length(), mark);
	}
	
	/**
	 * @param str
	 * @param idx 掩码起始位置，基数=0。 -1代表最后一位，-2倒数第二位。
	 * @param length 从起始位置开始的长度
	 * @return
	 */
	public static String shield(String str, int idx, int length) {
		
		if(str == null) {
			return str;
		}
		return shield(str, idx, length, '*');
	}
	
	/**
	 * @param str
	 * @param idx 掩码起始位置，基数=0。 -1代表最后一位，-2倒数第二位。
	 * @param length 从起始位置开始的长度
	 * @param mark 掩码字符
	 * @return
	 */
	public static String shield(String str, int idx, int length, char mark) {
		
		if(str == null) {
			return str;
		}
		
		int realIdx = idx;
		if(idx < 0) {
			str = StringUtils.reverse(str);
			realIdx = Math.abs(idx);
		}
		
		char[] charArr = str.toCharArray();
		for (int i = realIdx; i < realIdx + length && i < charArr.length; i++) {
			charArr[i] = mark;
		}
		
		str = new String(charArr);
		if(idx < 0) {
			str = StringUtils.reverse(str);
		}
		
		return str;
	}
}
