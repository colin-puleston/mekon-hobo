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

package uk.ac.manchester.cs.mekon_util.remote.client.net;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.*;

/**
 * Handles runtime-exceptions caused by client access of a
 * network connection. Each handling method will result, possibly
 * after some other action(s), such as user notification, in one
 * of the following actions being performed (a) throwing of the
 * provided exception (b) throwing of some other runtime-exception,
 * (c) system exit, or (d) return of a document, or null value,
 * signifying to the invoking application that an exception
 * occurred and the action was not performed.
 *
 * @author Colin Puleston
 */
public interface RNetClientExceptionHandler {

	/**
	 * Handles network-connection exception.
	 *
	 * @param exception Exception to be handled
	 * @return document, or null value, to be handled by application
	 */
	public XDocument handle(RConnectionException exception);

	/**
	 * Handles server-access exception.
	 *
	 * @param exception Exception to be handled
	 * @return document, or null value, to be handled by application
	 */
	public XDocument handle(RServerAccessException exception);
}
