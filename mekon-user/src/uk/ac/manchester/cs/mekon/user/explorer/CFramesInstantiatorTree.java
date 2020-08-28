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

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class CFramesInstantiatorTree extends CFramesTree {

	static private final long serialVersionUID = -1;

	private CFrameInstances instances;
	private CIdentity selectedInstance = null;

	private class InstantiatorFrameNode extends CFrameNode {

		protected void addInitialChildren() {

			super.addInitialChildren();

			addInstanceChildren();
		}

		InstantiatorFrameNode(CFrame frame) {

			super(CFramesInstantiatorTree.this, frame);
		}

		private void addInstanceChildren() {

			CFrame frame = getCFrame();

			for (CIdentity instance : instances.getDirectFor(frame)) {

				addChild(new InstantiatorInstanceNode(frame, instance));
			}
		}
	}

	private class InstantiatorInstanceNode extends GNode {

		private CFrame frame;
		private CIdentity instance;

		protected Boolean leafNodeFastCheck() {

			return true;
		}

		protected GCellDisplay getDisplay() {

			return EntityDisplays.get().forInstanceRef(instance);
		}

		InstantiatorInstanceNode(CFrame frame, CIdentity instance) {

			super(CFramesInstantiatorTree.this);

			this.frame = frame;
			this.instance = instance;
		}

		void onSelected() {

			selectInstance(frame, instance);
		}
	}

	private class InstanceNodesListener extends GSelectionListener<GNode> {

		protected void onSelected(GNode node) {

			if (node instanceof InstantiatorInstanceNode) {

				((InstantiatorInstanceNode)node).onSelected();
			}
		}

		protected void onDeselected(GNode node) {
		}
	}

	CFramesInstantiatorTree(CFrame rootFrame, CFrameInstances instances) {

		super(CVisibility.EXPOSED);

		this.instances = instances;

		initialise(rootFrame, true);
		addNodeSelectionListener(new InstanceNodesListener());
	}

	CFrameNode createCFrameNode(CFrame frame) {

		return new InstantiatorFrameNode(frame);
	}

	Boolean leafCFrameNodeFastCheck(CFrameNode node) {

		return super.leafCFrameNodeFastCheck(node)
				&& !instances.anyDirectFor(node.getCFrame());
	}

	void selectInstance(CFrame frame, CIdentity instance) {

		selectedInstance = instance;

		select(frame);
	}

	CIdentity getInstanceOrNull() {

		return selectedInstance;
	}
}
