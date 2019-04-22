package org.csc.account.exception;

public class TransactionExecuteException extends BlockException {
	public TransactionExecuteException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionExecuteException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransactionExecuteException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public TransactionExecuteException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TransactionExecuteException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
}
