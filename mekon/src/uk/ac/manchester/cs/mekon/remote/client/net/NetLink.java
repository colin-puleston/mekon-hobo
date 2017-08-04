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

import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class NetLink {

	private URLConnection connection;

	private InputStream input = null;
	private OutputStream output = null;

	NetLink(URL serverURL) throws IOException {

		connection = serverURL.openConnection();

		connection.connect();
	}

	void writeDocument(XDocument document) throws IOException {

		document.writeToOutput(getOutputStream());
	}

	XDocument readDocument() throws IOException {

		return new XDocument(getInputStream());
	}

	Boolean readBoolean() throws IOException {

		return RBoolean.fromInteger(getInputStream().read());
	}

	void close() throws IOException {

		if (input != null) {

			input.close();
		}

		if (output != null) {

			output.close();
		}
	}

	private InputStream getInputStream() throws IOException {

		if (input == null) {

			input = connection.getInputStream();
		}

		return input;
	}

	private OutputStream getOutputStream() throws IOException {

		if (output == null) {

			output = connection.getOutputStream();
		}

		return output;
	}
}
