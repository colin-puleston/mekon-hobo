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
import uk.ac.manchester.cs.mekon.serial.*;

/**
 * Parses an XML document representing the serialised contents
 * of an instance-store, and populates the current instance-store
 * with the reconstitued set of instances.
 *
 * @author Colin Puleston
 */
public abstract class IStoreParser extends ISerialiser {

	private IFrameParser iFrameParser;

	/**
	 * Parses the serialised version of the instance-store to
	 * populate the current instance-store.
	 *
	 * @param storeFile Serialisation file
	 */
	public void parse(File storeFile) {

		XNode rootNode = new XDocument(storeFile).getRootNode();

		for (XNode instNode : rootNode.getChildren(INSTANCE_ID)) {

			addInstance(parseIFrame(instNode), parseIdentity(instNode));
		}
	}

	/**
	 * Constructor.
	 *
	 * @param model Model to which instance-store is attached
	 */
	protected IStoreParser(CModel model) {

		iFrameParser = new IFrameParser(model, IFrameCategory.ASSERTION);
	}

	/**
	 * Method whose implementation adds instances to the
	 * instance-store.
	 *
	 * @param instance Instance to be added to instance-store
	 * @param identity Identity with which instance is to be stored
	 */
	protected abstract void addInstance(IFrame instance, CIdentity identity);

	private IFrame parseIFrame(XNode instanceNode) {

		return iFrameParser.parse(instanceNode);
	}
}