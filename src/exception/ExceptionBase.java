package exception;

/**
 * 独自例外のベースクラスです
 * @author blank-nano
 *
 */
@SuppressWarnings("serial")
public abstract class ExceptionBase extends Exception {

	/** プログラムを終了させるか否か */
	protected boolean exitFlg = true;
	/** ログ連結記号 */
	protected static final String PIPE = "|";

	/**
	 * デフォルトコンストラクタ
	 */
	public ExceptionBase() {
		super();
	}

	/**
	 * デフォルトコンストラクタ
	 * @param messgae - メッセージ
	 */
	public ExceptionBase(String messgae) {
		super(messgae);
	}

	/**
	 * プログラムを終了させる場合は終了
	 */
	public void judgeExit() {
		if (this.exitFlg) {
			System.exit(0);
		}
	}

	/**
	 * ログメッセージ用に文字列を連結します
	 * @param message - 連結したいデータ
	 * @return - 連結文字列
	 */
	protected static String join(String... message) {
		if (message == null || message.length == 0) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		for (String msg : message) {
			if (msg == null) {
				continue;
			}
			sb.append(msg);
		}
		return sb.toString();
	}

}
