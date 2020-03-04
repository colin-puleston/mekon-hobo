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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstantiationsPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CREATE_LABEL = "Create...";
	static private final String LOAD_LABEL = "Load";
	static private final String RENAME_LABEL = "Rename...";
	static private final String REMOVE_LABEL = "Remove";

	static private final int WIDTH = 200;

	private InstanceGroup instanceGroup;
	private InstanceIdsList idsList;

	private InstantiationOps instantiationOps;

	private class CreateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			instantiationOps.checkDisplayNew();
		}

		CreateButton() {

			super(CREATE_LABEL);
		}
	}

	private class LoadButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		LoadButton() {

			super(idsList, LOAD_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			instantiationOps.displayReloaded(storeId);
		}
	}

	private class RenameButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		RenameButton() {

			super(idsList, RENAME_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			instantiationOps.checkRename(storeId);
		}
	}

	private class RemoveButton extends SelectedInstanceIdActionButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(idsList, REMOVE_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			instanceGroup.checkRemoveInstance(storeId);
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(WIDTH, (int)super.getPreferredSize().getHeight());
	}

	InstantiationsPanel(InstanceGroup instanceGroup, InstanceIdsList idsList, String title) {

		super(new BorderLayout());

		this.instanceGroup = instanceGroup;
		this.idsList = idsList;

		instantiationOps = createInstantiationOps();

		setTitle(title);

		add(new GListPanel<CIdentity>(idsList), BorderLayout.CENTER);
		add(createControlsComponent(), BorderLayout.SOUTH);
	}

	void setTitle(String title) {

		PanelEntitler.entitle(this, title);
	}

	void displayIds(Collection<CIdentity> ids) {

		idsList.update(ids);
	}

	void clearIds() {

		idsList.clearList();
	}

	IFrameFunction getInstantiationsFunction() {

		return IFrameFunction.ASSERTION;
	}

	boolean allowLoadActionOnly() {

		return false;
	}

	private InstantiationOps createInstantiationOps() {

		return new InstantiationOps(this, instanceGroup, getInstantiationsFunction());
	}

	private JComponent createControlsComponent() {

		return allowLoadActionOnly() ? new LoadButton() : createFullControlsComponent();
	}

	private JComponent createFullControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		panel.addControl(new CreateButton());
		panel.addControl(new LoadButton());
		panel.addControl(new RenameButton());
		panel.addControl(new RemoveButton());

		return panel;
	}
}
