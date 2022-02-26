package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;

/**
 * ファイルの管理を行います
 * @author blank-nano
 *
 */
public class FileUtils {

	/**
	 * @param filePath - 存在を確認するファイル名
	 * @return ファイルが存在するか
	 */
	public static boolean isExistFile(String filePath) {
		return new File(filePath).exists();
	}

	/**
	 * @param filePath - 不存在を確認するファイル名
	 * @return ファイルが存在しないか
	 */
	public static boolean isNoExistFile(String filePath) {
		return !isExistFile(filePath);
	}

	/**
	 * ファイルを作成する
	 * @param filePath - 作成したいファイル名
	 * @throws IOException - 作成できない場合発生
	 */
	public static void createFile(String filePath) throws IOException {
		new File(filePath).createNewFile();
	}

	/**
	 * ファイルを作成する
	 * @param directoryName - 作成したいファイル名
	 */
	public static void createDirectory(String directoryName) {
		new File(directoryName).mkdirs();
	}

	/**
	 * ファイルを削除する
	 * @param filePath - 削除したいファイル名
	 * @throws IOException - 削除できなかった場合発生
	 */
	public static void deleteFile(String filePath) {
		new File(filePath).deleteOnExit();
	}

//	private static String getWorkspacePath() {
//		return System.getProperty("user.dir") + "\\";
//	}

	/**
	 * 拡張子を取得します
	 * @param file - ファイル名
	 * @return - ファイルの拡張子
	 */
	public static String getExtension(String file) {
		if (file == null || file.indexOf(".") == -1) {
			return "";
		}
		return file.substring(file.lastIndexOf(".") + 1);
	}

	/**
	 * 文字列として拡張子を置換します
	 * @param file - ファイル名
	 * @param extension - 変更後拡張子
	 * @return - 拡張子変更を行ったファイル名
	 */
	public static String changeExtension(String file, String extension) {
		if (file == null) {
			return "";
		}
		if (file.indexOf(".") == -1) {
			return file;
		}
		String ext = extension;
		if (StringUtils.isBlank(ext)) {
			return file.replaceAll("\\.[^\\.]*$", "");
		}
		if (ext.indexOf(".") == -1) {
			ext = "." + ext;
		}
		return file.replaceAll("\\.[^\\.]*$", ext);
	}

	/**
	 * 通常ファイルの読み込みを行う
	 * @param filePath - ファイルパス
	 * @return - ファイル内容
	 * @throws IOException - ファイルを読み込みできない場合に発生
	 */
	public static List<String> readNormalFileToList(String filePath) throws IOException {
	    List<String> dataList = new ArrayList<>();
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8));){
		    String str;
		    while ((str = reader.readLine()) != null) {
		        dataList.add(str);
		    }
		} catch (IOException e) {
			throw e;
		}
	    return dataList;
	}

	/**
	 * 通常ファイルの読み込みを行う
	 * @param filePath - ファイルパス
	 * @return - ファイル内容
	 * @throws IOException - ファイルを読み込みできない場合に発生
	 */
	public static byte[] readNormalFileToByte(String filePath) throws IOException {
	    return Files.readAllBytes(new File(filePath).toPath());
	}

	/**
	 * プロパティファイルの読み込みを行う
	 * @param filePath - ファイルパス
	 * @return - ファイル内容
	 * @throws IOException - ファイルを読み込みできない場合に発生
	 */
	public static Map<String, String> readPropFile(String filePath) throws IOException {
		Properties prop = new Properties();
		Map<String, String> propMap = new HashMap<>();

		try (InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(filePath), StandardCharsets.UTF_8);){
			prop.load(inputStreamReader);
			for (Entry<Object, Object> entry : prop.entrySet()) {
				propMap.put(entry.getKey().toString(), entry.getValue().toString());
			}
		} catch (IOException e) {
			throw e;
		}
		return propMap;
	}

	/**
	 * csvファイルの読み込みを行う
	 * @param filePath - ファイルパス
	 * @return - ファイル内容
	 * @throws IOException - ファイルを読み込みできない場合に発生
	 */
	public static String[][] readCsvFile(String filePath) throws IOException {
		List<String> readList = readNormalFileToList(filePath);
		String[] readArray = readList.toArray(new String[readList.size()]);
		String[][] csvArray = new String[readArray.length][];
		for (int i = 0; i < readArray.length; i++) {
			csvArray[i] = readArray[i].split(",");
		}
		return csvArray;
	}

	/**
	 * csvファイルをint配列で読み込む
	 * @param filePath - ファイルパス
	 * @return - ファイル内容
	 * @throws IOException - ファイルを読み込みできない場合に発生
	 */
	public static int[][] readCsvFileToInt(String filePath) throws IOException {
		String[][] csvArray = readCsvFile(filePath);
		int[][] csvArrayToInt = new int[csvArray.length][csvArray[0].length];
		for (int i = 0; i < csvArray.length; i++) {
			for (int j = 0; j < csvArray[0].length; j++) {
				csvArrayToInt[i][j] = Integer.parseInt(csvArray[i][j]);
			}
		}
		return csvArrayToInt;
	}

	/**
	 * 通常ファイルの書き込みを行う
	 * @param filePath - ファイルパス
	 * @param data - 書き込むデータ
	 * @param appendFlg - 追記の有無true : 追記, false : 上書き
	 * @throws IOException - ファイルを書き込みできない場合に発生
	 */
	public static void writeNormalFile(String filePath, String data, boolean appendFlg) throws IOException {
		if (isNoExistFile(filePath)) {
			createFile(filePath);
		}
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, appendFlg), StandardCharsets.UTF_8));){
			writer.write(data);
			writer.newLine();
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 通常ファイルの書き込みを行う
	 * @param filePath - ファイルパス
	 * @param dataList - 書き込むデータ
	 * @param appendFlg - 追記の有無true : 追記, false : 上書き
	 * @throws IOException - ファイルを書き込みできない場合に発生
	 */
	public static void writeNormalFile(String filePath, List<String> dataList, boolean appendFlg) throws IOException {
		if (isNoExistFile(filePath)) {
			createFile(filePath);
		}
		try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, appendFlg), StandardCharsets.UTF_8));){
			for (String value : dataList) {
				writer.write(value);
				writer.newLine();
			}
		} catch (IOException e) {
			throw e;
		}
	}

	/**
	 * 通常ファイルの書き込みを行う
	 * @param filePath - ファイルパス
	 * @param data - 書き込むデータ
	 * @param appendFlg - 追記の有無true : 追記, false : 上書き
	 * @throws IOException - ファイルを書き込みできない場合に発生
	 */
	public static void writeNormalFile(String filePath, byte[] data, boolean appendFlg) throws IOException {
		if (appendFlg) {
			Files.write(new File(filePath).toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.WRITE, StandardOpenOption.APPEND);
		} else {
			Files.write(new File(filePath).toPath(), data, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.WRITE);
		}
	}

	/**
	 * プロパティファイルの書き込みを行う
	 * @param filePath - ファイルパス
	 * @param propMap - 書き込む設定項目
	 * @throws IOException - ファイルを書き込みできない場合に発生
	 */
	public static void writePropFile(String filePath, Map<String, String> propMap) throws IOException {
		Properties prop = new Properties();

		try (OutputStreamWriter outputStreamReader = new OutputStreamWriter(new FileOutputStream(filePath), StandardCharsets.UTF_8);){
			for (Entry<String, String> value : propMap.entrySet()) {
				prop.setProperty(value.getKey(), value.getValue());
			}
			prop.store(outputStreamReader, "Comment");
		} catch (IOException e) {
			throw e;
		}
	}

}
