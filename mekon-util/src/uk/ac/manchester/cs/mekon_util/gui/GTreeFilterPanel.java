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

package uk.ac.manchester.cs.mekon_util.gui;

import java.util.*;

/**
 * @author Colin Puleston
 */
public abstract class GTreeFilterPanel<N> extends GFilterPanel {

	static private final long serialVersionUID = -1;

	private NodeFilter inactiveFilter = new InactiveNodeFilter();
	private NodeFilter currentFilter = inactiveFilter;

	private abstract class NodeFilter {

		abstract boolean passesFilter(N node);

		abstract boolean requiredInTree(N node);
	}

	private class InactiveNodeFilter extends NodeFilter {

		boolean passesFilter(N node) {

			return false;
		}

		boolean requiredInTree(N node) {

			return true;
		}
	}

	private class ActiveNodeFilter extends NodeFilter {

		private GLexicalFilter lexicalFilter;
		private Set<N> displayInTrees = new HashSet<N>();

		ActiveNodeFilter(GLexicalFilter lexicalFilter) {

			this.lexicalFilter = lexicalFilter;

			initialiseFrom(getRootNodes());
		}

		boolean passesFilter(N node) {

			return lexicalFilter.pass(getNodeLabel(node));
		}

		boolean requiredInTree(N node) {

			return displayInTrees.contains(node);
		}

		private boolean initialiseFrom(N node) {

			if (initialiseFrom(getChildNodes(node)) || passesFilter(node)) {

				displayInTrees.add(node);

				return true;
			}

			return false;
		}

		private boolean initialiseFrom(Collection<N> nodes) {

			boolean anyFilterPasses = false;

			for (N node : nodes) {

				anyFilterPasses |= initialiseFrom(node);
			}

			return anyFilterPasses;
		}
	}

	public boolean passesFilter(N node) {

		return currentFilter.passesFilter(node);
	}

	public boolean requiredInTree(N node) {

		return currentFilter.requiredInTree(node);
	}

	protected void applyFilter(GLexicalFilter filter) {

		setNodeFilter(new ActiveNodeFilter(filter));
	}

	protected void clearFilter() {

		setNodeFilter(inactiveFilter);
	}

	protected abstract void reinitialiseTree();

	protected abstract Collection<N> getRootNodes();

	protected abstract Collection<N> getChildNodes(N parent);

	protected abstract String getNodeLabel(N node);

	private void setNodeFilter(NodeFilter filter) {

		currentFilter = filter;

		reinitialiseTree();
	}
}
