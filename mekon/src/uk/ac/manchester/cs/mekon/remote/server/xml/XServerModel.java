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

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Represents a server-side version of the MEKON frames model that uses
 * the standard MEKON XML-based serialisation to communicate with the
 * client.
 *
 * @author Colin Puleston
 */
public class XServerModel {

	private CModel cModel;

	private IFrameRenderer iFrameRenderer = new IFrameRenderer();
	private IFrameParser iFrameParser;

	/**
	 * Constructor.
	 *
	 * @param cBuilder Builder to use in creating frames model
	 */
	public XServerModel(CBuilder cBuilder) {

		this(cBuilder.build(), cBuilder);
	}

	/**
	 * Constructor.
	 *
	 * @param cModel frames model
	 * @param cBuilder Builder that was used to create frames model
	 */
	public XServerModel(CModel cModel, CBuilder cBuilder) {

		this.cModel = cModel;

		iFrameParser = new IFrameParser(cModel, IFrameFunction.ASSERTION);

		iFrameRenderer.setSchemaRender(ISchemaRender.FULL);
		cBuilder.setAutoUpdate(true);
	}

	/**
	 * Provides the server-side version of the frames model.
	 *
	 * @return Server-side version of frames model
	 */
	public CModel getCModel() {

		return cModel;
	}

	/**
	 * Provides document containing standard MEKON XML-based serialisation
	 * of concept-level frames hierarchy present on server.
	 *
	 * @return relevant document
	 */
	public XDocument getCFrameHierarchy() {

		CFrameHierarchyRenderer renderer = new CFrameHierarchyRenderer();

		renderer.setVisibilityFilter(CVisibility.EXPOSED);

		return renderer.render(cModel.getRootFrame());
	}

	/**
	 * Initialises the specified uninitialised instance-level frame.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised frame
	 * @return Updated version of document
	 */
	public XDocument initialiseAssertion(XDocument assertionDoc) {

		IFrame rootFrame = iFrameParser.parse(assertionDoc);

		return iFrameRenderer.render(rootFrame.getType().instantiate());
	}

	/**
	 * Automatically updates the specified instance-level frame/slot network.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant frame/slot network
	 * @return Updated version of document
	 */
	public XDocument updateAssertion(XDocument assertionDoc) {

		Map<String, IFrame> idsToFrames = new HashMap<String, IFrame>();
		IFrame rootFrame = iFrameParser.parse(assertionDoc, idsToFrames);

		rootFrame.checkManualUpdate();

		return iFrameRenderer.render(rootFrame, mapFramesToIds(idsToFrames));
	}

	private Map<IFrame, String> mapFramesToIds(Map<String, IFrame> idsToFrames) {

		Map<IFrame, String> framesToIds = new HashMap<IFrame, String>();

		for (Map.Entry<String, IFrame> entry : idsToFrames.entrySet()) {

			framesToIds.put(entry.getValue(), entry.getKey());
		}

		return framesToIds;
	}
}
