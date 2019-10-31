
package com.demo.core.common;

public class ResponseEvent<T> {

	private T payload;
	
	private boolean forceTxCommitEnabled;

	private boolean rollback;
	
	private DeException error; 
	
	public ResponseEvent(T payload) {
		this.payload = payload;
	}
	
	public ResponseEvent(DeException error) {
		this.error = error;
	}
	
	public ResponseEvent(DeException error, boolean forceTxCommitEnabled) {
		this.error = error;
		this.forceTxCommitEnabled = forceTxCommitEnabled;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

	public DeException getError() {
		return error;
	}

	public void setError(DeException error) {
		this.error = error;
	}
	
	public boolean isForceTxCommitEnabled() {
		return forceTxCommitEnabled;
	}

	public void setForceTxCommitEnabled(boolean forceTxCommitEnabled) {
		this.forceTxCommitEnabled = forceTxCommitEnabled;
	}

	public boolean isRollback() {
		return rollback;
	}

	public void setRollback(boolean rollback) {
		this.rollback = rollback;
	}

	public void throwErrorIfUnsuccessful() {
		if (error != null) {
			throw error;
		}
	}
	
	public boolean isSuccessful() {
		return error == null;
	}

	public boolean isSystemError() {
		//return error != null && error.getErrorType() == ErrorType.SYSTEM_ERROR;
		return error != null;
	}

	public boolean isUnknownError() {
		//return error != null && error.getErrorType() == ErrorType.UNKNOWN_ERROR;
		return error != null;
	}

	public static <P> ResponseEvent<P> response(P payload) {
		return new ResponseEvent<P>(payload);
	}
	
	public static <P> ResponseEvent<P> error(DeException error) {
		return new ResponseEvent<P>(error);
	}
	
	public static <P> ResponseEvent<P> error(DeException error, boolean forceTxCommitEnabled) {
		return new ResponseEvent<P>(error, forceTxCommitEnabled);
	}
	
//	public static <P> ResponseEvent<P> userError(ErrorCode error) {
//		return new ResponseEvent<P>(DeException.userError(error));
//	}
//
//	public static <P> ResponseEvent<P> serverError(ErrorCode error) {
//		return new ResponseEvent<P>(DeException.serverError(error));
//	}
//	
//	public static <P> ResponseEvent<P> userError(ErrorCode error, boolean forceTxCommitEnabled) {
//		return new ResponseEvent<P>(DeException.userError(error), forceTxCommitEnabled);
//	}
//
//	public static <P> ResponseEvent<P> userError(ErrorCode error, Object ... params) {
//		return new ResponseEvent<P>(DeException.userError(error, params));
//	}
//	
//	public static <P> ResponseEvent<P> serverError(Exception e) {
//		return new ResponseEvent<P>(DeException.serverError(e));
//	}

	public static <P> P unwrap(ResponseEvent<P> resp) {
		resp.throwErrorIfUnsuccessful();
		return resp.getPayload();
	}
}
