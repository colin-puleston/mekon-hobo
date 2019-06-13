/**
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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class CFrameUsagePanel extends GVerticalPanel {

	static private final long serialVersionUID = -1;

	private CFrame frame;
	private CFrameSelectionListener reselectionListener;

	private class UsageTree extends CTree {

		static private final long serialVersionUID = -1;

		UsageTree(CFrame rootFrame) {

			addUsageTree(this);
			addSelectionListener(reselectionListener);

			initialise(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSlotNodes();
			parent.checkAddFixedValuesNode();
			parent.addSubFrameNodes(CVisibility.ALL);
		}

		boolean requiredCValue(CValue<?> value) {

			return value.equals(frame) || value.equals(frame.getType());
		}

		int autoExpandCFrameNodesToLevel() {

			return 2;
		}
	}

	private class UsageAsSubFrameTree extends UsageTree {

		static private final long serialVersionUID = -1;

		UsageAsSubFrameTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSubFrameNodes(CVisibility.ALL);
		}
	}

	private class UsageAsSlotValueTypeTree extends UsageTree {

		static private final long serialVersionUID = -1;

		UsageAsSlotValueTypeTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSlotNodes();
		}
	}

	private class UsageAsSlotValueTree extends UsageTree {

		static private final long serialVersionUID = -1;

		UsageAsSlotValueTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.checkAddFixedValuesNode();
		}
	}

	CFrameUsagePanel(CFrame frame, CFrameSelectionListener reselectionListener) {

		super(true);

		this.frame = frame;
		this.reselectionListener = reselectionListener;

		addUsageAsSubFrame();
		addUsageAsSlotValueType(frame);
		addUsageAsSlotValueType(frame.getType());
		addUsageAsSlotValue(frame);
		addUsageAsSlotValue(frame.getType());
	}

	private void addUsageAsSubFrame() {

		for (CFrame sup : frame.getSupers()) {

			if (!sup.isRoot()) {

				new UsageAsSubFrameTree(sup);
			}
		}
	}

	private void addUsageAsSlotValueType(CValue<?> usedValue) {

		for (CSlot slot : usedValue.getReferencingSlots()) {

			new UsageAsSlotValueTypeTree(slot.getContainer());
		}
	}

	private void addUsageAsSlotValue(CValue<?> usedValue) {

		for (CFrame frame : usedValue.getSlotValueReferencingFrames()) {

			new UsageAsSlotValueTree(frame);
		}
	}

	private void addUsageTree(UsageTree tree) {

		addComponent(wrapInInertScroller(wrapInVerticalPanel(tree)));
	}

	private GVerticalPanel wrapInVerticalPanel(JComponent component) {

		GVerticalPanel panel = new GVerticalPanel(true);

		panel.addComponent(component);

		return panel;
	}

	private JScrollPane wrapInInertScroller(JComponent component) {

		return new JScrollPane(
						component,
						JScrollPane.VERTICAL_SCROLLBAR_NEVER,
						JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
	}
}
