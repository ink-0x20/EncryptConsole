package utils;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * 変換処理を行います
 * @author blank-nano
 *
 */
public class ConversionUtils {

	private static final Charset CHARSET = StandardCharsets.UTF_8;

	/**
	 * 文字列をbyte配列に変換します
	 * @param str - byte配列に変換する文字列
	 * @return - 文字列をbyte配列に変換した結果
	 */
	public static byte[] stringToByteArray(String str) {
		if (str == null) {
			return new byte[0];
		}
		return str.getBytes(CHARSET);
	}

	/**
	 * byte配列を文字列に変換します
	 * @param bytes - 文字列に変換するbyte配列
	 * @return - byte配列を文字列に変換した結果
	 */
	public static String byteArrayToString(byte[] bytes) {
		if (bytes == null) {
			return new String();
		}
		return new String(bytes, CHARSET);
	}

}
