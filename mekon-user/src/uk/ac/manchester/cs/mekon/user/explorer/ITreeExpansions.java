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

package uk.ac.manchester.cs.mekon.user.explorer;

import java.util.*;

/**
 * @author Colin Puleston
 */
class ITreeExpansions {

	private INode rootNode;
	private Set<INode> expandeds = new HashSet<INode>();

	ITreeExpansions(INode rootNode) {

		this.rootNode = rootNode;
	}

	void update(INode updated) {

		expandeds.clear();
		expandeds.add(updated);

		addExpandeds(rootNode);
	}

	void restore() {

		restoreCollapseds(rootNode);
	}

	private void addExpandeds(INode current) {

		if (current.expanded()) {

			expandeds.add(current);

			for (INode child : current.getIChildren()) {

				addExpandeds(child);
			}
		}
	}

	private void restoreCollapseds(INode current) {

		if (current.expanded()) {

			if (expandeds.contains(current)) {

				for (INode child : current.getIChildren()) {

					restoreCollapseds(child);
				}
			}
			else {

				collapseDescendantsLowestFirst(current);
				current.collapse();
			}
		}
	}

	private void collapseDescendantsLowestFirst(INode node) {

		for (INode child : node.getIChildren()) {

			if (node.expanded()) {

				collapseDescendantsLowestFirst(child);
				node.collapse();
			}
		}
	}
}
