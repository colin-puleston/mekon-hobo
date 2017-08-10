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

package uk.ac.manchester.cs.mekon.remote.client.net;

import java.io.*;
import java.net.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.client.*;
import uk.ac.manchester.cs.mekon.remote.client.xml.*;

/**
 * XXX.
 *
 * @author Colin Puleston
 */
public class MekonNetClient {

	private URL serverURL;

	private CModel model;
	private IStore store;

	private class NetClientModel extends XClientModel {

		protected XDocument performActionOnServer(XDocument request) {

			return performAction(request);
		}
	}

	private class NetClientStore extends XClientStore {

		protected XDocument performActionOnServer(XDocument request) {

			return performAction(request);
		}

		NetClientStore() {

			super(model);
		}
	}

	/**
	 * XXX.
	 */
	public MekonNetClient(URL serverURL) {

		this.serverURL = serverURL;

		model = new NetClientModel().getCModel();
		store = new NetClientStore().getIStore();
	}

	/**
	 * Provides the client MEKON frames model.
	 *
	 * @return Client MEKON frames model
	 */
	public CModel getCModel() {

		return model;
	}

	/**
	 * Provides the client MEKON frames store.
	 *
	 * @return Client MEKON frames store
	 */
	public IStore getIStore() {

		return store;
	}

	private XDocument performAction(XDocument request) {

		try {

			URLConnection connection = connect();

			send(connection, request);

			return receive(connection);
		}
		catch (IOException e) {

			throw new RServerAccessException(e);
		}
	}

	private URLConnection connect() throws IOException {

		URLConnection connection = serverURL.openConnection();

		connection.setDoInput(true);
		connection.setDoOutput(true);

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
