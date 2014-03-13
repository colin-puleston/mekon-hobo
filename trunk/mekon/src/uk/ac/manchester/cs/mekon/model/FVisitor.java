/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2014 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package uk.ac.manchester.cs.mekon.model;

import uk.ac.manchester.cs.mekon.*;

/**
 * Abstract base-class for visitors that visit entities from the
 * Frames Model (FM).
 *
 * @author Colin Puleston
 */
public abstract class FVisitor<A> {

	/**
	 * Causes relevant sub-class-specific visit method to be invoked,
	 * for cases where visit methods are not expected to throw any
	 * exceptions.
	 *
	 * @param acceptor Object that is to accept visitor.
	 */
	public void visit(A acceptor) {

		try {

			performVisit(acceptor);
		}
		catch (RuntimeException e) {

			throw e;
		}
		catch (Exception e) {

			processUnexpectedException(e);
		}
	}

	/**
	 * Causes relevant sub-class-specific visit method to be invoked,
	 * for cases where visit methods may throw expections of the
	 * specified type.
	 *
	 * @param acceptor Object that is to accept visitor.
	 * @param exceptionType Type of exception that may be thrown.
	 * @throws Exception thrown by sub-class-specific visit method, if
	 * applicable.
	 */
	public <E extends Exception>void visit(
										A acceptor,
										Class<E> exceptionType)
										throws E {

		try {

			performVisit(acceptor);
		}
		catch (RuntimeException e) {

			throw e;
		}
		catch (Exception e) {

			processException(e, exceptionType);
		}
	}

	abstract void performVisit(A acceptor) throws Exception;

	private void processUnexpectedException(Exception exception) {

		checkRuntimeException(exception);

		throw new KAccessException(
					"Unexpected exception: " + exception
					+ " (exception should never be"
					+ " thrown when using this method)");
	}

	private <E extends Exception> void processException(
											Exception exception,
											Class<E> exceptionType)
											throws E {

		checkRuntimeException(exception);

		if (exceptionType.isAssignableFrom(exception.getClass())) {

			throw exceptionType.cast(exception);
		}

		throw new KAccessException(
					"Unexpected exception: " + exception
					+ " (exceptions should be of type: "
					+ exceptionType + ")");
	}

	private void checkRuntimeException(Exception exception) {

		if (exception instanceof RuntimeException) {

			throw (RuntimeException)exception;
		}
	}
}
