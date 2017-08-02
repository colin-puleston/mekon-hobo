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

/**
 * @author Colin Puleston
 */
class ServerIO {

	private ServletRequest request;
	private ServletResponse response;

	private ServerActionSpec actionSpec;

	ServerIO(ServletRequest request, ServletResponse response) throws ServletException {

		this.request = request;
		this.response = response;

		actionSpec = new ServerActionSpec(request);
	}

	ServerActionSpec getActionSpec() {

		return actionSpec;
	}

	XDocument acceptDocument() throws ServletException, IOException {

		try {

			return new XDocument(request.getInputStream());
		}
		catch (Throwable t) {

			throw new ServletException(t);
		}
	}

	void returnDocument(XDocument document) throws ServletException, IOException {

		try {

			document.writeToOutput(response.getOutputStream());
		}
		catch (Throwable t) {

			throw new ServletException(t);
		}
	}

	void checkReturnDocument(XDocument document) throws ServletException, IOException {

		if (document != null) {

			returnDocument(document);
		}
	}

	void returnBoolean(Boolean value) throws IOException {

		response.getWriter().append(value.toString());
	}
}
