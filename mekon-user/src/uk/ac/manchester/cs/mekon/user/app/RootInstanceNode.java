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

package uk.ac.manchester.cs.mekon.user.app;

import java.awt.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class RootInstanceNode extends InstanceNode {

	private IFrame rootFrame;
	private DescriptorChildNodes childNodes;

	private class CellDisplay extends InstanceCellDisplay {

		CellDisplay() {

			super(RootInstanceNode.this);
		}

		GCellDisplay createDefault() {

			return createForValue(getDisplayLabel());
		}

		Icon getIcon() {

			return MekonAppIcons.VALUE_ICONS.get(queryInstance(), false);
		}

		boolean editableSlot() {

			return false;
		}

		private String getDisplayLabel() {

			return getCustomiser().getValueDisplayLabel(rootFrame);
		}

		private Customiser getCustomiser() {

			return getInstantiator().getController().getCustomiser();
		}
	}

	protected void addInitialChildren() {

		childNodes.addInitialChildren();
	}

	protected void onChildrenInitialised() {

		childNodes.onChildrenInitialised();
	}

	protected GCellDisplay getDisplay() {

		return new CellDisplay().create();
	}

	RootInstanceNode(InstanceTree tree, IFrame rootFrame) {

		super(tree);

		this.rootFrame = rootFrame;

		childNodes = new DescriptorChildNodes(this, rootFrame);
	}

	void updateChildList() {

		childNodes.update();
	}
}
