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

package uk.ac.manchester.cs.mekon.gui;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFrameNode extends GNode {

	static private final List<Class<? extends GNode>> childTypePriority
								= new ArrayList<Class<? extends GNode>>();

	static {

		childTypePriority.add(CSlotNode.class);
		childTypePriority.add(CFrameFixedValuesNode.class);
		childTypePriority.add(CFrameNode.class);
	}

	private CTree tree;
	private CFrame frame;

	protected Boolean leafNodeFastCheck() {

		return tree.leafCFrameNodeFastCheck(this);
	}

	protected void addInitialChildren() {

		tree.addCFrameChildren(this);
	}

	protected boolean orderedChildren() {

		return true;
	}

	protected int compareChildrenPriorToLabelCompare(GNode first, GNode second) {

		Class<?> c1 = first.getClass();
		Class<?> c2 = second.getClass();

		if (c1 != c2) {

			int p1 = childTypePriority.indexOf(c1);
			int p2 = childTypePriority.indexOf(c2);

			return p1 - p2;
		}

		if (c1 == CFrameNode.class) {

			boolean h1 = ((CFrameNode)first).frame.hidden();
			boolean h2 = ((CFrameNode)second).frame.hidden();

			return h1 == h2 ? 0 : (h1 ? 1 : -1);
		}

		return 1;
	}

	protected boolean autoExpand() {

		return getNodeLevel() < tree.autoExpandCFrameNodesToLevel();
	}

	protected GCellDisplay getDisplay() {

		return EntityDisplays.get().get(frame);
	}

	CFrameNode(CTree tree, CFrame frame) {

		super(tree);

		this.tree = tree;
		this.frame = frame;
	}

	List<CFrameNode> addSubFrameNodes(CVisibility visibility) {

		List<CFrameNode> nodes = new ArrayList<CFrameNode>();

		if (visibility == CVisibility.ALL) {

			addSubFrameNodes(nodes, CVisibility.EXPOSED);
			addSubFrameNodes(nodes, CVisibility.HIDDEN);
		}
		else {

			addSubFrameNodes(nodes, visibility);
		}

		return nodes;
	}

	void addSlotNodes() {

		for (CSlot slot : frame.getSlots().asList()) {

			if (tree.requiredCValue(slot.getValueType())) {

				addChild(new CSlotNode(tree, slot));
			}
		}
	}

	void checkAddFixedValuesNode() {

		List<CIdentity> slotIds = getRequiredSlotValueIds();

		if (!slotIds.isEmpty()) {

			addChild(new CFrameFixedValuesNode(tree, frame, slotIds));
		}
	}

	CFrame getCFrame() {

		return frame;
	}

	private void addSubFrameNodes(List<CFrameNode> nodes, CVisibility visibility) {

		for (CFrame subFrame : frame.getSubs(visibility)) {

			if (tree.requiredCValue(subFrame)) {

				CFrameNode node = new CFrameNode(tree, subFrame);

				addChild(node);
				nodes.add(node);
			}
		}
	}

	private List<CIdentity> getRequiredSlotValueIds() {

		CSlotValues values = frame.getSlotValues();
		List<CIdentity> requiredIds = new ArrayList<CIdentity>();

		for (CIdentity id : values.getSlotIdentities()) {

			if (containsRequiredCValue(values.getValues(id))) {

				requiredIds.add(id);
			}
		}

		return requiredIds;
	}

	private boolean containsRequiredCValue(List<CValue<?>> values) {

		for (CValue<?> value : values) {

			if (tree.requiredCValue(value)) {

				return true;
			}
		}

		return false;
	}
}
