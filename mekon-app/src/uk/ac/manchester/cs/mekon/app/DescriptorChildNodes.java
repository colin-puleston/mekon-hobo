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
import uk.ac.manchester.cs.mekon.util.*;

/**
 * @author Colin Puleston
 */
class DescriptorChildNodes {

	private InstantiationNode parentNode;
	private DescriptorsList descriptors;

	private UpdateRelayer updateRelayer = new UpdateRelayer();

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

	private class UpdateRelayer implements ISlotListener, KUpdateListener {

		private Set<ISlot> targetSlots = new HashSet<ISlot>();

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

		public void onUpdated() {

			new Updater();
		}

		void checkAddTo(ISlot slot) {

			if (targetSlots.add(slot)) {

				slot.addListener(this);
				slot.getValues().addUpdateListener(this);
			}
		}
	}

	DescriptorChildNodes(InstantiationNode parentNode, IFrame aspect) {

		this.parentNode = parentNode;

		descriptors = new DescriptorsList(getInstantiator(), aspect, viewOnly());
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
		updateRelayer.checkAddTo(descriptor.getSlot());
	}

	private void removeFromParentNode(int index) {

		parentNode.removeChild(index);
	}

	private DescriptorNode createDescriptorNode(Descriptor descriptor) {

		InstantiationTree tree = parentNode.getInstantiationTree();

		if (descriptor.aspectType() && descriptor.hasValue()) {

			return new AspectValuedDescriptorNode(tree, descriptor);
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
