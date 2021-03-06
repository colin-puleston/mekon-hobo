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

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.network.*;
import uk.ac.manchester.cs.mekon.store.*;
import uk.ac.manchester.cs.mekon_util.*;
import uk.ac.manchester.cs.mekon_util.xdoc.*;

/**
 * @author Colin Puleston
 */
class InstanceRenderer extends Renderer {

	private NNode rootNode;

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

		abstract void renderValue(F feature, V value, XNode xNode);

		private void render(F feature) {

			XNode xNode = xParent.addChild(getEntityId());

			renderType(feature, xNode);

			for (V value : feature.getValues()) {

				renderValue(feature, value, xNode);
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

		void renderValue(NLink feature, NNode value, XNode xNode) {

			renderValueNode(feature, value, xNode.addChild(NODE_ID));
		}
	}

	private class NumbersRenderer
					extends
						FeaturesRenderer<INumber, NNumber> {

		NumbersRenderer(XNode xParent) {

			super(xParent);
		}

		String getEntityId() {

			return NUMBER_ID;
		}

		void renderValue(NNumber feature, INumber value, XNode xNode) {

			renderNumber(value, xNode);
		}
	}

	private class StringsRenderer
					extends
						FeaturesRenderer<String, NString> {

		StringsRenderer(XNode xParent) {

			super(xParent);
		}

		String getEntityId() {

			return STRING_ID;
		}

		void renderValue(NString feature, String value, XNode xNode) {

			renderString(value, xNode);
		}
	}

	InstanceRenderer(NNode rootNode) {

		checkNonCyclic(rootNode);

		this.rootNode = rootNode;
	}

	XDocument render(int index) {

		XDocument document = new XDocument(ROOT_ID);
		XNode xRoot = document.getRootNode();

		xRoot.setValue(INDEX_ATTR, index);
		renderNode(rootNode, xRoot.addChild(NODE_ID));

		return document;
	}

	private void renderValueNode(NLink link, NNode node, XNode xNode) {

		if (node.instanceRef()) {

			renderType(node, xNode);
			renderType(node.getInstanceRef(), xNode);
		}
		else {

			renderNode(node, xNode);
		}
	}

	private void renderNode(NNode node, XNode xNode) {

		checkAtomicType(node);

		renderType(node, xNode);
		renderNodeAncestorTypes(node, xNode);
		renderNodeFeatures(node, xNode);
	}

	private void renderNodeAncestorTypes(NNode node, XNode xNode) {

		CFrame cFrame = node.getCFrame();

		if (cFrame != null) {

			for (CFrame cAncestor : cFrame.getAncestors()) {

				if (!cAncestor.isRoot()) {

					renderType(cAncestor, xNode);
				}
			}
		}
	}

	private void renderNodeFeatures(NNode node, XNode xNode) {

		new LinksRenderer(xNode).renderAll(node.getLinks());
		new NumbersRenderer(xNode).renderAll(node.getNumbers());
		new StringsRenderer(xNode).renderAll(node.getStrings());
	}

	private void renderType(CFrame frame, XNode xNode) {

		renderType(frame.getIdentity(), xNode);
	}

	private void renderType(NEntity entity, XNode xNode) {

		renderType(entity.getType(), xNode);
	}

	private void renderType(CIdentity type, XNode xNode) {

		renderId(type, xNode.addChild(TYPE_ID));
	}

	private void renderId(CIdentity identity, XNode xNode) {

		xNode.setValue(ID_ATTR, renderId(identity));
	}

	private void renderNumber(INumber value, XNode xNode) {

		checkDefiniteNumberValue(value);

		renderString(value.asTypeNumber().toString(), xNode);
	}

	private void renderString(String value, XNode xNode) {

		xNode.setValue(VALUE_ATTR, value);
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
