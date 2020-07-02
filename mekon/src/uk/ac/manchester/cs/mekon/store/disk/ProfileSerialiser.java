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
import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.serial.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class ProfileSerialiser {

	static private final String ROOT_ID = "Instance";
	static private final String TYPE_ID = "Type";
	static private final String REFERENCES_ID = "ReferencedInstances";

	static void render(InstanceProfile profile, File file) {

		XDocument document = new XDocument(ROOT_ID);

		XNode rootNode = document.getRootNode();
		XNode typeNode = rootNode.addChild(TYPE_ID);

		renderIdentity(profile.getInstanceId(), rootNode);
		renderIdentity(profile.getTypeId(), typeNode);

		List<CIdentity> refIds = profile.getReferenceIds();

		if (!refIds.isEmpty()) {

			renderIdentities(refIds, rootNode.addChild(REFERENCES_ID));
		}

		document.writeToFile(file);
	}

	static InstanceProfile parse(File file) {

		XNode rootNode = new XDocument(file).getRootNode();
		XNode typeNode = rootNode.getChild(TYPE_ID);

		return new InstanceProfile(
						parseIdentity(rootNode),
						parseIdentity(typeNode),
						parseReferenceIds(rootNode));
	}

	static private List<CIdentity> parseReferenceIds(XNode rootNode) {

		XNode refsNode = rootNode.getChildOrNull(REFERENCES_ID);

		return refsNode != null ?  parseIdentities(refsNode) : Collections.emptyList();
	}

	static private void renderIdentity(CIdentity identity, XNode node) {

		CIdentitySerialiser.render(identity, node);
	}

	static private void renderIdentities(List<CIdentity> identities, XNode node) {

		CIdentitySerialiser.renderList(identities, node);
	}

	static private CIdentity parseIdentity(XNode node) {

		return CIdentitySerialiser.parse(node);
	}

	static private List<CIdentity> parseIdentities(XNode node) {

		return CIdentitySerialiser.parseList(node);
	}
}