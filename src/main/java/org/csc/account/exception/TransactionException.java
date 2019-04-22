package org.csc.account.exception;

public class TransactionException extends BlockException {

	public TransactionException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransactionException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

	public TransactionException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TransactionException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	
}
