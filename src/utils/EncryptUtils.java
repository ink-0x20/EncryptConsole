package utils;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.ArrayUtils;

import exception.EP0101EncryptAlgorithmException;
import exception.EP0102EncryptKeyNoneException;
import exception.EP0103EncryptKeyAttributeException;
import exception.EP0104EncryptException;
import exception.EP0105DecryptException;

/**
 * 暗号に関する処理を定義しています
 * 暗号化の種類は、共通鍵暗号方式の「AES」、公開鍵暗号方式の「RSA」があります
 * AESは128・192・256bitから選択可能なブロック暗号です。データの入替や排他的論理和の計算、行列変換などの処理を組み合わせてデータを暗号化します
 * RSAは大きな素数による素因数分解が困難であることを安全性の根拠としています
 * アルゴリズムのブロックは、「CBC」「ECB」があり、強度はEBC < CBCです
 * パディング方式は、数が足りない時にどう補うかを決めてます
 * @author blank-nano
 *
 */
public class EncryptUtils {

	/** 自動補完暗号化キーの長さ */
	private static final int AUTO_KEY_LENGTH = 128;
	/** byteずらし */
	private static final int SHIFT_BYTE = 73;
	/** 区切り文字1 */
	private static final byte SPLIT1 = ConversionUtils.stringToByteArray("_")[0];
	/** 区切り文字2 */
	private static final byte SPLIT2 = ConversionUtils.stringToByteArray("-")[0];

	/**
	 * セキュアなランダムbyte配列を作成します
	 * @param length - 作成するbyte数
	 * @return - ランダムbyte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 */
	public static byte[] secureRandomBytes(int length) throws EP0101EncryptAlgorithmException {
		if (length <= 0) {
			return new byte[0];
		}
		try {
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			byte[] randomBytes = new byte[length];
			secureRandom.nextBytes(randomBytes);
			return randomBytes;
		} catch (@SuppressWarnings("unused") NoSuchAlgorithmException e) {
			throw new EP0101EncryptAlgorithmException(true);
		}
	}

	/**
	 * セキュアなランダム文字列を作成します
	 * @param length - 作成する文字数
	 * @return - ランダム文字列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 */
	public static String secureRandomString(int length) throws EP0101EncryptAlgorithmException {
		return ConversionUtils.byteArrayToString(secureRandomBytes(length));
	}

	/**
	 * 拡張子を保存します
	 * @param target - 暗号文
	 * @param extension - 付与する拡張子
	 * @return - 拡張子を付与した暗号文
	 */
	public static byte[] saveExtension(byte[] target, String extension) {
		byte[] bytes = ArrayUtils.addAll(ConversionUtils.stringToByteArray(extension), SPLIT2);
		bytes = ArrayUtils.addAll(bytes, target);
		return Base64.getEncoder().encode(bytes);
	}

	/**
	 * 拡張子を削除します
	 * @param target - 暗号文
	 * @return - 拡張子を削除した暗号文
	 */
	public static byte[] deleteExtension(byte[] target) {
		byte[] bytes = Base64.getDecoder().decode(target);
		return Arrays.copyOfRange(bytes, ArrayUtils.indexOf(bytes, SPLIT2) + 1, bytes.length);
	}

	/**
	 * 拡張子を取得します
	 * @param target - 暗号文
	 * @return - 暗号文に付与された拡張子
	 */
	public static String loadExtension(byte[] target) {
		byte[] bytes = Base64.getDecoder().decode(target);
		bytes = Arrays.copyOfRange(bytes, 0, ArrayUtils.indexOf(bytes, SPLIT2));
		return ConversionUtils.byteArrayToString(bytes);
	}

