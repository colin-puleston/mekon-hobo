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

package uk.ac.manchester.cs.mekon.store;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
class InstanceSerialiser extends CIdentitySerialiser {

	static private final String ROOT_ID = "Instance";
	static private final String TYPE_ID = "Type";

	private CModel model;

	InstanceSerialiser(CModel model) {

		this.model = model;
	}

	void renderProfile(InstanceProfile profile, File file) {

		XDocument document = new XDocument(ROOT_ID);

		XNode rootNode = document.getRootNode();
		XNode typeNode = rootNode.addChild(TYPE_ID);

		renderIdentity(profile.getIdentity(), rootNode);
		renderIdentity(profile.getType(), typeNode);

		document.writeToFile(file);
	}

	void renderInstance(IFrame instance, File file) {

		XDocument document = new XDocument(ROOT_ID);

		renderInstance(instance, document.getRootNode());
		document.writeToFile(file);
	}

	InstanceProfile parseProfile(File file) {

		XNode rootNode = getRootNode(file);
		XNode typeNode = rootNode.getChild(TYPE_ID);


		return new InstanceProfile(
					parseIdentity(rootNode),
					parseType(typeNode));
	}

	IFrame parseInstance(File file, boolean freeInstance) {

		return createFrameParser(freeInstance).parse(getRootNode(file));
	}

	private void renderInstance(IFrame instance, XNode rootNode) {

		IFrameRenderer frameRenderer = new IFrameRenderer();

		frameRenderer.setSchemaLevel(ISchemaLevel.BASIC);
		frameRenderer.render(instance, rootNode);
	}

	private CFrame parseType(XNode typeNode) {

		return model.getFrames().get(parseIdentity(typeNode));
	}

	private IFrameParserAbstract createFrameParser(boolean freeInstance) {

		return freeInstance ?
				new IFrameFreeParser(model, IFrameFunction.ASSERTION) :
				new IFrameParser(model, IFrameFunction.ASSERTION);
	}

	private XNode getRootNode(File file) {

		return new XDocument(file).getRootNode();
	}
}