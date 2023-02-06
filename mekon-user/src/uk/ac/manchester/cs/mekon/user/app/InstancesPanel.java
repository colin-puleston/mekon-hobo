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

import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
abstract class InstancesPanel extends JPanel {

	static private final long serialVersionUID = -1;

	static private final String CREATE_LABEL = "Create...";
	static private final String COPY_LABEL = "Copy...";
	static private final String LOAD_LABEL = "Load";
	static private final String RENAME_LABEL = "Rename...";
	static private final String REMOVE_LABEL = "Remove";

	static private final int WIDTH = 200;

	private InstanceGroup group;
	private InstanceIdsList idsList;

	private abstract class CurrentInstanceActionButton extends SelectedInstanceActionButton {

		static private final long serialVersionUID = -1;

		CurrentInstanceActionButton(String label) {

			super(idsList, label);
		}

		boolean enableIfSelection() {

			return !potentialEditOp() || allowPotentialEditOp(idsList.getSelectedEntity());
		}

		boolean potentialEditOp() {

			return true;
		}
	}

	private class CreateButton extends GButton {

		static private final long serialVersionUID = -1;

		protected void doButtonThing() {

			checkCreateAndDisplay();
		}

		CreateButton() {

			super(CREATE_LABEL);
		}
	}

	private class CopyButton extends CurrentInstanceActionButton {

		static private final long serialVersionUID = -1;

		CopyButton() {

			super(COPY_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			loadAndDisplay(storeId, true);
		}

		boolean potentialEditOp() {

			return false;
		}
	}

	private class LoadButton extends CurrentInstanceActionButton {

		static private final long serialVersionUID = -1;

		LoadButton() {

			super(LOAD_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			loadAndDisplay(storeId, false);
		}
	}

	private class RenameButton extends CurrentInstanceActionButton {

		static private final long serialVersionUID = -1;

		RenameButton() {

			super(RENAME_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			checkRename(storeId);
		}
	}

	private class RemoveButton extends CurrentInstanceActionButton {

		static private final long serialVersionUID = -1;

		RemoveButton() {

			super(REMOVE_LABEL);
		}

		void doInstanceThing(CIdentity storeId) {

			checkRemove(storeId);
		}
	}

	private class ClickBasedLoadInvoker extends MouseAdapter {

		public void mouseClicked(MouseEvent e) {

			if (e.getClickCount() == 2) {

				loadAndDisplay(getClickedStoreId(e), false);
			}
		}

		ClickBasedLoadInvoker() {

			idsList.addMouseListener(this);
		}

		private CIdentity getClickedStoreId(MouseEvent e) {

			return idsList.getDisplayedEntity(idsList.locationToIndex(e.getPoint()));
		}
	}

	public Dimension getPreferredSize() {

		return new Dimension(WIDTH, (int)super.getPreferredSize().getHeight());
	}

	InstancesPanel(InstanceGroup group, InstanceIdsList idsList, String title) {

		super(new BorderLayout());

		this.group = group;
		this.idsList = idsList;

		setTitle(title);
	}

	void initialise() {

		add(new GListPanel<CIdentity>(idsList), BorderLayout.CENTER);
		add(createControlsComponent(), BorderLayout.SOUTH);

		new ClickBasedLoadInvoker();
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

	void updateDisplay() {

		idsList.updateDisplay();
	}

	boolean canCreate() {

		return false;
	}

	boolean canEdit() {

		return false;
	}

	void checkCreateAndDisplay() {

		throw new Error("Method should never be invoked!");
	}

	abstract void loadAndDisplay(CIdentity storeId, boolean asCopy);

	void checkRename(CIdentity storeId) {

		throw new Error("Method should never be invoked!");
	}

	void checkRemove(CIdentity storeId) {

		throw new Error("Method should never be invoked!");
	}

	boolean allowPotentialEditOp(CIdentity storeId) {

		return true;
	}

	private JComponent createControlsComponent() {

		return canCreate() ? createFullControlsComponent() : new LoadButton();
	}

	private JComponent createFullControlsComponent() {

		ControlsPanel panel = new ControlsPanel(true);

		if (canCreate()) {

			panel.addControl(new CreateButton());
			panel.addControl(new CopyButton());
		}

		panel.addControl(new LoadButton());

		if (canEdit()) {

			panel.addControl(new RenameButton());
			panel.addControl(new RemoveButton());
		}

		return panel;
	}
}
