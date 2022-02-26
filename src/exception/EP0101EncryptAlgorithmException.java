package exception;

/**
 * 暗号アルゴリズム不正例外を定義します
 * @author blank-nano
 *
 */
public class EP0101EncryptAlgorithmException extends ExceptionBase {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "暗号アルゴリズム不正";

	/**
	 * デフォルトコンストラクタ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0101EncryptAlgorithmException(boolean exitFlg) {
		super(MESSAGE);
		this.exitFlg = exitFlg;
	}

	/**
	 * デフォルトコンストラクタ
	 * @param message - ログメッセージ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0101EncryptAlgorithmException(String message, boolean exitFlg) {
		super(join(MESSAGE, PIPE, message));
		this.exitFlg = exitFlg;
	}

}
