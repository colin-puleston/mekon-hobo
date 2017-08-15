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

package uk.ac.manchester.cs.mekon.remote.client.xml;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * Represents a client-side version of the MEKON instance store, with
 * all client/server interaction being via XML-based representations
 * of specific actions.
 * <p>
 * Both this class and {@link XClientModel} are designed to be used
 * in combination with the companion XML-based server class,
 * <code>XServer</code>. All encoding and decoding of the action
 * requests and responses is handled by this set of client/server
 * classes. Hence the extending and wrapper classes are only required
 * to pass on the relevant documents, and never need to interpret any
 * of the XML contained within.
 *
 * @author Colin Puleston
 */
public abstract class XClientStore {

	private CModel model;
	private IStore store = new XClientIStore();

	private IInstanceParser assertionParser;

	private class XClientIStore implements IStore {

		public CModel getModel() {

			return model;
		}

		public IFrame add(IFrame instance, CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.ADD);

			request.addParameter(instance);
			request.addParameter(identity);

			return performAssertionOrNullResponseAction(request);
		}

		public boolean remove(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.REMOVE);

			request.addParameter(identity);

			return performBooleanResponseAction(request);
		}

		public boolean clear() {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.CLEAR);

			return performBooleanResponseAction(request);
		}

		public boolean contains(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.CONTAINS);

			request.addParameter(identity);

			return performBooleanResponseAction(request);
		}

		public IFrame get(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.GET);

			request.addParameter(identity);

			return performAssertionOrNullResponseAction(request);
		}

		public List<CIdentity> getAllIdentities() {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.GET_IDS);

			return performIdentitiesResponseAction(request);
		}

		public IMatches match(IFrame query) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.MATCH);

			request.addParameter(query);

			return performMatchesResponseAction(request);
		}

		public boolean matches(IFrame query, IFrame instance) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.MATCHES);

			request.addParameter(query);
			request.addParameter(instance);

			return performBooleanResponseAction(request);
		}
	}

	/**
	 * Provides the client MEKON instance store.
	 *
	 * @return Client MEKON instance store
	 */
	public IStore getIStore() {

		return store;
	}

	/**
	 * Constructor.
	 *
	 * @param model Client-side model associated with the store
	 */
	protected XClientStore(CModel model) {

		this.model = model;

		assertionParser = new IInstanceParser(model, IFrameFunction.ASSERTION);
	}

	/**
	 * Accesses the server to perform a particular store-related action.
	 *
	 * @param requestDoc Document representing specification of required
	 * action
	 * @return Document representing output produced by action
	 */
	protected abstract XDocument performActionOnServer(XDocument request);

	private boolean performBooleanResponseAction(XRequestRenderer request) {

		return performAction(request).getBooleanResponse();
	}

	private IFrame performAssertionOrNullResponseAction(XRequestRenderer request) {

		XResponseParser response = performAction(request);

		if (response.isNullResponse()) {

			return null;
		}

		return assertionParser.parse(response.getInstanceResponseParseInput());
	}

	private List<CIdentity> performIdentitiesResponseAction(XRequestRenderer request) {

		return performAction(request).getIdentitiesResponse();
	}

	private IMatches performMatchesResponseAction(XRequestRenderer request) {

		return performAction(request).getMatchesResponse();
	}

	private XResponseParser performAction(XRequestRenderer request) {

		return new XResponseParser(performActionOnServer(request.getDocument()));
	}
}
