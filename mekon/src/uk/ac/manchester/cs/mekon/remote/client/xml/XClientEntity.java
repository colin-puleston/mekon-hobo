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

package uk.ac.manchester.cs.mekon.remote.client.xml;

import uk.ac.manchester.cs.mekon_util.remote.client.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Base class for client-side versions of the major MEKON entities
 * (frames model and instance store), with all client/server
 * interaction being via XML-based representations of specific actions.
 *
 * @author Colin Puleston
 */
public abstract class XClientEntity {

	private long expiryCheckTime;

	/**
	 * Accesses the server to perform a particular action.
	 *
	 * @param requestDoc Document representing specification of required
	 * action
	 * @return Document representing output produced by action
	 */
	protected abstract XDocument performActionOnServer(XDocument requestDoc);

	/**
	 * Handles a client-generated server-access exception. The default
	 * method, which simply throws the supplied exception, can be
	 * overriden to perform other types of action.
	 *
	 * @param exception Exception to be handled
	 * @return document, or null value, to be handled by application
	 * (assuming no exception is thrown, or system exited)
	 */
	protected XDocument handleServerAccessException(RServerAccessException exception) {

		throw exception;
	}

	XClientEntity(boolean expireOnServerRestart) {

		expiryCheckTime = expireOnServerRestart ? System.currentTimeMillis() : -1;
	}

	XResponseParser performAction(XRequestRenderer request) {

		if (expiryCheckTime != -1) {

			request.setClientExpiryCheckTime(expiryCheckTime);
		}

		XDocument requestDoc = request.getDocument();
		XDocument responseDoc = performActionOnServer(requestDoc);

		XResponseParser response = new XResponseParser(responseDoc);

		if (response.invalidatedClient()) {

			response = new XResponseParser(handleInvalidatedClientStart());
		}

		return response;
	}

	private XDocument handleInvalidatedClientStart() {

		return handleServerAccessException(createInvalidatedClientException());
	}

	private RServerAccessException createInvalidatedClientException() {

		return new RServerAccessException("Current client session is no longer valid!");
	}
}
