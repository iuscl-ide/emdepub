/* Emdepub Eclipse Plugin - emdepub.org */
package org.emdepub.activator;

/** Break to top exception */
public class E extends RuntimeException {

	private static final long serialVersionUID = 7621240120763198277L;

	private final Throwable throwable;

	public E(Throwable throwable) {
		super();
		this.throwable = throwable;
	}

	public Throwable getThrowable() {
		return throwable;
	}
}
