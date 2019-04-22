package org.csc.account.exception;

public class TransactionParameterInvalidException extends BlockException {
	public TransactionParameterInvalidException(String msg) {
		super(msg);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionParameterInvalidException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public TransactionParameterInvalidException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public TransactionParameterInvalidException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}
	
	
}
