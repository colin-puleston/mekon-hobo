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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * Parser for the standard XML serialisation of section of
 * {@link CFrame}-hierarchy. Provides the parsed hierarchy in the form
 * of a {@link CHierarchy} object.
 *
 * @author Colin Puleston
 */
public class CHierarchyParser extends CSerialiser {

	private class OneTimeParser {

		private CHierarchyBuilder hierarchyBldr;

		CHierarchy parse(XNode rootFrameNode) {

			CIdentity rootFrameId = parseIdentity(rootFrameNode);

			hierarchyBldr = new CHierarchyBuilder(rootFrameId);

			parseFrom(rootFrameId, rootFrameNode);

			return hierarchyBldr.getHierarchy();
		}

		private void parseFrom(CIdentity frameId, XNode frameNode) {

			for (XNode subFrameNode : frameNode.getChildren(CFRAME_ID)) {

				CIdentity subFrameId = parseIdentity(subFrameNode);

				if (hierarchyBldr.addSub(frameId, subFrameId)) {

					parseAnnotations(subFrameId, subFrameNode);
					parseFrom(subFrameId, subFrameNode);
				}
			}
		}

		private void parseAnnotations(CIdentity frameId, XNode frameNode) {

			for (XNode annoNode : frameNode.getChildren(ANNOTATION_ID)) {

				String key = annoNode.getString(ANNOTATION_KEY_ATTR);
				String value = annoNode.getString(ANNOTATION_VALUE_ATTR);

				hierarchyBldr.addAnnotation(frameId, key, value);
			}
		}
	}

	/**
	 * Parses serialised hierarchy from specified document, whose
	 * top-level element contains the representation of the root-frame.
	 *
	 * @param document Document containing serialised hierarchy
	 * @return Generated hierarchy
	 */
	public CHierarchy parse(XDocument document) {

		return parse(document.getRootNode());
	}

	/**
	 * Parses serialised hierarchy from node representing the root-frame.
	 *
	 * @param parentNode Parent-node for parsing
	 * @return Generated hierarchy
	 */
	public CHierarchy parse(XNode parentNode) {

		return new OneTimeParser().parse(parentNode.getChild(CFRAME_ID));
	}

	private CIdentity parseIdentity(XNode node) {

		return CIdentitySerialiser.parse(node);
	}
}
