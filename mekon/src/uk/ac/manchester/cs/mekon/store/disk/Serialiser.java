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

package uk.ac.manchester.cs.mekon.store.disk;

import java.io.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.motor.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon.config.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
class Serialiser {

	static private final String PROFILE_ROOT_ID = "Instance";
	static private final String PROFILE_TYPE_ID = "Type";

	private CModel model;
	private IInstanceRenderer instanceRenderer = new IInstanceRenderer();

	Serialiser(CModel model) {

		this.model = model;
	}

	void renderProfile(InstanceProfile profile, File file) {

		XDocument document = new XDocument(PROFILE_ROOT_ID);

		XNode rootNode = document.getRootNode();
		XNode typeNode = rootNode.addChild(PROFILE_TYPE_ID);

		renderIdentity(profile.getIdentity(), rootNode);
		renderIdentity(profile.getType(), typeNode);

		document.writeToFile(file);
	}

	void renderInstance(IFrame instance, File file) {

		instanceRenderer.render(new IInstanceRenderInput(instance)).writeToFile(file);
	}

	InstanceProfile parseProfile(File file) {

		XNode rootNode = new XDocument(file).getRootNode();
		XNode typeNode = rootNode.getChild(PROFILE_TYPE_ID);

		return new InstanceProfile(parseIdentity(rootNode), parseType(typeNode));
	}

	IFrame parseInstance(CIdentity identity, File file, boolean freeInstance) {

		IInstanceParser parser = new IInstanceParser(model, IFrameFunction.ASSERTION);

		parser.setFreeInstances(freeInstance);
		parser.setPossibleModelUpdates(true);

		IInstanceParseInput input = new IInstanceParseInput(new XDocument(file));
		IInstanceParseOutput output = parser.parse(input);

		checkValidInstance(identity, output);

		return output.getRootFrame();
	}

	private void checkValidInstance(CIdentity identity, IInstanceParseOutput output) {

		switch (output.getStatus()) {

			case FULLY_INVALID:
				reportInvalidInstance(identity, output);
				break;

			case PARTIALLY_VALID:
				reportPrunedInstance(identity, output);
				break;
		}
	}

	private void reportInvalidInstance(CIdentity identity, IInstanceParseOutput output) {

		reportWarning(
			"Cannot re-load instance: " + identity
			+ ": Invalid root-frame type: " + output.getRootTypeId());
	}

	private void reportPrunedInstance(CIdentity identity, IInstanceParseOutput output) {

		reportWarning(
			"Removed invalid components from re-loaded instance: " + identity
			+ "...");

		for (IPath path : output.getAllPrunedPaths()) {

			reportLine(path.toString());
		}
	}

	private void renderIdentity(CIdentity identity, XNode node) {

		CIdentitySerialiser.render(identity, node);
	}

	private void renderIdentity(CIdentified identified, XNode node) {

		CIdentitySerialiser.render(identified, node);
	}

	private CFrame parseType(XNode typeNode) {

		return model.getFrames().get(parseIdentity(typeNode));
	}

	private CIdentity parseIdentity(XNode node) {

		return CIdentitySerialiser.parse(node);
	}

	private void reportWarning(String message) {

		reportLine("INSTANCE STORE WARNING: " + message);
	}

	private void reportLine(String message) {

		System.out.println(message);
	}
}