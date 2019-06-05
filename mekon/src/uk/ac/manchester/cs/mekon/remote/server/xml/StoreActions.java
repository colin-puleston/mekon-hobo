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
import uk.ac.manchester.cs.mekon.model.regen.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.server.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class StoreActions extends ServerActions<RStoreActionType> {

	private IStore store;
	private RServerInstanceParser parameterParser;

	private class AddAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.ADD;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			IFrame adding = getAssertionParameter(request, 0);
			CIdentity id = request.getIdentityParameter(1);

			response.setInstanceOrNullResponse(store.add(adding, id));
		}
	}

	private class RemoveAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.REMOVE;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			CIdentity id = request.getIdentityParameter(0);

			response.setBooleanResponse(store.remove(id));
		}
	}

	private class ClearsAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.CLEAR;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			response.setBooleanResponse(store.clear());
		}
	}

	private class ContainsAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.CONTAINS;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			CIdentity id = request.getIdentityParameter(0);

			response.setBooleanResponse(store.contains(id));
		}
	}

	private class GetTypeAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.GET_TYPE;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			CIdentity id = request.getIdentityParameter(0);
			IRegenType regen = store.getType(id);
			CIdentity typeId = regen != null ? regen.getRootTypeId() : null;

			response.setIdentityOrNullResponse(typeId);
		}
	}

	private class GetAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.GET;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			CIdentity id = request.getIdentityParameter(0);
			IRegenInstance regen = store.get(id);
			IFrame frame = regen != null ? regen.getRootFrame() : null;

			response.setInstanceOrNullResponse(frame);
		}
	}

	private class GetIdsAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.GET_IDS;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			response.setIdentitiesResponse(store.getAllIdentities());
		}
	}

	private class MatchAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.MATCH;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			IFrame query = getQueryParameter(request, 0);

			response.setMatchesResponse(store.match(query));
		}
	}

	private class MatchesAction extends Action {

		RStoreActionType getActionType() {

			return RStoreActionType.MATCHES;
		}

		void perform(XRequestParser request, XResponseRenderer response) {

			IFrame query = getQueryParameter(request, 0);
			IFrame assertion = getAssertionParameter(request, 1);

			response.setBooleanResponse(store.matches(query, assertion));
		}
	}

	StoreActions(IStore store) {

		this.store = store;

		parameterParser = new RServerInstanceParser(store.getModel());

		new AddAction();
		new RemoveAction();
		new ClearsAction();
		new ContainsAction();
		new GetAction();
		new GetIdsAction();
		new MatchAction();
		new MatchesAction();
	}

	RActionCategory getActionCategory() {

		return RActionCategory.STORE;
	}

	RStoreActionType getRequestActionType(XRequestParser request) {

		return request.getStoreActionType();
	}

	private IFrame getAssertionParameter(XRequestParser request, int index) {

		return getParameter(request, index, false);
	}

	private IFrame getQueryParameter(XRequestParser request, int index) {

		return getParameter(request, index, true);
	}

	private IFrame getParameter(XRequestParser request, int index, boolean query) {

		IInstanceParseInput input = request.getInstanceParameterParseInput(index);

		return parameterParser.parse(input, query);
	}
}
