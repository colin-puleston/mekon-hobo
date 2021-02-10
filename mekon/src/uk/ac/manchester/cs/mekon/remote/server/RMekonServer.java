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

package uk.ac.manchester.cs.mekon.remote.server;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.server.*;

/**
 * Servlet that provides access to a server-side version of the MEKON
 * frames model and an associated instance store.
 * <p>
 * This class is designed to be used in combination with the companion
 * client class, <code>RMekonClient</code>.
 *
 * @author Colin Puleston
 */
public class RMekonServer extends RNetServer {

	static private final long serialVersionUID = -1;

	private XServer xServer = null;

	/**
	 * Invokes {@link #createXServer} method to create the {@link
	 * XServer} object that will handle the server operations.
	 */
	protected void initNetServer() {

		xServer = createXServer();
	}

	/**
	 * {@inheritDoc}
	 */
	protected XDocument performAction(XDocument request) {

		return xServer.performAction(request);
	}

	/**
	 * Creates the object that will handle the server operations.
	 * The default implementation utilises both a MEKON model and a
	 * disk-based instance-store, each specified via a configuration
	 * file located somewhere on the classpath.
	 *
	 * @return Created object for handling server operations
	 */
	protected XServer createXServer() {

		CBuilder cBuilder = CManager.createBuilder();
		XServer xServer = new XServer(cBuilder.build());

		xServer.setStore(createStore(cBuilder));

		return xServer;
	}

	private IStore createStore(CBuilder cBuilder) {

		return IDiskStoreManager.getBuilder(cBuilder).build();
	}
}