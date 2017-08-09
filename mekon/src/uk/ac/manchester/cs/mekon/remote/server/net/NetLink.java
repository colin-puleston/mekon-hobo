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

package uk.ac.manchester.cs.mekon.remote.server.net;

import java.io.*;
import javax.servlet.*;

import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class NetLink {

	private ServletRequest request;
	private ServletResponse response;

	private ServerActionSpec actionSpec;

	private InputStream input = null;
	private OutputStream output = null;

	NetLink(ServletRequest request, ServletResponse response) throws ServletException {

		this.request = request;
		this.response = response;

		actionSpec = new ServerActionSpec(request);
	}

	ServerActionSpec getActionSpec() {

		return actionSpec;
	}

	XDocument readDocument() throws ServletException, IOException {

		try {

			return new XDocument(getInputStream());
		}
		catch (XDocumentException e) {

			throw new ServletException(e);
		}
	}

	void writeDocument(XDocument document) throws ServletException, IOException {

		try {

			document.writeToOutput(getOutputStream());
		}
		catch (XDocumentException e) {

			throw new ServletException(e);
		}
	}

	void checkWriteDocument(XDocument document) throws ServletException, IOException {

		if (document != null) {

			writeDocument(document);
		}
	}

	void writeBoolean(boolean value) throws IOException {

		getOutputStream().write(RBoolean.toInteger(value));
	}

	private InputStream getInputStream() throws IOException {

		if (input == null) {

			input = new BufferedInputStream(request.getInputStream());
		}

		return input;
	}

	private OutputStream getOutputStream() throws IOException {

		if (output == null) {

			output = response.getOutputStream();
		}

		return output;
	}
}
