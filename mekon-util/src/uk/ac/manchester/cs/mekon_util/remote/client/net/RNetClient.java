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

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.*;

/**
 * Represents a network client, via which server-side actions
 * can be specified and performed.
 *
 * @author Colin Puleston
 */
public class RNetClient {

	static private final int CONNECT_TIMEOUT = 20000;
	static private final int READ_TIMEOUT = 60000;

	static class DefaultExceptionHandler implements RNetClientExceptionHandler {

		public XDocument handle(RConnectionException exception) {

			throw exception;
		}

		public XDocument handle(RServerAccessException exception) {

			throw exception;
		}
	}

	private URL serverURL;
	private RNetClientExceptionHandler exceptionHandler = new DefaultExceptionHandler();

	/**
	 * Constructor.
	 *
	 * @param serverURL URL providing access to server
	 */
	public RNetClient(URL serverURL) {

		this.serverURL = serverURL;
	}

	/**
	 * Sets handler for any runtime-exceptions resulting from
	 * server-access operations. By default all such exceptions will
	 * simply be thrown/re-thrown, without any other actions being
	 * performed.
	 *
	 * @param exceptionHandler Relevant exception-handler
	 */
	public void setExceptionHandler(RNetClientExceptionHandler exceptionHandler) {

		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * Accesses the server to perform a specific action.
	 *
	 * @param requestDoc Document representing specification of
	 * required action
	 * @return Document representing output produced by action
	 */
	public XDocument performActionOnServer(XDocument request) {

		try {

			URLConnection connection = connect();

			send(connection, request);

			return receive(connection);
		}
		catch (IOException e) {

			return exceptionHandler.handle(new RConnectionException(e));
		}
		catch (RServerAccessException e) {

			return exceptionHandler.handle(e);
		}
	}

	private URLConnection connect() throws IOException {

		URLConnection connection = serverURL.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(CONNECT_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);

		connection.connect();

		return connection;
	}

	private void send(URLConnection connection, XDocument request) throws IOException {

		OutputStream output = connection.getOutputStream();

		request.writeToOutput(new BufferedOutputStream(output));
		output.close();
	}

	private XDocument receive(URLConnection connection) throws IOException {

		InputStream input = connection.getInputStream();
		XDocument response = new XDocument(new BufferedInputStream(input));

		input.close();

		return response;
	}
}
