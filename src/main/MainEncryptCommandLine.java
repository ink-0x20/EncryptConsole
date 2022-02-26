package main;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import common.ExitCode;
import utils.EncryptUtils;
import utils.FileUtils;

/**
 * @author blank-nano
 *
 */
public class MainEncryptCommandLine {

	/**
	 * メインメソッドです
	 * @param args - コマンドライン引数
	 */
	public static void main(String[] args) {
		encryptManage(args);
	}

	/**
	 * 暗号化を管理します
	 * @param args - コマンドライン引数
	 */
	private static void encryptManage(String[] args) {
		// ********************************************************************************
		// オプション定義
		// ********************************************************************************
		Options options = new Options();
		options.addOption(
				Option.builder("h")
				.longOpt("help")
				.desc("ヘルプ・メッセージを出力します")
				.build());
		options.addOption(
				Option.builder("e")
				.longOpt("encrypt")
				.desc("暗号化モードで起動します")
				.build());
		options.addOption(
				Option.builder("d")
				.longOpt("decrypt")
				.desc("復号モードで起動します")
				.build());
		options.addOption(
				Option.builder("del")
				.longOpt("delete")
				.desc("元のファイルを削除します")
				.build());
		options.addOption(
				Option.builder("ext")
				.longOpt("extension")
				.desc("暗号化後のファイル拡張子を指定します※省略した場合は.logになります")
				.hasArg()
				.argName("拡張子名")
				.build());
		options.addOption(
				Option.builder("p")
				.longOpt("password1")
				.desc("暗号/復号時の第一パスワードを指定します※省略可")
				.hasArg()
				.argName("第一パスワード")
				.build());
		options.addOption(
				Option.builder("pp")
				.longOpt("password2")
				.desc("暗号/復号時の第二パスワードを指定します（第一パスワードを指定したときのみ指定可能※省略可）")
				.hasArg()
				.argName("第二パスワード")
				.build());
		options.addOption(
				Option.builder("f")
				.longOpt("file")
				.desc("暗号化する対象のファイルパスを入力します")
				.hasArgs()
				.argName("暗号化ファイルパス")
				.build());

		// ********************************************************************************
		// 引数チェック
		// ********************************************************************************
		if (args == null || args.length == 0) {
			System.out.println("引数がありません");
			System.out.println("");
			new HelpFormatter().printHelp("オプションは以下の通りです", options);
			ExitCode.ARG_NONE.exit();
			return;
		}

		// ********************************************************************************
		// ヘルプ表示
		// ********************************************************************************
		List<String> argsList = Arrays.asList(args);
		if (argsList.contains("-?") || argsList.contains("-h") || argsList.contains("--help")) {
			System.out.println("使用方法：\tNanoEncrypt [options] -e -f <filePath...>");
			System.out.println("\t\t\t（暗号化する場合）");
			System.out.println("\tまたは\tNanoEncrypt [options] -e -p <password1> (-pp <password2>) -f <filePath...>");
			System.out.println("\t\t\t（パスワード1つ、または2つで暗号化する場合）");
			System.out.println("\tまたは\tNanoEncrypt [options] -d -f <filePath...>");
			System.out.println("\t\t\t（復号する場合）");
			System.out.println("\tまたは\tNanoEncrypt [options] -d -p <password1> (-pp <password2>) -f <filePath...>");
			System.out.println("\t\t\t（パスワード1つ、または2つで復号する場合）");
			System.out.println("");
			new HelpFormatter().printHelp("オプションは以下の通りです", options);
			ExitCode.Success.exit();
			return;
		}

		// ********************************************************************************
		// オプション解析
		// ********************************************************************************
		CommandLine commandLine = null;
		try {
			// 変換
			commandLine = new DefaultParser().parse(options, args);
			// モード
			if (commandLine.hasOption("e")) {
				if (commandLine.hasOption("d")) {
					throw new ParseException("重複モード指定");
				}
			} else {
				if (!commandLine.hasOption("d")) {
					throw new ParseException("モード指定なし");
				}
			}
			// パスワード
			if (!commandLine.hasOption("p") && commandLine.hasOption("pp")) {
				throw new ParseException("パスワード組み合わせ不正");
			}
			// ファイル
			if (commandLine.hasOption("f")) {
				for (String file : commandLine.getOptionValues("f")) {
					if (!new File(file).exists()) {
						throw new ParseException("ファイル未存在\t" + file);
					}
				}
			}
		} catch (ParseException e) {
			System.out.println("オプションの形式が異なります　※" + e.getMessage());
			new HelpFormatter().printHelp("オプションは以下の通りです", options);
			ExitCode.ARG_ATTRIBUTE.exit();
			return;
		}

		// ********************************************************************************
		// 暗号化・復号実行
		// ********************************************************************************
		// 暗号化キーを取得
		String key1 = null;
		String key2 = null;
		if (commandLine.hasOption("pp")) {
			key1 = commandLine.getOptionValue("p");
			key2 = commandLine.getOptionValue("pp");
		} else if (commandLine.hasOption("p")) {
			key1 = commandLine.getOptionValue("p");
		}
		if (commandLine.hasOption("e")) {
			// 暗号化
			try {
				HashMap<String, byte[]> encryptMap = new HashMap<>();
				HashMap<String, String> changeMap = new HashMap<>();
				// 暗号化キーを指定して暗号化
				for (String file : commandLine.getOptionValues("f")) {
					byte[] data = FileUtils.readNormalFileToByte(file);
					// 暗号化
					if (commandLine.hasOption("pp")) {
						data = EncryptUtils.doEncryptToBytes(data, key1, key2);
					} else if (commandLine.hasOption("p")) {
						data = EncryptUtils.doEncryptToBytes(data, key1);
					} else {
						data = EncryptUtils.doEncryptToBytes(data);
					}
					// 元の拡張子保存
					data = EncryptUtils.saveExtension(data, FileUtils.getExtension(file));
					// 拡張子変更
					String ext = null;
					if (commandLine.hasOption("ext")) {
						ext = commandLine.getOptionValue("ext");
					} else {
						ext = ".log";
					}
					String filePath = FileUtils.changeExtension(file, ext);
					encryptMap.put(filePath, data);
					changeMap.put(filePath, file);
				}
				// すべての暗号化に成功した場合、暗号文をファイルに出力
				for (Entry<String, byte[]> entry : encryptMap.entrySet()) {
					FileUtils.writeNormalFile(entry.getKey(), entry.getValue(), false);
					new File(entry.getKey()).setLastModified(new File(changeMap.get(entry.getKey())).lastModified());
					if (commandLine.hasOption("del")) {
						FileUtils.deleteFile(changeMap.get(entry.getKey()));
					}
				}
			} catch (@SuppressWarnings("unused") Exception e) {
				System.out.println("暗号化に失敗しました");
				ExitCode.GENERAL_ERR1.exit();
			}
		} else {
			// 復号
			try {
				HashMap<String, byte[]> decryptMap = new HashMap<>();
				HashMap<String, String> changeMap = new HashMap<>();
				// 暗号化キーを指定して復号
				for (String file : commandLine.getOptionValues("f")) {
					byte[] data = FileUtils.readNormalFileToByte(file);
					// 元の拡張子取得
					String ext = EncryptUtils.loadExtension(data);
					data = EncryptUtils.deleteExtension(data);
					// 復号
					if (commandLine.hasOption("pp")) {
						data = EncryptUtils.doDecryptToBytes(data, key1, key2);
					} else if (commandLine.hasOption("p")) {
						data = EncryptUtils.doDecryptToBytes(data, key1);
					} else {
						data = EncryptUtils.doDecryptToBytes(data);
					}
					// 拡張子変更
					String filePath = FileUtils.changeExtension(file, ext);
					decryptMap.put(filePath, data);
					changeMap.put(filePath, file);
				}
				// すべての復号に成功した場合、平文をファイルに出力
				for (Entry<String, byte[]> entry : decryptMap.entrySet()) {
					FileUtils.writeNormalFile(entry.getKey(), entry.getValue(), false);
					new File(entry.getKey()).setLastModified(new File(changeMap.get(entry.getKey())).lastModified());
					if (commandLine.hasOption("del")) {
						FileUtils.deleteFile(changeMap.get(entry.getKey()));
					}
				}
			} catch (@SuppressWarnings("unused") Exception e) {
				System.out.println("復号に失敗しました");
				ExitCode.GENERAL_ERR2.exit();
			}
		}
	}

}
