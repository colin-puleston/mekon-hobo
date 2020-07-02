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

package uk.ac.manchester.cs.mekon.gui.explorer;

import java.util.*;

import uk.ac.manchester.cs.mekon_util.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class KListDerivedChildNodes<V> {

	private GNode parentNode;
	private Map<V, GNode> childNodes = new HashMap<V, GNode>();

	private KList<V> values;

	private class ModelValuesListener implements KValuesListener<V> {

		public void onAdded(V value) {

			if (childNodeRequiredFor(value)) {

				addChildNode(value);
				parentNode.expand();
			}
		}

		public void onRemoved(V value) {

			if (childNodeRequiredFor(value)) {

				childNodes.remove(value).remove();
			}
		}

		public void onCleared(List<V> values) {

			parentNode.clearChildren();
			childNodes.clear();
		}
	}

	KListDerivedChildNodes(GNode parentNode, KList<V> values) {

		this.parentNode = parentNode;
		this.values = values;
	}

	void addInitialChildNodes() {

		for (V value : values.asList()) {

			if (childNodeRequiredFor(value)) {

				addChildNode(value);
			}
		}

		values.addValuesListener(new ModelValuesListener());
	}

	boolean childNodeRequiredFor(V value) {

		return true;
	}

	abstract GNode createChildNode(V value);

	private GNode addChildNode(V value) {

		GNode node = createChildNode(value);

		childNodes.put(value, node);
		parentNode.addChild(node);

		return node;
	}
}
