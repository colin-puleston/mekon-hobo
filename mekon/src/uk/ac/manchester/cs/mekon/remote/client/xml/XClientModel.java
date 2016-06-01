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
import uk.ac.manchester.cs.mekon.xdoc.*;
import uk.ac.manchester.cs.mekon.remote.client.*;

/**
 * Represents a client-side version of the MEKON frames model that uses
 * the standard MEKON XML-based serialisations to communicate with the
 * server.
 *
 * @author Colin Puleston
 */
public abstract class XClientModel extends RClientModel {

	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	private IInstanceParser assertionParser;
	private IInstanceParser queryParser;

	private abstract class InstanceAction {

		private Map<IFrame, String> mastersToIds = new HashMap<IFrame, String>();
		private Map<String, IFrame> idsToUpdates = new HashMap<String, IFrame>();

		RUpdates perform(IFrame masterRoot) {

			boolean query = masterRoot.getFunction().query();

			XDocument masterDoc = instanceRenderer.render(createRenderInput(masterRoot));
			XDocument updateDoc = processOnServer(masterDoc, query);

			IFrame updateRoot = getParser(query).parse(createParseInput(updateDoc));

			return createUpdates(updateRoot);
		}

		IInstanceRenderInput createRenderInput(IFrame masterRoot) {

			IInstanceRenderInput input = new IInstanceRenderInput(masterRoot);

			input.setFrameXDocIds(mastersToIds);

			return input;
		}

		abstract XDocument processAssertionOnServer(XDocument assertionDoc);

		abstract XDocument processQueryOnServer(XDocument queryDoc);

		private XDocument processOnServer(XDocument doc, boolean query) {

			return query ? processQueryOnServer(doc) : processAssertionOnServer(doc);
		}

		private IInstanceParser getParser(boolean query) {

			return query ? assertionParser : queryParser;
		}

		private IInstanceParseInput createParseInput(XDocument updateDoc) {

			IInstanceParseInput input = new IInstanceParseInput(updateDoc);

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

	private class InstanceInitAction extends InstanceAction {

		XDocument processAssertionOnServer(XDocument assertionDoc) {

			return initialiseAssertionOnServer(assertionDoc);
		}

		XDocument processQueryOnServer(XDocument queryDoc) {

			return initialiseQueryOnServer(queryDoc);
		}
	}

	private class InstanceUpdateAction extends InstanceAction {

		private IValuesUpdate clientUpdate;

		InstanceUpdateAction(IValuesUpdate clientUpdate) {

			this.clientUpdate = clientUpdate;
		}

		IInstanceRenderInput createRenderInput(IFrame masterRoot) {

			IInstanceRenderInput input = super.createRenderInput(masterRoot);

			input.setValuesUpdate(clientUpdate);

			return input;
		}

		XDocument processAssertionOnServer(XDocument assertionDoc) {

			return updateAssertionOnServer(assertionDoc);
		}

		XDocument processQueryOnServer(XDocument queryDoc) {

			return updateQueryOnServer(queryDoc);
		}
	}

	/**
	 * Constructor.
	 *
	 * @param frameHierarchyDoc Document containing standard MEKON XML-based
	 * serialisation of concept-level frames hierarchy present on server
	 */
	public XClientModel(XDocument frameHierarchyDoc) {

		this(new CFrameHierarchyParser().parse(frameHierarchyDoc));
	}

	/**
	 * Constructor.
	 *
	 * @param frameHierarchyRootNode Root-node of standard MEKON XML-based
	 * serialisation of concept-level frames hierarchy present on server
	 */
	public XClientModel(XNode frameHierarchyRootNode) {

		this(new CFrameHierarchyParser().parse(frameHierarchyRootNode));
	}

	/**
	 * {@inheritDoc}
	 */
	protected RUpdates initialiseOnServer(IFrame frame) {

		return new InstanceInitAction().perform(frame);
	}

	/**
	 * {@inheritDoc}
	 */
	protected RUpdates updateOnServer(
							IFrame rootFrame,
							IValuesUpdate clientUpdate) {

		return new InstanceUpdateAction(clientUpdate).perform(rootFrame);
	}

	/**
	 * Sends an uninitialised instance-level frame with function {@link
	 * IFrameFunction#ASSERTION} to be initialised on the server.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised assertion frame
	 * @return Updated version of document
	 */
	protected abstract XDocument initialiseAssertionOnServer(XDocument assertionDoc);

	/**
	 * Sends an uninitialised instance-level frame with function {@link
	 * IFrameFunction#QUERY} to be initialised on the server.
	 *
	 * @param queryDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised query frame
	 * @return Updated version of document
	 */
	protected abstract XDocument initialiseQueryOnServer(XDocument queryDoc);

	/**
	 * Sends an instance-level frame/slot network with function {@link
	 * IFrameFunction#ASSERTION} to be automatically updated on the server.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant assertion frame/slot network
	 * @return Updated version of document
	 */
	protected abstract XDocument updateAssertionOnServer(XDocument assertionDoc);

	/**
	 * Sends an instance-level frame/slot network with function {@link
	 * IFrameFunction#QUERY} to be automatically updated on the server.
	 *
	 * @param queryDoc Document containing standard MEKON XML-based
	 * serialisation of relevant query frame/slot network
	 * @return Updated version of document
	 */
	protected abstract XDocument updateQueryOnServer(XDocument queryDoc);

	private XClientModel(CFrameHierarchy hierarchy) {

		super(hierarchy);

		assertionParser = new IInstanceParser(getModel(), IFrameFunction.ASSERTION);
		queryParser = new IInstanceParser(getModel(), IFrameFunction.QUERY);
	}
}
