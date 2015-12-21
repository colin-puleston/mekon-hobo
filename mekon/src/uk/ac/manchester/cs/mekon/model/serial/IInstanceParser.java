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

package uk.ac.manchester.cs.mekon.model.serial;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * Parser for the standard XML serialisation of an instance,
 * comprising an instance-identifier, and a frame representation
 * of the instance.
 *
 * @author Colin Puleston
 */
public class IInstanceParser extends ISerialiser {

	private CModel model;

	private XNode rootNode;
	private boolean freeInstance = false;

	/**
	 * Constructor that performs the parse operation.
	 *
	 * @param model Relevant model
	 * @param instanceFile Serialisation file
	 */
	public IInstanceParser(CModel model, File instanceFile) {

		this.model = model;

		rootNode = new XDocument(instanceFile).getRootNode();
	}

	/**
	 * Sets whether the parsing of the instance-description should
	 * produce a "free-instance" (see {@link IFreeInstances}). By
	 * default this will not be the case.
	 *
	 * @param freeInstance True if free-instance should be produced
	 */
	public void setFreeInstance(boolean freeInstance) {

		this.freeInstance = freeInstance;
	}

	/**
	 * Provides the identity of the instance.
	 *
	 * @return Identity of instance
	 */
	public CIdentity parseIdentity() {

		return parseIdentity(rootNode);
	}

	/**
	 * Provides the frame representation of the instance.
	 *
	 * @return Frame representation of instance
	 */
	public IFrame parseInstance() {

		return createFrameParser().parse(rootNode);
	}

	private IFrameParserAbstract createFrameParser() {

		return freeInstance ?
				new IFrameFreeParser(model, IFrameFunction.ASSERTION) :
				new IFrameParser(model, IFrameFunction.ASSERTION);
	}
}