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
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Represents a server-side version of the MEKON frames model that uses
 * the standard MEKON XML-based serialisations to communicate with the
 * client.
 *
 * @author Colin Puleston
 */
public class XServerModel {

	private CModel cModel;

	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	private IInstanceParser assertionParser;
	private IInstanceParser queryParser;

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

		assertionParser = new IInstanceParser(cModel, IFrameFunction.ASSERTION);
		queryParser = new IInstanceParser(cModel, IFrameFunction.QUERY);

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
	 * Initialises the specified uninitialised instance-level frame with
	 * function {@link IFrameFunction#ASSERTION}.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised frame
	 * @return Updated version of document
	 */
	public XDocument initialiseAssertion(XDocument assertionDoc) {

		return initialiseInstance(assertionParser, assertionDoc);
	}

	/**
	 * Initialises the specified uninitialised instance-level frame with
	 * function {@link IFrameFunction#QUERY}.
	 *
	 * @param queryDoc Document containing standard MEKON XML-based
	 * serialisation of relevant uninitialised frame
	 * @return Updated version of document
	 */
	public XDocument initialiseQuery(XDocument queryDoc) {

		return initialiseInstance(queryParser, queryDoc);
	}

	/**
	 * Automatically updates the specified instance-level frame/slot network.
	 * with function {@link IFrameFunction#ASSERTION}.
	 *
	 * @param assertionDoc Document containing standard MEKON XML-based
	 * serialisation of relevant frame/slot network
	 * @return Updated version of document
	 */
	public XDocument updateAssertion(XDocument assertionDoc) {

		return updateInstance(assertionParser, assertionDoc);
	}

	/**
	 * Automatically updates the specified instance-level frame/slot network
	 * with function {@link IFrameFunction#QUERY}.
	 *
	 * @param queryDoc Document containing standard MEKON XML-based
	 * serialisation of relevant frame/slot network
	 * @return Updated version of document
	 */
	public XDocument updateQuery(XDocument queryDoc) {

		return updateInstance(queryParser, queryDoc);
	}

	private XDocument initialiseInstance(IInstanceParser parser, XDocument doc) {

		IInstanceParseInput parseInput = new IInstanceParseInput(doc);
		IFrame rootFrame = parser.parseRootFrameType(parseInput).instantiate();

		return instanceRenderer.render(new IInstanceRenderInput(rootFrame));
	}

	private XDocument updateInstance(IInstanceParser parser, XDocument doc) {

		IInstanceParseInput parseInput = new IInstanceParseInput(doc);
		IFrame rootFrame = parser.parse(parseInput);
		IInstanceRenderInput renderInput = new IInstanceRenderInput(rootFrame);

		renderInput.setFrameXDocIds(parseInput.getFrameXDocIds());

		return instanceRenderer.render(renderInput);
	}
}
