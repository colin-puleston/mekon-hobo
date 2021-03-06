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

package uk.ac.manchester.cs.mekon_util.remote.client;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.*;

/**
 * Represents a network client, via which server-side actions
 * can be specified and performed.
 *
 * @author Colin Puleston
 */
public class RNetClient implements RNetVocab {

	/**
	 * Creates object and invokes {@link #initialiseServer} method,
	 * interpreting first argument as the required server URL.
	 *
	 * @param args Expects single argument representing URL providing
	 * access to server
	 * @throws MalformedURLException if first argument exists but
	 * does not represent a valid URL
	 */
	static public void main(String[] args) throws MalformedURLException {

		if (args.length != 0) {

			new RNetClient(new URL(args[0])).initialiseServer();
		}
		else {

			System.out.println("No URL argument found");
		}
	}

	static class DefaultExceptionHandler implements RClientExceptionHandler {

		public XDocument handle(RConnectionException exception) {

			throw exception;
		}

		public XDocument handle(RServerAccessException exception) {

			throw exception;
		}
	}

	private URL serverURL;

	private RClientExceptionHandler exceptionHandler = new DefaultExceptionHandler();

	private int connectTimeoutInMillis = 0;
	private int readTimeoutInMillis = 0;

	/**
	 * Constructor.
	 *
	 * @param serverURL URL providing access to server
	 */
	public RNetClient(URL serverURL) {

		this.serverURL = serverURL;
	}

	/**
	 * Sets a value for connect timeout. By default these will be no
	 * connect timeout.
	 *
	 * @param timeInSeconds Required connect timeout value in seconds
	 */
	public void setConnectTimeout(int timeInSeconds) {

		connectTimeoutInMillis = timeInSeconds * 1000;
	}

	/**
	 * Sets a value for read timeout. By default these will be no read
	 * timeout.
	 *
	 * @param timeInSeconds Required read timeout value in seconds
	 */
	public void setReadTimeout(int timeInSeconds) {

		readTimeoutInMillis = timeInSeconds * 1000;
	}

	/**
	 * Sets handler for any runtime-exceptions resulting from
	 * server-access operations. By default all such exceptions will
	 * simply be thrown/re-thrown, without any other actions being
	 * performed.
	 *
	 * @param exceptionHandler Relevant exception-handler
	 */
	public void setExceptionHandler(RClientExceptionHandler exceptionHandler) {

		this.exceptionHandler = exceptionHandler;
	}

	/**
	 * Accesses the server to perform any required initialisations.
	 *
	 * @return Document containing single root-node if initialisation
	 * successful, or document, or null value, produced by exception
	 * handler if relevant
	 */
	public XDocument initialiseServer() {

		return performActionOnServer(new XDocument(SERVER_INIT_REQUEST_ID));
	}

	/**
	 * Accesses the server to perform a specific action.
	 *
	 * @param request Document representing specification of required
	 * action
	 * @return Document representing output produced by action, or
	 * document, or null value, produced by exception handler if
	 * relevant
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

	/**
	 * Uses the net-clients exception-handler (see {@link
	 * #setExceptionHandler}) to handle a client-generated server-access
	 * exception.
	 *
	 * @param exception Exception to be handled
	 * @return document, or null value, to be handled by application
	 * (assuming no exception is thrown, or system exited)
	 */
	public XDocument handleServerAccessException(RServerAccessException exception) {

		return exceptionHandler.handle(exception);
	}

	private URLConnection connect() throws IOException {

		URLConnection connection = serverURL.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setConnectTimeout(connectTimeoutInMillis);
		connection.setReadTimeout(readTimeoutInMillis);

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
