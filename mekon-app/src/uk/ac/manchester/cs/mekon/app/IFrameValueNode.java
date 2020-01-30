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
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class IFrameValueNode extends ValueNode {

	private DescriptorsList aspectDescriptors;
	private UpdateRelayer updateRelayer = new UpdateRelayer();

	private class ChildNodesUpdater {

		private List<Descriptor> oldDescriptors;
		private List<Descriptor> newDescriptors;

		ChildNodesUpdater() {

			oldDescriptors = aspectDescriptors.getList();
			aspectDescriptors.update();
			newDescriptors = aspectDescriptors.getList();

			if (!newDescriptors.equals(oldDescriptors)) {

				removeOldChildNodes();
				addNewChildNodes();
			}
		}

		private void removeOldChildNodes() {

			int childIdx = 0;

			for (Descriptor descriptor : oldDescriptors) {

				if (!newDescriptors.contains(descriptor)) {

					removeChild(childIdx--);
				}

				childIdx++;
			}
		}

		private void addNewChildNodes() {

			int childIdx = 0;

			for (Descriptor descriptor : newDescriptors) {

				if (!oldDescriptors.contains(descriptor)) {

					addChild(descriptor, childIdx);
				}

				childIdx++;
			}
		}
	}

	private class UpdateRelayer implements ISlotListener, KUpdateListener {

		private Set<ISlot> targetSlots = new HashSet<ISlot>();

		public void onUpdatedValueType(CValue<?> valueType) {

			new ChildNodesUpdater();
		}

		public void onUpdatedCardinality(CCardinality cardinality) {

			new ChildNodesUpdater();
		}

		public void onUpdatedActivation(CActivation activation) {

			new ChildNodesUpdater();
		}

		public void onUpdatedEditability(CEditability editability) {

			new ChildNodesUpdater();
		}

		public void onUpdated() {

			new ChildNodesUpdater();
		}

		void checkAddTo(ISlot slot) {

			if (targetSlots.add(slot)) {

				slot.addListener(this);
				slot.getValues().addUpdateListener(this);
			}
		}
	}

	protected void addInitialChildren() {

		for (Descriptor descriptor : aspectDescriptors.getList()) {

			addChild(descriptor);
		}
	}

	IFrameValueNode(InstantiationTree tree, IFrame topLevelAspect) {

		super(tree);

		createAspectDescriptors(topLevelAspect);
	}

	IFrameValueNode(InstantiationTree tree, Descriptor descriptor) {

		super(tree, descriptor);

		createAspectDescriptors((IFrame)descriptor.getCurrentValue());
	}

	private void createAspectDescriptors(IFrame aspect) {

		aspectDescriptors = new DescriptorsList(getInstantiator(), aspect);
	}

	private void addChild(Descriptor descriptor) {

		addChild(descriptor, -1);
	}

	private void addChild(Descriptor descriptor, int index) {

		DescriptorNode child = createDescriptorNode(descriptor);

		addChild(child, index);
		updateRelayer.checkAddTo(descriptor.getSlot());
	}

	private DescriptorNode createDescriptorNode(Descriptor descriptor) {

		return new DescriptorNode(getInstantiationTree(), descriptor);
	}
}
