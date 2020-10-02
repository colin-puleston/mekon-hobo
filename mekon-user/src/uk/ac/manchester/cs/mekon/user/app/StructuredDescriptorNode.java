/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 University of Manchester
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files the "Software", to deal
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

package uk.ac.manchester.cs.mekon.user.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

/**
 * @author Colin Puleston
 */
class StructuredDescriptorNode extends DescriptorNode {

	private DescriptorChildNodes childNodes;

	private List<ArrayNodeReplacement> arrayNodeReplacements
							= new ArrayList<ArrayNodeReplacement>();

	private class ArrayNodeReplacement {

		private DescriptorArrayNode arrayNode;
		private List<DescriptorNode> elementNodes;

		ArrayNodeReplacement(DescriptorArrayNode arrayNode) {

			this.arrayNode = arrayNode;

			elementNodes = arrayNode.getChildren(DescriptorNode.class);

			replace();
			arrayNodeReplacements.add(this);
		}

		void restore() {

			addChild(arrayNode, getIndex(elementNodes.get(0)));
			transferElementNodes(arrayNode, 0);
		}

		private void replace() {

			transferElementNodes(StructuredDescriptorNode.this, getIndex(arrayNode));
			arrayNode.remove();
		}

		private void transferElementNodes(InstanceNode newParentNode, int index) {

			for (DescriptorNode elementNode : elementNodes) {

				elementNode.remove();

				newParentNode.addChild(elementNode, index++);
			}
		}
	}

	protected void addInitialChildren() {

		childNodes.addInitialChildren();
	}

	protected void onChildrenInitialised() {

		checkArrayNodesReplace();
	}

	StructuredDescriptorNode(InstanceTree tree, Descriptor descriptor) {

		super(tree, descriptor);

		childNodes = new DescriptorChildNodes(this, (IFrame)descriptor.getValue());
	}

	void updateChildList() {

		checkArrayNodesRestore();
		childNodes.update();
		checkArrayNodesReplace();
	}

	private void checkArrayNodesReplace() {

		if (showQuerySemantics()) {

			for (DescriptorArrayNode arrayNode : getChildren(DescriptorArrayNode.class)) {

				new ArrayNodeReplacement(arrayNode);
			}
		}
	}

	private void checkArrayNodesRestore() {

		for (ArrayNodeReplacement replacement : arrayNodeReplacements) {

			replacement.restore();
		}

		arrayNodeReplacements.clear();
	}
}
