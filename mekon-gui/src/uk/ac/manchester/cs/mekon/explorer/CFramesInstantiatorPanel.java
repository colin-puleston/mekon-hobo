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

package uk.ac.manchester.cs.mekon.explorer;

import java.util.*;

import uk.ac.manchester.cs.mekon.model.*;

import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class CFramesInstantiatorPanel extends CFramesComboPanel {

	static private final long serialVersionUID = -1;

	static private final String INSTANCE_LIST_TITLE_PREFIX = "Instance";

	private CFrameInstances instances;
	private CFramesInstantiatorTree tree;

	private class InstancesListListener extends GSelectionListener<CIdentity> {

		protected void onSelected(CIdentity instance) {

			selectInstance(instance);
		}

		protected void onSelectionCleared() {
		}
	}

	private class InstancesList extends GList<CIdentity> {

		static private final long serialVersionUID = -1;

		InstancesList() {

			super(true);

			for (CIdentity instance : instances.getAllForRootFrame()) {

				addInstance(instance);
			}

			addSelectionListener(new InstancesListListener());
		}

		private void addInstance(CIdentity instance) {

			addEntity(instance, EntityDisplays.get().forInstanceRef(instance));
		}
	}

	CFramesInstantiatorPanel(CFrame rootFrame, CFrameInstances instances) {

		super(rootFrame, CVisibility.ALL, false);

		this.instances = instances;

		tree = new CFramesInstantiatorTree(rootFrame, instances);

		addTree(tree);

		if (!rootFrame.getSubs(CVisibility.EXPOSED).isEmpty()) {

			addDefaultList();
		}

		addList(INSTANCE_LIST_TITLE_PREFIX, new InstancesList());
	}

	CIdentity getInstanceOrNull() {

		return tree.getInstanceOrNull();
	}

	private void selectInstance(CIdentity instance) {

		tree.selectInstance(instances.getDirectFrameFor(instance), instance);
	}
}
