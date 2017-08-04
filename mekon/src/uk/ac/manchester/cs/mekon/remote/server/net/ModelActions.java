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

import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.remote.server.xml.*;
import uk.ac.manchester.cs.mekon.remote.util.*;

/**
 * @author Colin Puleston
 */
class ModelActions extends ServerActions {

	private XServerModel model;

	private abstract class ModelAction extends Action<RModelActionType> {
	}

	private class GetFrameHierarchyAction extends ModelAction {

		RModelActionType getType() {

			return RModelActionType.GET_FRAME_HIERARCHY;
		}

		void perform(NetLink link) throws ServletException, IOException {

			link.writeDocument(model.getCFrameHierarchy());
		}
	}

	private class InitialiseAssertionAction extends ModelAction {

		RModelActionType getType() {

			return RModelActionType.INITIALISE_ASSERTION;
		}

		void perform(NetLink link) throws ServletException, IOException {

			link.writeDocument(model.initialiseAssertion(link.readDocument()));
		}
	}

	private class InitialiseQueryAction extends ModelAction {

		RModelActionType getType() {

			return RModelActionType.INITIALISE_QUERY;
		}

		void perform(NetLink link) throws ServletException, IOException {

			link.writeDocument(model.updateQuery(link.readDocument()));
		}
	}

	private class UpdateAssertionAction extends ModelAction {

		RModelActionType getType() {

			return RModelActionType.UPDATE_ASSERTION;
		}

		void perform(NetLink link) throws ServletException, IOException {

			link.writeDocument(model.updateAssertion(link.readDocument()));
		}
	}

	private class UpdateQueryAction extends ModelAction {

		RModelActionType getType() {

			return RModelActionType.UPDATE_QUERY;
		}

		void perform(NetLink link) throws ServletException, IOException {

			link.writeDocument(model.initialiseQuery(link.readDocument()));
		}
	}

	ModelActions(CBuilder cBuilder) {

		model = new XServerModel(cBuilder);

		new GetFrameHierarchyAction();
		new InitialiseAssertionAction();
		new InitialiseQueryAction();
		new UpdateAssertionAction();
		new UpdateQueryAction();
	}

	RActionCategory getCategory() {

		return RActionCategory.MODEL;
	}
}
