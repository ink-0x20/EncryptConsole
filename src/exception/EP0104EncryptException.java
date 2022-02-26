package exception;

/**
 * 暗号化失敗例外を定義します
 * @author blank-nano
 *
 */
public class EP0104EncryptException extends ExceptionBase {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "暗号化失敗";

	/**
	 * デフォルトコンストラクタ。
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0104EncryptException(boolean exitFlg) {
		super(MESSAGE);
		this.exitFlg = exitFlg;
	}

	/**
	 * デフォルトコンストラクタ。
     * @param target - 暗号化対象文字列。
	 * @param key - 暗号化キー。
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0104EncryptException(String target, String key, boolean exitFlg) {
		super(MESSAGE + "_target=[" + target + "],key=[" + key + "]");
		this.exitFlg = exitFlg;
	}

}
