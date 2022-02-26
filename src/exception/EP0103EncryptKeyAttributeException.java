package exception;

/**
 * 暗号化キー不正例外を定義します
 * @author blank-nano
 *
 */
public class EP0103EncryptKeyAttributeException extends ExceptionBase {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "暗号化キー不正";

	/**
	 * デフォルトコンストラクタ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0103EncryptKeyAttributeException(boolean exitFlg) {
		super(MESSAGE);
		this.exitFlg = exitFlg;
	}

	/**
	 * デフォルトコンストラクタ
	 * @param message - ログメッセージ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0103EncryptKeyAttributeException(String message, boolean exitFlg) {
		super(join(MESSAGE, PIPE, message));
		this.exitFlg = exitFlg;
	}

}
