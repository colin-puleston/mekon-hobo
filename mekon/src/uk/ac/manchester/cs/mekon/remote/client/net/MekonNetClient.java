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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.client.xml.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;
import uk.ac.manchester.cs.mekon_util.remote.client.*;

/**
 * Represents a client-side version of the MEKON frames model and an
 * associated instance store.
 * <p>
 * This class is designed to be used in combination with the companion
 * server class, <code>MekonNetServer</code>.
 *
 * @author Colin Puleston
 */
public class MekonNetClient {

	private RNetClient netClient;

	private NetClientModel clientModel;
	private NetClientStore clientStore;

	private class NetClientModel extends XClientModel {

		protected XDocument performActionOnServer(XDocument request) {

			return netClient.performActionOnServer(request);
		}

		protected XDocument handleServerAccessException(RServerAccessException exception) {

			return netClient.handleServerAccessException(exception);
		}

		NetClientModel(boolean expireOnServerRestart) {

			super(expireOnServerRestart);
		}
	}

	private class NetClientStore extends XClientStore {

		protected XDocument performActionOnServer(XDocument request) {

			return netClient.performActionOnServer(request);
		}

		protected XDocument handleServerAccessException(RServerAccessException exception) {

			return netClient.handleServerAccessException(exception);
		}

		NetClientStore(boolean expireOnServerRestart) {

			super(getCModel(), expireOnServerRestart);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param netClient Entity providing access to relevant instance of
	 * <code>MekonNetServer</code> running on server
	 * @param expireOnServerRestart true if client should become invalid
	 * if server is restarted whilst client is running
	 */
	public MekonNetClient(RNetClient netClient, boolean expireOnServerRestart) {

		this.netClient = netClient;

		clientModel = new NetClientModel(expireOnServerRestart);
		clientStore = new NetClientStore(expireOnServerRestart);
	}

	/**
	 * Provides the client MEKON frames model.
	 *
	 * @return Client MEKON frames model
	 */
	public CModel getCModel() {

		return clientModel.getCModel();
	}

	/**
	 * Provides the client MEKON instance store.
	 *
	 * @return Client MEKON instance store
	 */
	public IStore getIStore() {

		return clientStore.getIStore();
	}
}
