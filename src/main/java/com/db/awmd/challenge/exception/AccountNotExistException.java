package com.db.awmd.challenge.exception;

/**
 * Exception class for account not exist handling
 * 
 * @author nihil.agrawal
 *
 */
public class AccountNotExistException extends Exception{

	public AccountNotExistException(String message) {
		super(message);
	}
}
