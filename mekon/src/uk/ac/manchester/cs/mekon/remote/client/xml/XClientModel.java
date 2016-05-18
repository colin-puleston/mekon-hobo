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
 * the standard MEKON XML-based serialisation to communicate with the
 * server.
 *
 * @author Colin Puleston
 */
public abstract class XClientModel extends RClientModel {

	private IFrameRenderer iFrameRenderer = new IFrameRenderer();
	private IFrameParser iFrameParser;

	private abstract class IFrameProcessor {

		private Map<IFrame, String> mastersToIds = new HashMap<IFrame, String>();
		private Map<String, IFrame> idsToUpdates = new HashMap<String, IFrame>();

		RUpdates process(IFrame masterRoot) {

			XDocument masterDoc = iFrameRenderer.render(createRenderInput(masterRoot));
			XDocument updateDoc = processDoc(masterDoc);

			IFrame updateRoot = iFrameParser.parse(updateDoc, idsToUpdates);

			return createUpdates(updateRoot);
		}

		abstract XDocument processDoc(XDocument masterDoc);

		private IFrameRenderInput createRenderInput(IFrame masterRoot) {

			IFrameRenderInput input = new IFrameRenderInput(masterRoot);

			input.setXDocIds(mastersToIds);

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

	private class IFrameInitialiser extends IFrameProcessor {

		XDocument processDoc(XDocument masterDoc) {

			return initialiseAssertionOnServer(masterDoc);
		}
	}

	private class IFrameUpdater extends IFrameProcessor {

		XDocument processDoc(XDocument masterDoc) {

			return updateAssertionOnServer(masterDoc);
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
	 * @param frameHierarchyDoc Root-node of standard MEKON XML-based
	 * serialisation of concept-level frames hierarchy present on server
	 */
	public XClientModel(XNode frameHierarchyRootNode) {

		this(new CFrameHierarchyParser().parse(frameHierarchyRootNode));
	}

	/**
	 * {@inheritDoc}
	 */
	protected RUpdates initialiseAssertionOnServer(IFrame frame) {

		return new IFrameInitialiser().process(frame);
	}

	/**
	 * {@inheritDoc}
	 */
	protected RUpdates updateAssertionOnServer(IFrame rootFrame) {

		return new IFrameUpdater().process(rootFrame);
	}

	/**
	 * Sends an uninitialised instance-level frame to be initialised on
	 * the server.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised frame
	 * @return Updated version of document
	 */
	protected abstract XDocument initialiseAssertionOnServer(XDocument assertionDoc);

	/**
	 * Sends an instance-level frame/slot network to be automatically
	 * updated on the server.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant frame/slot network
	 * @return Updated version of document
	 */
	protected abstract XDocument updateAssertionOnServer(XDocument assertionDoc);

	private XClientModel(CFrameHierarchy hierarchy) {

		super(hierarchy);

		iFrameParser = new IFrameParser(getCModel(), IFrameFunction.ASSERTION);
	}
}
