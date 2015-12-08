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

package uk.ac.manchester.cs.mekon.basex;

import java.util.*;

import uk.ac.manchester.cs.mekon.*;
import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.xdoc.*;

/**
 * @author Colin Puleston
 */
class InstanceRenderer extends Renderer {

	private abstract class AttributeRenderer<V, A extends NAttribute<V>> {

		private XNode xParent;

		AttributeRenderer(XNode xParent) {

			this.xParent = xParent;
		}

		void renderAll(List<A> attributes) {

			for (A attribute : attributes) {

				if (attribute.hasValues()) {

					checkValid(attribute);
					render(attribute);
				}
			}
		}

		abstract String getEntityId();

		void checkValid(A attribute) {
		}

		abstract void renderValue(V value, XNode xNode);

		private void render(A attribute) {

			XNode xNode = xParent.addChild(getEntityId());

			renderType(attribute.getProperty(), xNode);

			for (V value : attribute.getValues()) {

				renderValue(value, xNode);
			}
		}
	}

	private class LinkRenderer
					extends
						AttributeRenderer<NNode, NLink> {

		LinkRenderer(XNode xParent) {

			super(xParent);
		}

		void checkValid(NLink attribute) {

			checkConjunctionLink(attribute);
		}

		String getEntityId() {

			return LINK_ID;
		}

		void renderValue(NNode value, XNode xNode) {

			renderNode(value, xNode.addChild(NODE_ID));
		}
	}

	private class NumericRenderer
					extends
						AttributeRenderer<INumber, NNumeric> {

		NumericRenderer(XNode xParent) {

			super(xParent);
		}

		String getEntityId() {

			return NUMERIC_ID;
		}

		void renderValue(INumber value, XNode xNode) {

			xNode.addValue(VALUE_ATTR, value.asTypeNumber().toString());
		}
	}

	XDocument render(NNode rootNode, CIdentity identity) {

		checkNonCyclic(rootNode);

		XDocument document = new XDocument(ROOT_ID);
		XNode xRoot = document.getRootNode();

		renderId(identity, xRoot);
		renderNode(rootNode, xRoot.addChild(NODE_ID));

		return document;
	}

	private void renderNode(NNode node, XNode xNode) {

		checkAtomicConcept(node);

		renderNodeTypes(node, xNode);

		new LinkRenderer(xNode).renderAll(node.getLinks());
		new NumericRenderer(xNode).renderAll(node.getNumerics());
	}

	private void renderNodeTypes(NNode node, XNode xNode) {

		renderType(node.getAtomicConcept(), xNode);
		renderNodeAncestorTypes(node, xNode);
	}

	private void renderNodeAncestorTypes(NNode node, XNode xNode) {

		CFrame cFrame = node.getCFrame();

		if (cFrame != null) {

			for (CFrame cAncestor : cFrame.getAncestors()) {

				if (!cAncestor.isRoot()) {

					renderType(cAncestor.getIdentity(), xNode);
				}
			}
		}
	}

	private void renderType(CIdentity type, XNode xNode) {

		renderId(type, xNode.addChild(TYPE_ID));
	}

	private void renderId(CIdentity identity, XNode xNode) {

		xNode.addValue(ID_ATTR, identity.getIdentifier());
	}

	private void checkNonCyclic(NNode rootNode) {

		if (rootNode.leadsToCycle()) {

			throw new KAccessException(
						"Cannot render cyclic instance as XML tree: "
						+ "Top-level node: " + rootNode);
		}
	}

	private void checkAtomicConcept(NNode node) {

		if (!node.atomicConcept()) {

			throw new KAccessException(
						"Cannot render node with disjunction-concept: "
						+ node);
		}
	}

	private void checkConjunctionLink(NLink link) {

		if (link.disjunctionLink()) {

			throw new KAccessException(
						"Cannot render disjunction-link: "
						+ link);
		}
	}

	private void checkDefiniteNumberValue(INumber value) {

		if (value.indefinite()) {

			throw new KAccessException(
						"Cannot render indefinite number-value: "
						+ value);
		}
	}
}
