package com.technosophos.lantern.commands.auth;

import com.technosophos.lantern.SinciputException;

/**
 * This exception indicates that there is an authentication problem.
 * @author mbutcher
 *
 */
public class AuthenticationException extends SinciputException {

	private static final long serialVersionUID = 1L;
	
	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(String message, Throwable thr) {
		super(message, thr);
	}

}
