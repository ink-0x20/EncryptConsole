package utils;

/**
 * 配列関係の処理を行います
 * @author blank-nano
 *
 */
public class ArraysUtils {

	/**
	 * 配列を逆順に並び替えます
	 * @param array - 配列
	 * @return - 逆順配列
	 */
	public static byte[] reverse(byte[] array) {
		int length = array.length;
		byte[] bytes = new byte[length];
		for (int i = 0, j = length - 1; i < length; i++, j--) {
			bytes[i] = array[j];
		}
		return bytes;
	}

}
