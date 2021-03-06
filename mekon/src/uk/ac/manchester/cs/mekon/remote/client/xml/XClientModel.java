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
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.remote.client.*;
import uk.ac.manchester.cs.mekon.remote.xml.*;
import uk.ac.manchester.cs.mekon.remote.util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Represents a client-side version of the MEKON frames model, with
 * all client/server interaction being via XML-based representations
 * of specific actions.
 * <p>
 * Both this class and {@link XClientStore} are designed to be used
 * in combination with the companion XML-based server class,
 * <code>XServer</code>. All encoding and decoding of the action
 * requests and responses is handled by this set of client/server
 * classes. Hence the extending and wrapper classes are only required
 * to pass on the relevant documents, and never need to interpret any
 * of the XML contained within.
 *
 * @author Colin Puleston
 */
public abstract class XClientModel extends XClientEntity {

	private RClientModel rClientModel;
	private RClientInstanceParser responseParser;

	private abstract class InstanceAction {

		private Map<IFrame, String> mastersToIds = new HashMap<IFrame, String>();
		private Map<String, IFrame> idsToUpdates = new HashMap<String, IFrame>();

		RUpdates perform(IFrame masterRoot) {

			XRequestRenderer request = getRequest(masterRoot);
			XResponseParser response = performAction(request);

			return createUpdates(parseInstance(response));
		}

		abstract RModelActionType getActionType();

		void customiseRenderInput(IInstanceRenderInput input) {
		}

		private XRequestRenderer getRequest(IFrame masterRoot) {

			XRequestRenderer request = new XRequestRenderer(getActionType());

			request.addParameter(createRenderInput(masterRoot));

			return request;
		}

		private IFrame parseInstance(XResponseParser response) {

			return responseParser.parse(createParseInput(response));
		}

		private IInstanceRenderInput createRenderInput(IFrame masterRoot) {

			IInstanceRenderInput input = new IInstanceRenderInput(masterRoot);

			input.setFrameXDocIds(mastersToIds);
			customiseRenderInput(input);

			return input;
		}

		private IInstanceParseInput createParseInput(XResponseParser response) {

			IInstanceParseInput input = response.getInstanceResponseParseInput();

			input.setFramesByXDocId(idsToUpdates);

			return input;
		}

		private RUpdates createUpdates(IFrame updatedRoot) {

			RUpdates updates = new RUpdates(updatedRoot);

			for (Map.Entry<IFrame, String> entry : mastersToIds.entrySet()) {

				IFrame update = idsToUpdates.get(entry.getValue());

				if (update != null) {

					updates.addMapping(entry.getKey(), update);
				}
			}

			return updates;
		}
	}

	private class AssertionInitAction extends InstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.INITIALISE_ASSERTION;
		}
	}

	private class QueryInitAction extends InstanceAction {

		RModelActionType getActionType() {

			return RModelActionType.INITIALISE_QUERY;
		}
	}

	private abstract class InstanceUpdateAction extends InstanceAction {

		private IValuesUpdate clientUpdate = null;

		void setClientUpdate(IValuesUpdate clientUpdate) {

			this.clientUpdate = clientUpdate;
		}

		void customiseRenderInput(IInstanceRenderInput input) {

			input.setValuesUpdate(clientUpdate);
		}
	}

	private class AssertionUpdateAction extends InstanceUpdateAction {

		RModelActionType getActionType() {

			return RModelActionType.UPDATE_ASSERTION;
		}
	}

	private class QueryUpdateAction extends InstanceUpdateAction {

		RModelActionType getActionType() {

			return RModelActionType.UPDATE_QUERY;
		}
	}

	private class XRClientModel extends RClientModel {

		protected RUpdates initialiseOnServer(IFrame frame) {

			return getInitAction(frame).perform(frame);
		}

		protected RUpdates updateOnServer(IFrame rootFrame) {

			return getUpdateAction(rootFrame).perform(rootFrame);
		}

		protected RUpdates updateOnServer(IFrame rootFrame, IValuesUpdate clientUpdate) {

			InstanceUpdateAction action = getUpdateAction(rootFrame);

			action.setClientUpdate(clientUpdate);

			return action.perform(rootFrame);
		}

		XRClientModel() {

			super(getHierarchy());
		}
	}

	/**
	 * Provides the client MEKON frames model.
	 *
	 * @return Client MEKON frames model
	 */
	public CModel getCModel() {

		return rClientModel.getCModel();
	}

	/**
	 * Constructor.
	 *
	 * @param expireOnServerRestart true if client should become invalid
	 * if server is restarted whilst client is running
	 */
	protected XClientModel(boolean expireOnServerRestart) {

		super(expireOnServerRestart);

		rClientModel = new XRClientModel();
		responseParser = new RClientInstanceParser(getCModel());
	}

	void initialiseReloadedInstance(IFrame rootFrame) {

		rClientModel.initialiseReloadedInstance(rootFrame);
	}

	private CHierarchy getHierarchy() {

		return performAction(getHierarchyRequest()).getHierarchyResponse();
	}

	private XRequestRenderer getHierarchyRequest() {

		return new XRequestRenderer(RModelActionType.GET_FRAME_HIERARCHY);
	}

	private InstanceAction getInitAction(IFrame frame) {

		return query(frame) ? new QueryInitAction() : new AssertionInitAction();
	}

	private InstanceUpdateAction getUpdateAction(IFrame frame) {

		return query(frame) ? new QueryUpdateAction() : new AssertionUpdateAction();
	}

	private boolean query(IFrame frame) {

		return frame.getFunction().query();
	}
}
