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

	private abstract class FeaturesRenderer<V, F extends NFeature<V>> {

		private XNode xParent;

		FeaturesRenderer(XNode xParent) {

			this.xParent = xParent;
		}

		void renderAll(List<F> features) {

			for (F feature : features) {

				if (feature.hasValues()) {

					checkValid(feature);
					render(feature);
				}
			}
		}

		abstract String getEntityId();

		void checkValid(F feature) {
		}

		abstract void renderValue(V value, XNode xNode);

		private void render(F feature) {

			XNode xNode = xParent.addChild(getEntityId());

			renderType(feature.getType(), xNode);

			for (V value : feature.getValues()) {

				renderValue(value, xNode);
			}
		}
	}

	private class LinksRenderer
					extends
						FeaturesRenderer<NNode, NLink> {

		LinksRenderer(XNode xParent) {

			super(xParent);
		}

		void checkValid(NLink feature) {

			checkConjunctionLink(feature);
		}

		String getEntityId() {

			return LINK_ID;
		}

		void renderValue(NNode value, XNode xNode) {

			renderNode(value, xNode.addChild(NODE_ID));
		}
	}

	private class NumbersRenderer
					extends
						FeaturesRenderer<INumber, NNumber> {

		NumbersRenderer(XNode xParent) {

			super(xParent);
		}

		String getEntityId() {

			return NUMERIC_ID;
		}

		void renderValue(INumber value, XNode xNode) {

			xNode.addValue(VALUE_ATTR, value.asTypeNumber().toString());
		}
	}

	XDocument render(NNode rootNode, int index) {

		checkNonCyclic(rootNode);

		XDocument document = new XDocument(ROOT_ID);
		XNode xRoot = document.getRootNode();

		xRoot.addValue(INDEX_ATTR, index);
		renderNode(rootNode, xRoot.addChild(NODE_ID));

		return document;
	}

	private void renderNode(NNode node, XNode xNode) {

		checkAtomicType(node);

		renderNodeTypes(node, xNode);

		new LinksRenderer(xNode).renderAll(node.getLinks());
		new NumbersRenderer(xNode).renderAll(node.getNumbers());
	}

	private void renderNodeTypes(NNode node, XNode xNode) {

		renderType(node.getType(), xNode);
		renderNodeAncestorTypes(node, xNode);

		if (node.instanceReference()) {

			renderType(node.getInstanceRef(), xNode);
		}
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

		xNode.addValue(ID_ATTR, renderId(identity));
	}

	private void checkAtomicType(NNode node) {

		if (!node.atomicType()) {

			throw new KAccessException(
						"Cannot render node with disjunction-type: "
						+ node);
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
