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
package uk.ac.manchester.cs.mekon.gui;

import uk.ac.manchester.cs.mekon.manage.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.client.xml.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;

/**
 * @author Colin Puleston
 */
public class MekonRemoteTestModelExplorer {

	private static class LocalXClientModel extends XClientModel {

		private XServerModel serverModel;

		protected XDocument initialiseAssertionOnServer(XDocument assertionDoc) {

			return serverModel.initialiseAssertion(assertionDoc);
		}

		protected XDocument initialiseQueryOnServer(XDocument queryDoc) {

			return serverModel.initialiseQuery(queryDoc);
		}

		protected XDocument updateAssertionOnServer(XDocument assertionDoc) {

			return serverModel.updateAssertion(assertionDoc);
		}

		protected XDocument updateQueryOnServer(XDocument queryDoc) {

			return serverModel.updateQuery(queryDoc);
		}

		LocalXClientModel(XServerModel serverModel) {

			super(serverModel.getCFrameHierarchy());

			this.serverModel = serverModel;
		}
	}

	private static class LocalXClientStore extends XClientStore {

		private XServerStore serverStore;

		protected XDocument addOnServer(XDocument instance, XDocument identity) {

			return serverStore.add(instance, identity);
		}

		protected boolean removeOnServer(XDocument identity) {

			return serverStore.remove(identity);
		}

		protected void clearOnServer() {

			serverStore.clear();
		}

		protected boolean containsOnServer(XDocument identity) {

			return serverStore.contains(identity);
		}

		protected XDocument getOnServer(XDocument identity) {

			return serverStore.get(identity);
		}

		protected XDocument getAllIdentitiesOnServer() {

			return serverStore.getAllIdentities();
		}

		protected XDocument matchOnServer(XDocument query) {

			return serverStore.match(query);
		}

		protected boolean matchesOnServer(XDocument query, XDocument instance) {

			return serverStore.matches(query, instance);
		}

		LocalXClientStore(XClientModel clientModel, XServerStore serverStore) {

			super(clientModel);

			this.serverStore = serverStore;
		}
	}

	static public void main(String[] args) {

		CBuilder builder = CManager.createBuilder();

		create(builder.build(), builder, createStore(builder));
	}

	static public void create(CModel model, CBuilder builder) {

		XServerModel serverModel = new XServerModel(model, builder);
		XClientModel clientModel = new LocalXClientModel(serverModel);

		new MekonModelExplorer(clientModel.getCModel());
	}

	static public void create(CModel model, CBuilder builder, IStore store) {

		XServerModel serverModel = new XServerModel(model, builder);
		XServerStore serverStore = new XServerStore(store);

		XClientModel clientModel = new LocalXClientModel(serverModel);
		XClientStore clientStore = new LocalXClientStore(clientModel, serverStore);

		new MekonModelExplorer(clientModel.getCModel(), clientStore);
	}

	static private IStore createStore(CBuilder builder) {

		return IDiskStoreManager.getBuilder(builder).build();
	}
}
