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

/**
 * @author Colin Puleston
 */
class CFrameUsagePanel extends JPanel {

	static private final long serialVersionUID = -1;

	private CFrame frame;
	private CFrameSelectionListener reselectionListener;

	private class UsageTree extends CTree {

		static private final long serialVersionUID = -1;

		UsageTree(CFrame rootFrame) {

			CFrameUsagePanel.this.add(wrapInInertScroller());
			addSelectionListener(reselectionListener);

			initialise(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSlotNodes();
			parent.checkAddSlotValuesNode();
			parent.addSubFrameNodes(CFrameVisibility.ALL);
		}

		boolean requiredCValue(CValue<?> value) {

			return value.equals(frame) || value.equals(frame.getType());
		}

		int autoExpandCFrameNodesToLevel() {

			return 2;
		}

		private JScrollPane wrapInInertScroller() {

			JScrollPane scroller = new JScrollPane(
										this,
										JScrollPane.VERTICAL_SCROLLBAR_NEVER,
										JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

			scroller.setPreferredSize(getPreferredSize());

			return scroller;
		}
	}

	private class SubFrameUsageTree extends UsageTree {

		static private final long serialVersionUID = -1;

		SubFrameUsageTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSubFrameNodes(CFrameVisibility.ALL);
		}
	}

	private class SlotValueTypeUsageTree extends UsageTree {

		static private final long serialVersionUID = -1;

		SlotValueTypeUsageTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.addSlotNodes();
		}
	}

	private class SlotValuesUsageTree extends UsageTree {

		static private final long serialVersionUID = -1;

		SlotValuesUsageTree(CFrame rootFrame) {

			super(rootFrame);
		}

		void addCFrameChildren(CFrameNode parent) {

			parent.checkAddSlotValuesNode();
		}
	}

	CFrameUsagePanel(CFrame frame, CFrameSelectionListener reselectionListener) {

		this.frame = frame;
		this.reselectionListener = reselectionListener;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		addSubFrameUsage();
		addSlotValueTypeUsage(frame);
		addSlotValueTypeUsage(frame.getType());
		addSlotValuesUsage(frame);
		addSlotValuesUsage(frame.getType());
	}

	private void addSubFrameUsage() {

		for (CFrame sup : frame.getSupers()) {

			if (!sup.isRoot()) {

				new SubFrameUsageTree(sup);
			}
		}
	}

	private void addSlotValueTypeUsage(CValue<?> usedValue) {

		for (CSlot slot : usedValue.getReferencingSlots()) {

			new SlotValueTypeUsageTree(slot.getContainer());
		}
	}

	private void addSlotValuesUsage(CValue<?> usedValue) {

		for (CFrame frame : usedValue.getSlotValueReferencingFrames()) {

			new SlotValuesUsageTree(frame);
		}
	}
}
