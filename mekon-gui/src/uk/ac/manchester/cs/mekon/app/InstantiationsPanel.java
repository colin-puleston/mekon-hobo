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

import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.util.*;

/**
 * @author Colin Puleston
 */
class InstantiationsPanel extends ControlsPanel {

	static private final long serialVersionUID = -1;

	static private final String TITLE = "Create";

	static private final String INSTANCE_BUTTON_LABEL = "Instance...";
	static private final String QUERY_BUTTON_LABEL = "Query...";

	static private final String STORE_NAME_SELECTOR_TITLE = "Enter Instance Name";

	private InstanceType instanceType;

	private class InstanceButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			CIdentity id = checkObtainStoreId();

			if (id != null) {

				new InstanceGFrame(instanceType, id);
			}
		}

		InstanceButton() {

			super(INSTANCE_BUTTON_LABEL);
		}
	}

	private class QueryButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			new QueryGFrame(instanceType);
		}

		QueryButton() {

			super(QUERY_BUTTON_LABEL);
		}
	}

	InstantiationsPanel(InstanceType instanceType) {

		super(false);

		this.instanceType = instanceType;

		PanelEntitler.entitle(this, TITLE);

		addControl(new InstanceButton());
		addControl(new QueryButton());
	}

	private CIdentity checkObtainStoreId() {

		IStringSelector selector = createStoreNameSelector();

		if (selector.display(false) == EditStatus.EDITED) {

			IString storeName = selector.getSelection();

			if (storeName != null) {

				return MekonAppStoreId.toStoreId(storeName.get());
			}
		}

		return null;
	}

	private IStringSelector createStoreNameSelector() {

		return new IStringSelector(findOwnerFrame(), STORE_NAME_SELECTOR_TITLE);
	}

	private JFrame findOwnerFrame() {

		return (JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, this);
	}
}
