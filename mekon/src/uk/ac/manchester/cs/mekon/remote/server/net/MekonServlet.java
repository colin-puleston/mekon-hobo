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

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.motor.*;

/**
 * XXX.
 *
 * @author Colin Puleston
 */
public class MekonServlet extends GenericServlet {

	static private final long serialVersionUID = -1;

	private ModelActions modelActions = null;
	private StoreActions storeActions = null;

	/**
	 */
	public void init() throws ServletException {

		CBuilder cBuilder = CManager.createBuilder();

		modelActions = new ModelActions(cBuilder);
		storeActions = new StoreActions(cBuilder);
	}

	/**
	 */
	public void service(
					ServletRequest request,
					ServletResponse response)
					throws ServletException, IOException {

		response.setContentType("text/html");

		performAction(new ServerIO(request, response));
	}

	/**
	 */
	public void destroy() {
	}

	private void performAction(ServerIO io) throws ServletException, IOException {

		if (modelActions.checkPerformAction(io)) {

			return;
		}

		if (storeActions.checkPerformAction(io)) {

			return;
		}

		throw io.getActionSpec().getBadSpecException();
	}
}