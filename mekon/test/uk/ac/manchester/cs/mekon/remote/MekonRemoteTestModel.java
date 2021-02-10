/**
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
package uk.ac.manchester.cs.mekon.remote;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.client.xml.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
public class MekonRemoteTestModel {

	public final CModel clientModel;
	public final IStore clientStore;

	private CModel serverModel;
	private XServer server;

	private class LocalXClientModel extends XClientModel {

		protected XDocument performActionOnServer(XDocument request) {

			return server.performAction(request);
		}

		LocalXClientModel( ) {

			super(false);
		}
	}

	private class LocalXClientStore extends XClientStore {

		protected XDocument performActionOnServer(XDocument request) {

			return server.performAction(request);
		}

		LocalXClientStore(CModel model) {

			super(model, false);
		}
	}

	public MekonRemoteTestModel(CModel serverModel) {

		this(serverModel, IDiskStoreManager.getBuilder(serverModel).build());
	}

	public MekonRemoteTestModel(CModel serverModel, IStore serverStore) {

		this.serverModel = serverModel;

		server = new XServer(serverModel);
		server.setStore(serverStore);

		clientModel = createClientModel();
		clientStore = createClientStore();
	}

	public IStore resetServerStore() {

		return resetServerStore(IDiskStoreManager.getBuilder(serverModel).build());
	}

	public IStore resetServerStore(IStore serverStore) {

		server.setStore(serverStore);

		return serverStore;
	}

	private CModel createClientModel() {

		return new LocalXClientModel().getCModel();
	}

	private IStore createClientStore() {

		return new LocalXClientStore(clientModel).getIStore();
	}
}
