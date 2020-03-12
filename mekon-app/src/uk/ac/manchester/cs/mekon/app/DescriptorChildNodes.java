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

package uk.ac.manchester.cs.mekon.app;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.model.util.*;
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class DescriptorChildNodes {

	private InstanceNode parentNode;
	private DescriptorsList descriptors;

	private SlotUpdateRelayer slotUpdateRelayer;
	private ValueUpdateRelayer valueUpdateRelayer;

	private class Updater {

		private List<Descriptor> oldDescriptors;
		private List<Descriptor> newDescriptors;

		Updater() {

			oldDescriptors = descriptors.getList();
			descriptors.update(viewOnly());
			newDescriptors = descriptors.getList();

			if (!newDescriptors.equals(oldDescriptors)) {

				removeOldChildNodes();
				addNewChildNodes();
			}
		}

		private void removeOldChildNodes() {

			int childIdx = 0;

			for (Descriptor descriptor : oldDescriptors) {

				if (!newDescriptors.contains(descriptor)) {

					removeFromParentNode(childIdx--);
				}

				childIdx++;
			}
		}

		private void addNewChildNodes() {

			int childIdx = 0;

			for (Descriptor descriptor : newDescriptors) {

				if (!oldDescriptors.contains(descriptor)) {

					addToParentNode(descriptor, childIdx);
				}

				childIdx++;
			}
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

		descriptors = new DescriptorsList(getInstantiator(), container, viewOnly());

		slotUpdateRelayer = new SlotUpdateRelayer();
		valueUpdateRelayer = new ValueUpdateRelayer(container);
	}

	void update() {

		new Updater();
	}

	void addInitialChildren() {

		for (Descriptor descriptor : descriptors.getList()) {

			addToParentNode(descriptor, -1);
		}
	}

	private void addToParentNode(Descriptor descriptor, int index) {

		DescriptorNode node = createDescriptorNode(descriptor);

		parentNode.addChild(node, index);

		ISlot slot = descriptor.getSlot();

		slotUpdateRelayer.checkAddTo(slot);
		valueUpdateRelayer.checkAddTo(slot);
	}

	private void removeFromParentNode(int index) {

		parentNode.removeChild(index);
	}

	private DescriptorNode createDescriptorNode(Descriptor descriptor) {

		InstanceTree tree = parentNode.getInstanceTree();

		if (descriptor.structuredType() && descriptor.hasValue()) {

			return new StructuredDescriptorNode(tree, descriptor);
		}

		return new DescriptorNode(tree, descriptor);
	}

	private Instantiator getInstantiator() {

		return parentNode.getInstantiator();
	}

	private boolean viewOnly() {

		return parentNode.viewOnly();
	}
}
