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

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;

/**
 * Servlet that provides access to a server-side version of the MEKON
 * frames model and an associated instance store.
 * <p>
 * This class is designed to be used in combination with the companion
 * client class, <code>MekonNetClient</code>.
 *
 * @author Colin Puleston
 */
public class MekonNetServer extends GenericServlet {

	static private final long serialVersionUID = -1;

	private XServer server = null;

	public void init() throws ServletException {

		setLibraryPath();

		try {

			server = new XServer(createModel());
		}
		catch (KRuntimeException e) {

			throw new ServletException(e);
		}
	}

	public void service(
					ServletRequest request,
					ServletResponse response)
					throws ServletException, IOException {

		try {

			XDocument requestDoc = new XDocument(getInputStream(request));
			XDocument responseDoc = server.performAction(requestDoc);

			responseDoc.writeToOutput(getOutputStream(response));
		}
		catch (KRuntimeException e) {

			throw new ServletException(e);
		}
	}

	public void destroy() {
	}

	protected CModel createModel() {

		return CManager.createBuilder().build();
	}

	private InputStream getInputStream(ServletRequest request) throws IOException {

		return new BufferedInputStream(request.getInputStream());
	}

	private OutputStream getOutputStream(ServletResponse response) throws IOException {

		return new BufferedOutputStream(response.getOutputStream());
	}

	private void setLibraryPath() {

		LibraryPathHandler.setLibraryPath(getServletContext());
	}
}