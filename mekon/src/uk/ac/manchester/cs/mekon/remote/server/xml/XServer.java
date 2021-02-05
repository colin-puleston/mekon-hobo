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

package uk.ac.manchester.cs.mekon.remote.server.xml;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon.remote.server.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.server.*;

/**
 * Provides access to a server-side version of the MEKON frames model,
 * and optionally an associated instance store, with all client/server
 * interaction being via XML-based representations of specific actions.
 * <p>
 * This class is designed to be used in combination with the companion
 * XML-based client classes, <code>XClientModel</code> and
 * <code>XClientStore</code>. All encoding and decoding of the action
 * requests and responses is handled by this set of client/server
 * classes. Hence the extending and wrapper classes are only required
 * to pass on the relevant documents, and never need to interpret any
 * of the XML contained within.
 *
 * @author Colin Puleston
 */
public class XServer {

	private CModel model;

	private ModelActions modelActions;
	private StoreActions storeActions = null;

	private long serverStartupTime = System.currentTimeMillis();

	/**
	 * Constructor.
	 *
	 * @param model Server-side frames model
	 */
	public XServer(CModel model) {

		this.model = model;

		modelActions = new ModelActions(model);
	}

	/**
	 * Allows the specification of a server-side MEKON instance store.
	 *
	 * @param store Server-side MEKON instance store
	 */
	public void setStore(IStore store) {

		storeActions = new StoreActions(store);
	}

	/**
	 * Performs a particular model or store-related action.
	 *
	 * @param requestDoc Document representing specification of required
	 * action
	 * @return Document representing output produced by action
	 */
	public XDocument performAction(XDocument requestDoc) {

		XRequestParser request = new XRequestParser(requestDoc);
		XResponseRenderer response = new XResponseRenderer();

		if (performAction(request, response)) {

			return response.getDocument();
		}

		throw new RServerException(
					"Unrecognised server action category: "
					+ "\"" + request.getActionCategory() + "\"");
	}

	private boolean performAction(XRequestParser request, XResponseRenderer response) {

		if (checkExpiredClient(request, response)) {

			return true;
		}

		if (modelActions.checkPerformAction(request, response)) {

			return true;
		}

		if (storeActions != null && storeActions.checkPerformAction(request, response)) {

			return true;
		}

		return false;
	}

	private boolean checkExpiredClient(XRequestParser request, XResponseRenderer response) {

		long time = request.getClientExpiryCheckTime();

		if (time != -1 && time < serverStartupTime) {

			response.setInvalidatedClient();

			return true;
		}

		return false;
	}
}