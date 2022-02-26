package exception;

/**
 * 復号失敗例外を定義します
 * @author blank-nano
 *
 */
public class EP0105DecryptException extends ExceptionBase {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "復号失敗";

	/**
	 * デフォルトコンストラクタ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0105DecryptException(boolean exitFlg) {
		super(MESSAGE);
		this.exitFlg = exitFlg;
	}

	/**
	 * デフォルトコンストラクタ
	 * @param message - ログメッセージ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0105DecryptException(String message, boolean exitFlg) {
		super(join(MESSAGE, PIPE, message));
		this.exitFlg = exitFlg;
	}

}
