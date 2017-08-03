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
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class StoreActions extends ServerActions {

	private XServerStore store;

	private abstract class StoreAction extends Action<RStoreActionType> {
	}

	private class AddAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.ADD;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.checkReturnDocument(store.add(io.acceptDocument(), io.acceptDocument()));
		}
	}

	private class RemoveAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.REMOVE;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.returnBoolean(store.remove(io.acceptDocument()));
		}
	}

	private class ClearsAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.CLEAR;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			store.clear();
		}
	}

	private class ContainsAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.CONTAINS;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.returnBoolean(store.contains(io.acceptDocument()));
		}
	}

	private class GetAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.GET;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.checkReturnDocument(store.get(io.acceptDocument()));
		}
	}

	private class GetIdsAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.GET_IDS;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.returnDocument(store.getAllIdentities());
		}
	}

	private class MatchAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.MATCH;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.returnDocument(store.match(io.acceptDocument()));
		}
	}

	private class MatchesAction extends StoreAction {

		RStoreActionType getType() {

			return RStoreActionType.MATCHES;
		}

		void perform(ServerIO io) throws ServletException, IOException {

			io.returnBoolean(store.matches(io.acceptDocument(), io.acceptDocument()));
		}
	}

	StoreActions(CBuilder cBuilder) {

		store = new XServerStore(createIStore(cBuilder));

		new AddAction();
		new RemoveAction();
		new ClearsAction();
		new ContainsAction();
		new GetAction();
		new GetIdsAction();
		new MatchAction();
		new MatchesAction();
	}

	RActionCategory getCategory() {

		return RActionCategory.STORE;
	}

	private IStore createIStore(CBuilder cBuilder) {

		return IDiskStoreManager.getBuilder(cBuilder).build();
	}
}
