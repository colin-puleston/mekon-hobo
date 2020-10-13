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

/**
 * @author Colin Puleston
 */
class ArrayNodeReplacements {

	private InstanceNode parentNode;
	private List<Replacement> replacements = new ArrayList<Replacement>();

	private class Replacement {

		private DescriptorArrayNode arrayNode;
		private List<DescriptorNode> elementNodes;

		Replacement(DescriptorArrayNode arrayNode) {

			this.arrayNode = arrayNode;

			elementNodes = arrayNode.getChildren(DescriptorNode.class);

			replace();
			replacements.add(this);
		}

		void restore() {

			parentNode.addChild(arrayNode, getChildIndex(elementNodes.get(0)));

			transferElementNodes(arrayNode, 0);
		}

		private void replace() {

			transferElementNodes(parentNode, getChildIndex(arrayNode));

			arrayNode.remove();
		}

		private void transferElementNodes(InstanceNode newParentNode, int index) {

			for (DescriptorNode elementNode : elementNodes) {

				elementNode.remove();

				newParentNode.addChild(elementNode, index++);
			}
		}
	}

	ArrayNodeReplacements(InstanceNode parentNode) {

		this.parentNode = parentNode;
	}

	void checkReplace() {

		if (parentNode.showQuerySemantics()) {

			for (DescriptorArrayNode arrayNode : getArrayNodes()) {

				new Replacement(arrayNode);
			}
		}
	}

	void checkRestore() {

		for (Replacement replacement : replacements) {

			replacement.restore();
		}

		replacements.clear();
	}

	private List<DescriptorArrayNode> getArrayNodes() {

		return parentNode.getChildren(DescriptorArrayNode.class);
	}

	private int getChildIndex(InstanceNode child) {

		return parentNode.getIndex(child);
	}
}