	/**
	 * 暗号化キーなしでbyte配列をAES暗号化します
	 * @param target - 平文
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(byte[] target) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(EncryptAlgorithm.AES, target, null, null);
	}

	/**
	 * 暗号化キーありでbyte配列をAES暗号化します
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(byte[] target, byte[] encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(EncryptAlgorithm.AES, target, encryptKey, null);
	}

	/**
	 * 暗号化キーありでbyte配列をAES暗号化します
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(byte[] target, String encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(EncryptAlgorithm.AES, target, ConversionUtils.stringToByteArray(encryptKey), null);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列をAES暗号化します
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(byte[] target, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(EncryptAlgorithm.AES, target, encryptKey, ivKey);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列をAES暗号化します
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(byte[] target, String encryptKey, String ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(EncryptAlgorithm.AES, target, ConversionUtils.stringToByteArray(encryptKey), ConversionUtils.stringToByteArray(ivKey));
	}

	/**
	 * 暗号化キーなしでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(EncryptAlgorithm algorithm, byte[] target) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(algorithm, target, null, null);
	}

	/**
	 * 暗号化キーありでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(EncryptAlgorithm algorithm, byte[] target, byte[] encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(algorithm, target, encryptKey, null);
	}

	/**
	 * 暗号化キーありでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(EncryptAlgorithm algorithm, byte[] target, String encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(algorithm, target, ConversionUtils.stringToByteArray(encryptKey), null);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(EncryptAlgorithm algorithm, byte[] target, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(algorithm, target, encryptKey, ivKey);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	public static byte[] doEncryptToBytes(EncryptAlgorithm algorithm, byte[] target, String encryptKey, String ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		return manageEncrypt(algorithm, target, ConversionUtils.stringToByteArray(encryptKey), ConversionUtils.stringToByteArray(ivKey));
	}

	/**
	 * 暗号化キーなしでbyte配列をAES暗号化します
	 * @param target - 暗号文
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(byte[] target) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(EncryptAlgorithm.AES, target, null, null);
	}

	/**
	 * 暗号化キーありでbyte配列をAES暗号化します
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(byte[] target, byte[] encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(EncryptAlgorithm.AES, target, encryptKey, null);
	}

	/**
	 * 暗号化キーありでbyte配列をAES暗号化します
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(byte[] target, String encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(EncryptAlgorithm.AES, target, ConversionUtils.stringToByteArray(encryptKey), null);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列をAES暗号化します
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(byte[] target, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(EncryptAlgorithm.AES, target, encryptKey, ivKey);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列をAES暗号化します
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(byte[] target, String encryptKey, String ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(EncryptAlgorithm.AES, target, ConversionUtils.stringToByteArray(encryptKey), ConversionUtils.stringToByteArray(ivKey));
	}

	/**
	 * 暗号化キーなしでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 暗号文
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(EncryptAlgorithm algorithm, byte[] target) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(algorithm, target, null, null);
	}

	/**
	 * 暗号化キーありでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(EncryptAlgorithm algorithm, byte[] target, byte[] encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(algorithm, target, encryptKey, null);
	}

	/**
	 * 暗号化キーありでbyte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(EncryptAlgorithm algorithm, byte[] target, String encryptKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(algorithm, target, ConversionUtils.stringToByteArray(encryptKey), null);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(EncryptAlgorithm algorithm, byte[] target, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(algorithm, target, encryptKey, ivKey);
	}

	/**
	 * すべての暗号化キーを指定して、byte配列を指定の暗号方式で暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	public static byte[] doDecryptToBytes(EncryptAlgorithm algorithm, byte[] target, String encryptKey, String ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		return manageDecrypt(algorithm, target, ConversionUtils.stringToByteArray(encryptKey), ConversionUtils.stringToByteArray(ivKey));
	}



	/**
	 * 暗号化を管理します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @param saveKey - 暗号化キーを暗号文字列に含むか否か
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	private static byte[] manageEncrypt(EncryptAlgorithm algorithm, byte[] target, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		// 準備
		EncryptUtils.EncryptBean encryptBean = new EncryptUtils().new EncryptBean();
		encryptBean.setSaveEncryptKey(encryptKey == null);
		encryptBean.setSaveIvKey(ivKey == null);
		if (encryptBean.isSaveEncryptKey()) {
			encryptBean.setEncryptKey(secureRandomBytes(AUTO_KEY_LENGTH));
		} else {
			encryptBean.setEncryptKey(encryptKey);
		}
		if (encryptBean.isSaveIvKey()) {
			encryptBean.setIvKey(secureRandomBytes(AUTO_KEY_LENGTH));
		} else {
			encryptBean.setIvKey(ivKey);
		}
		// 暗号化
		byte[] encryptBytes = doEncrypt(algorithm, target, encryptBean);
		// 独自暗号化して返却
		return doOriginal(encryptBytes, encryptBean);
	}

	/**
	 * 暗号化します
	 * @param algorithm - 暗号アルゴリズム
	 * @param target - 平文
	 * @param encKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0104EncryptException - 暗号化に失敗したときに発生
	 */
	private static byte[] doEncrypt(EncryptAlgorithm algorithm, byte[] target, EncryptBean encryptBean) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0104EncryptException {
		if (target == null) {
			return new byte[0];
		}
		if (encryptBean.getEncryptKey() == null || encryptBean.getIvKey() == null) {
			throw new EP0102EncryptKeyNoneException(true);
		}
		SecretKeySpec secretKeySpec = new SecretKeySpec(DigestUtils.sha256(encryptBean.getEncryptKey()), algorithm.getEncrypt());
		IvParameterSpec ivParameterSpec = new IvParameterSpec(DigestUtils.md5(encryptBean.getIvKey()));
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm.getAlgorithm());
		} catch (@SuppressWarnings("unused") NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new EP0101EncryptAlgorithmException(true);
		}
		try {
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
		} catch (@SuppressWarnings("unused") InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new EP0103EncryptKeyAttributeException(true);
		}
		try {
			return cipher.doFinal(target);
		} catch (@SuppressWarnings("unused") IllegalBlockSizeException | BadPaddingException e) {
			throw new EP0104EncryptException(true);
		}
	}

	/**
	 * 独自にbyte配列を改ざんします
	 * 初期化ベクトルキー、暗号文、暗号化キー、の順で連結し返却
	 * Base64を使用し、初期化ベクトルキー、暗号文、暗号化キーはエンコードをかける（Base64は英数字と「/」「+」）
	 * 各3つの間をBase64では使用しない「-」「_」で区切る
	 * 最終的にすべてのキーを保存する場合は、初期化ベクトルキー_暗号文-暗号化キーとなる
	 * byteを+3ずらす
	 * @param encryptBytes - 暗号化
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @param saveKey - 暗号化キーを暗号文字列に含むか否か
	 * @return - 独自改竄byte配列
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 */
	private static byte[] doOriginal(byte[] encryptBytes, EncryptBean encryptBean) throws EP0102EncryptKeyNoneException {
		if (encryptBytes == null) {
			return new byte[0];
		}
		byte[] original;
		if (encryptBean.isSaveEncryptKey()) {
			if (encryptBean.isSaveIvKey()) {
				// 初期化ベクトルキー + 区切り文字1
				original = ArrayUtils.addAll(Base64.getEncoder().encode(ArraysUtils.reverse(encryptBean.getIvKey())), SPLIT1);
				// 初期化ベクトルキー + 区切り文字1 + 暗号文
				original = ArrayUtils.addAll(original, Base64.getEncoder().encode(ArraysUtils.reverse(encryptBytes)));
				// 初期化ベクトルキー + 区切り文字1 + 暗号文 + 区切り文字2
				original = ArrayUtils.addAll(original, SPLIT2);
				// 初期化ベクトルキー + 区切り文字1 + 暗号文 + 区切り文字2 + 暗号化キー
				original = ArrayUtils.addAll(original, Base64.getEncoder().encode(ArraysUtils.reverse(encryptBean.getEncryptKey())));
			} else {
				throw new EP0102EncryptKeyNoneException("暗号化キーが存在しません", true);
			}
		} else {
			if (encryptBean.isSaveIvKey()) {
				// 初期化ベクトルキー + 区切り文字1
				original = ArrayUtils.addAll(Base64.getEncoder().encode(ArraysUtils.reverse(encryptBean.getIvKey())), SPLIT1);
				// 初期化ベクトルキー + 区切り文字1 + 暗号文
				original = ArrayUtils.addAll(original, Base64.getEncoder().encode(ArraysUtils.reverse(encryptBytes)));
			} else {
				// 暗号文のみ
				original = Base64.getEncoder().encode(ArraysUtils.reverse(encryptBytes));
			}
		}
		// byteをずらす(ずらし分 + インデックス / 3)
		for (int i = 0; i < original.length; i++) {
			original[i] = (byte) (original[i] + SHIFT_BYTE + i / 3);
		}
		// Base64エンコードして返却
		return Base64.getEncoder().encode(original);
	}

	/**
	 * 復号を管理します
	 * @param algorithm - 暗号アルゴリズム
	 * @param encrypt - 暗号文
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @param saveKey - 暗号化キーを暗号文字列に含むか否か
	 * @return - 平文byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	private static byte[] manageDecrypt(EncryptAlgorithm algorithm, byte[] encrypt, byte[] encryptKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		// 独自復号
		DecryptBean decryptBean = undoOriginal(encrypt, encryptKey, ivKey);
		// 復号して返却
		return doDecrypt(algorithm, decryptBean.getDecrypt(), decryptBean.getEncryptKey(), decryptBean.getIvKey());
	}

	/**
	 * 独自に改竄したbyte配列を戻します
	 * 初期化ベクトルキー、暗号文字列、暗号化キー、の順で連結し返却
	 * @param encryptBytes - 暗号化
	 * @param encryptKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @param saveKey - 暗号化キーを暗号文字列に含むか否か
	 * @return - 独自改竄byte配列
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 */
	private static DecryptBean undoOriginal(byte[] encryptBytes, byte[] encryptKey, byte[] ivKey) throws EP0102EncryptKeyNoneException {
		EncryptUtils.DecryptBean decryptBean = new EncryptUtils().new DecryptBean();
		// Base64デコード
		byte[] original = Base64.getDecoder().decode(encryptBytes);
		// byteを戻す(ずらし分 - インデックス / 3)
		for (int i = 0; i < original.length; i++) {
			original[i] = (byte) (original[i] - SHIFT_BYTE - i / 3);
		}
		if (encryptKey == null) {
			if (ivKey == null) {
				// 暗号化キー
				decryptBean.setEncryptKey(ArraysUtils.reverse(Base64.getDecoder().decode(Arrays.copyOfRange(original, ArrayUtils.indexOf(original, SPLIT2) + 1, original.length))));
				// 暗号文
				decryptBean.setDecrypt(ArraysUtils.reverse(Base64.getDecoder().decode(Arrays.copyOfRange(original, ArrayUtils.indexOf(original, SPLIT1) + 1, ArrayUtils.indexOf(original, SPLIT2)))));
				// IV
				decryptBean.setIvKey(ArraysUtils.reverse(Base64.getDecoder().decode(Arrays.copyOfRange(original, 0, ArrayUtils.indexOf(original, SPLIT1)))));
			} else {
				throw new EP0102EncryptKeyNoneException("暗号化キーが存在しません", true);
			}
		} else {
			if (ivKey == null) {
				// 暗号化キー
				decryptBean.setEncryptKey(encryptKey);
				// 暗号文
				decryptBean.setDecrypt(ArraysUtils.reverse(Base64.getDecoder().decode(Arrays.copyOfRange(original, ArrayUtils.indexOf(original, SPLIT1) + 1, original.length))));
				// IV
				decryptBean.setIvKey(ArraysUtils.reverse(Base64.getDecoder().decode(Arrays.copyOfRange(original, 0, ArrayUtils.indexOf(original, SPLIT1)))));
			} else {
				decryptBean.setDecrypt(ArraysUtils.reverse(Base64.getDecoder().decode(original)));
				decryptBean.setEncryptKey(encryptKey);
				decryptBean.setIvKey(ivKey);
			}
		}
		return decryptBean;
	}

	/**
	 * 復号します
	 * @param algorithm - 暗号アルゴリズム
	 * @param encrypt - 暗号文
	 * @param encKey - 暗号化キー
	 * @param ivKey - 初期化ベクトルキー
	 * @return - 暗号化byte配列
	 * @throws EP0101EncryptAlgorithmException - 使用不可や不正なアルゴリズムの場合に発生
	 * @throws EP0102EncryptKeyNoneException - 暗号化キーがない場合に発生
	 * @throws EP0103EncryptKeyAttributeException - 暗号化キーが不正の場合発生
	 * @throws EP0105DecryptException - 復号に失敗したときに発生
	 */
	private static byte[] doDecrypt(EncryptAlgorithm algorithm, byte[] encrypt, byte[] encKey, byte[] ivKey) throws EP0101EncryptAlgorithmException, EP0102EncryptKeyNoneException, EP0103EncryptKeyAttributeException, EP0105DecryptException {
		if (encrypt == null) {
			return new byte[0];
		}
		if (encKey == null || ivKey == null) {
			throw new EP0102EncryptKeyNoneException(true);
		}
		SecretKeySpec secretKeySpec = new SecretKeySpec(DigestUtils.sha256(encKey), algorithm.getEncrypt());
		IvParameterSpec ivParameterSpec = new IvParameterSpec(DigestUtils.md5(ivKey));
		Cipher cipher;
		try {
			cipher = Cipher.getInstance(algorithm.getAlgorithm());
		} catch (@SuppressWarnings("unused") NoSuchAlgorithmException | NoSuchPaddingException e) {
			throw new EP0101EncryptAlgorithmException(true);
		}
		try {
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);
		} catch (@SuppressWarnings("unused") InvalidKeyException | InvalidAlgorithmParameterException e) {
			throw new EP0103EncryptKeyAttributeException(true);
		}
		try {
			return cipher.doFinal(encrypt);
		} catch (@SuppressWarnings("unused") IllegalBlockSizeException | BadPaddingException e) {
			throw new EP0105DecryptException(true);
		}
	}

	/**
	 * 暗号アルゴリズム
	 * @author blank-nano
	 *
	 */
	public enum EncryptAlgorithm {
		/** AES/CBC/PKCS5Padding */
		  AES("AES", "AES/CBC/PKCS5Padding")
		/** RSA/ECB/PKCS1Padding */
		, RSA("RSA", "RSA/ECB/PKCS1Padding")
		;

		private String encrypt;
		private String algorithm;

		/**
		 * デフォルトコンストラクタ
		 * @param encrypt - 暗号の種類
		 * @param algorithm - 暗号アルゴリズム
		 */
		private EncryptAlgorithm(String encrypt, String algorithm) {
			this.encrypt = encrypt;
			this.algorithm = algorithm;
		}

		/**
		 * 暗号の種類を取得します
		 * @return - 暗号の種類
		 */
		public String getEncrypt() {
			return this.encrypt;
		}

		/**
		 * 暗号アルゴリズムを取得します
		 * @return - 暗号アルゴリズム
		 */
		public String getAlgorithm() {
			return this.algorithm;
		}

	}

	private class EncryptBean {
		private byte[] encryptKey;
		private byte[] ivKey;
		private boolean saveEncryptKey;
		private boolean saveIvKey;
		public byte[] getEncryptKey() {
			return this.encryptKey;
		}
		public void setEncryptKey(byte[] encryptKey) {
			this.encryptKey = encryptKey;
		}
		public byte[] getIvKey() {
			return this.ivKey;
		}
		public void setIvKey(byte[] ivKey) {
			this.ivKey = ivKey;
		}
		public boolean isSaveEncryptKey() {
			return this.saveEncryptKey;
		}
		public void setSaveEncryptKey(boolean saveEncryptKey) {
			this.saveEncryptKey = saveEncryptKey;
		}
		public boolean isSaveIvKey() {
			return this.saveIvKey;
		}
		public void setSaveIvKey(boolean saveIvKey) {
			this.saveIvKey = saveIvKey;
		}
	}
	private class DecryptBean {
		private byte[] decrypt;
		private byte[] encryptKey;
		private byte[] ivKey;
		public byte[] getDecrypt() {
			return this.decrypt;
		}
		public void setDecrypt(byte[] decrypt) {
			this.decrypt = decrypt;
		}
		public byte[] getEncryptKey() {
			return this.encryptKey;
		}
		public void setEncryptKey(byte[] encryptKey) {
			this.encryptKey = encryptKey;
		}
		public byte[] getIvKey() {
			return this.ivKey;
		}
		public void setIvKey(byte[] ivKey) {
			this.ivKey = ivKey;
		}
	}
}
