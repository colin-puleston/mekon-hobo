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

package uk.ac.manchester.cs.mekon.gui.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon_util.*;

/**
 * @author Colin Puleston
 */
class DescriptorChildNodes {

	private InstanceNode parentNode;

	private IFrame container;
	private FrameDescriptors frameDescriptors;

	private ChildNodeCreator childNodeCreator;

	private SlotUpdateRelayer slotUpdateRelayer;
	private ValueUpdateRelayer valueUpdateRelayer;

	private class Updater {

		private FrameDescriptors oldFrameDescriptors = frameDescriptors;

		private List<SlotDescriptors> oldDescriptorsBySlot;
		private List<SlotDescriptors> newDescriptorsBySlot;

		Updater() {

			frameDescriptors = createFrameDescriptors();

			if (!frameDescriptors.equalDescriptors(oldFrameDescriptors)) {

				oldDescriptorsBySlot = oldFrameDescriptors.getDescriptorsBySlot();
				newDescriptorsBySlot = frameDescriptors.getDescriptorsBySlot();

				removeOldChildren();
				addNewAndUpdateArrayChildren();
			}
		}

		private void removeOldChildren() {

			int childIdx = 0;

			for (SlotDescriptors slotDescriptors : oldDescriptorsBySlot) {

				if (!newDescriptorsBySlot.contains(slotDescriptors)) {

					parentNode.removeChild(childIdx--);
				}

				childIdx++;
			}
		}

		private void addNewAndUpdateArrayChildren() {

			int childIdx = 0;

			for (SlotDescriptors slotDescriptors : newDescriptorsBySlot) {

				if (!oldDescriptorsBySlot.contains(slotDescriptors)) {

					addChild(slotDescriptors, childIdx);
				}
				else {

					if (slotDescriptors.populatedMultiValueSlot()) {

						updateArrayChild(slotDescriptors, childIdx);
					}
				}

				childIdx++;
			}
		}

		private void updateArrayChild(SlotDescriptors slotDescriptors, int childIdx) {

			((DescriptorArrayNode)parentNode.getChildAt(childIdx)).update(slotDescriptors);
		}
	}

	private abstract class UpdateRelayer {

		private Set<ISlot> targetSlots = new HashSet<ISlot>();

		void checkAddTo(ISlot slot) {

			if (targetSlots.add(slot)) {

				addTo(slot);
			}
		}

		abstract void addTo(ISlot slot);
	}

	private class SlotUpdateRelayer extends UpdateRelayer implements ISlotListener {

		public void onUpdatedValueType(CValue<?> valueType) {

			new Updater();
		}

		public void onUpdatedCardinality(CCardinality cardinality) {

			new Updater();
		}

		public void onUpdatedActivation(CActivation activation) {

			new Updater();
		}

		public void onUpdatedEditability(CEditability editability) {

			new Updater();
		}

		void addTo(ISlot slot) {

			slot.addListener(this);
		}
	}

	private class ValueUpdateRelayer extends UpdateRelayer implements KUpdateListener {

		private class Propagator extends ISlotUpdateListenerPropagator {

			protected boolean targetSlot(ISlot slot) {

				return true;
			}

			Propagator(IFrame container) {

				super(ValueUpdateRelayer.this);

				propagateFrom(container);
			}
		}

		public void onUpdated() {

			new Updater();
		}

		ValueUpdateRelayer(IFrame container) {

			new Propagator(container);
		}

		void addTo(ISlot slot) {

			slot.getValues().addUpdateListener(this);
		}
	}

	DescriptorChildNodes(InstanceNode parentNode, IFrame container) {

		this.parentNode = parentNode;
		this.container = container;

		frameDescriptors = createFrameDescriptors();
		childNodeCreator = new ChildNodeCreator(parentNode.getInstanceTree());

		slotUpdateRelayer = new SlotUpdateRelayer();
		valueUpdateRelayer = new ValueUpdateRelayer(container);
	}

	void update() {

		new Updater();
	}

	void addInitialChildren() {

		for (SlotDescriptors slotDescriptors : frameDescriptors.getDescriptorsBySlot()) {

			addChild(slotDescriptors, -1);
		}
	}

	private void addChild(SlotDescriptors slotDescriptors, int index) {

		InstanceNode child = childNodeCreator.createFor(slotDescriptors);

		parentNode.addChild(child, index);
		child.initialise();

		ISlot slot = slotDescriptors.getSlot();

		slotUpdateRelayer.checkAddTo(slot);
		valueUpdateRelayer.checkAddTo(slot);
	}

	private FrameDescriptors createFrameDescriptors() {

		return new FrameDescriptors(getInstantiator(), container, viewOnly());
	}

	private Instantiator getInstantiator() {

		return parentNode.getInstantiator();
	}

	private boolean viewOnly() {

		return parentNode.viewOnly();
	}
}
