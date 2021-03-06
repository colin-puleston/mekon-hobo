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

package uk.ac.manchester.cs.hobo.remote.server;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.server.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;

import uk.ac.manchester.cs.hobo.manage.*;
import uk.ac.manchester.cs.hobo.model.motor.*;

/**
 * Represents a server-side version of the MEKON frames model, with a
 * bound HOBO object model, and an associated instance store.
 * <p>
 * This class is an extension of {@link RMekonServer}, which is
 * designed to be used in combination with the companion server class,
 * <code>RMekonClient</code>.
 *
 * @author Colin Puleston
 */
public class RHoboServer extends RMekonServer {

	static private final long serialVersionUID = -1;

	/**
	 * Creates the object that will handle the server operations.
	 * Overrides the default implementation to provide an object
	 * that utilises both a HOBO model and a disk-based instance-store,
	 * each specified via a configuration file located somewhere on
	 * the classpath.
	 *
	 * @return Created object for handling server operations
	 */
	protected XServer createXServer() {

		DBuilder dBuilder = DManager.createBuilder();
		XServer xServer = new XServer(dBuilder.build().getCModel());

		xServer.setStore(createStore(dBuilder));

		return xServer;
	}

	private IStore createStore(DBuilder dBuilder) {

		return IDiskStoreManager.getBuilder(dBuilder.getCBuilder()).build();
	}
}