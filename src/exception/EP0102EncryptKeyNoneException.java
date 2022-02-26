package exception;

/**
 * 暗号化キー欠落例外を定義します
 * @author blank-nano
 *
 */
public class EP0102EncryptKeyNoneException extends ExceptionBase {

	private static final long serialVersionUID = 1L;
	private static final String MESSAGE = "暗号化キー欠落";

	/**
	 * デフォルトコンストラクタ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0102EncryptKeyNoneException(boolean exitFlg) {
		super(MESSAGE);
		this.exitFlg = exitFlg;
	}

	/**
	 * デフォルトコンストラクタ
	 * @param message - ログメッセージ
	 * @param exitFlg - プログラム終了フラグ
	 */
	public EP0102EncryptKeyNoneException(String message, boolean exitFlg) {
		super(join(MESSAGE, PIPE, message));
		this.exitFlg = exitFlg;
	}

}
