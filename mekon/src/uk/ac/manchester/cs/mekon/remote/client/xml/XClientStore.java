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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.store.motor.*;
import uk.ac.manchester.cs.mekon.remote.client.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon.remote.util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

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
public abstract class XClientStore extends XClientEntity {

	private XClientModel clientModel;
	private IStore store = new XClientIStore();

	private RClientInstanceParser responseParser;

	private class XClientIStore implements IStore {

		public IFrame add(IFrame instance, CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.ADD);

			request.addParameter(instance);
			request.addParameter(identity);

			return performInstanceOrNullResponseAction(request);
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

		public CModel getModel() {

			return clientModel.getCModel();
		}

		public IStoreRegenReport getRegenReport() {

			return IStoreInertRegenReport.SINGLETON;
		}

		public boolean contains(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.CONTAINS);

			request.addParameter(identity);

			return performBooleanResponseAction(request);
		}

		public IRegenType getType(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.GET_TYPE);

			request.addParameter(identity);

			CIdentity typeId = performIdentityOrNullResponseAction(request);

			if (typeId == null) {

				return null;
			}

			CFrame type = getModel().getFrames().getOrNull(typeId);

			return type != null ? new IRegenValidType(type) : new IRegenInvalidType(typeId);
		}

		public IRegenInstance get(CIdentity identity) {

			XRequestRenderer request = new XRequestRenderer(RStoreActionType.GET);

			request.addParameter(identity);

			IFrame instance = performInstanceOrNullResponseAction(request);

			if (instance == null) {

				return null;
			}

			clientModel.initialiseReloadedInstance(instance);

			return new IRegenValidInstance(instance);
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
	 * @param clientModel Client-side model associated with the store
	 * @param expireOnServerRestart true if client should become invalid
	 * if server is restarted whilst client is running
	 */
	protected XClientStore(XClientModel clientModel, boolean expireOnServerRestart) {

		super(expireOnServerRestart);

		this.clientModel = clientModel;

		responseParser = new RClientInstanceParser(clientModel.getCModel());
	}

	private boolean performBooleanResponseAction(XRequestRenderer request) {

		return performAction(request).getBooleanResponse();
	}

	private IFrame performInstanceOrNullResponseAction(XRequestRenderer request) {

		XResponseParser response = performAction(request);

		if (response.isNullResponse()) {

			return null;
		}

		return responseParser.parse(response.getInstanceResponseParseInput());
	}

	private CIdentity performIdentityOrNullResponseAction(XRequestRenderer request) {

		XResponseParser response = performAction(request);

		return response.isNullResponse() ? null : response.getIdentityResponse();
	}

	private List<CIdentity> performIdentitiesResponseAction(XRequestRenderer request) {

		return performAction(request).getIdentitiesResponse();
	}

	private IMatches performMatchesResponseAction(XRequestRenderer request) {

		return performAction(request).getMatchesResponse();
	}
}
