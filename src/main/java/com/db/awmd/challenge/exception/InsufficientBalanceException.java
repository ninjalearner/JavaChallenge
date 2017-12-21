package com.db.awmd.challenge.exception;

/**
 * 
 * Exception class for insufficient balance error handling
 * 
 * @author nikhil.agrawal
 *
 */
public class InsufficientBalanceException extends Exception{

	public InsufficientBalanceException(String message) {
		super(message);
	}
}
