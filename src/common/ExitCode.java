package common;

/**
 * コマンドの終了コードを定義します
 * @author blank-nano
 *
 */
public enum ExitCode {

	  Success(0)
	, ARG_NONE(1)
	, ARG_ATTRIBUTE(2)
	, GENERAL_ERR1(11)
	, GENERAL_ERR2(12)
	, GENERAL_ERR3(13)
	, GENERAL_ERR4(14)
	, GENERAL_ERR5(15)
	, GENERAL_ERR6(16)
	, GENERAL_ERR7(17)
	, GENERAL_ERR8(18)
	, GENERAL_ERR9(19)
	;

	private int exitCode;

	private ExitCode(int exitCode) {
		this.exitCode = exitCode;
	}

	/**
	 * 終了コードをセットして、システムを終了する
	 */
	public void exit() {
		System.exit(this.exitCode);
	}

}
