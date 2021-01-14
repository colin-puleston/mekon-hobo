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

package uk.ac.manchester.cs.mekon.user.remote;

import java.awt.Color;
import javax.swing.*;

import uk.ac.manchester.cs.mekon_util.remote.admin.*;
import uk.ac.manchester.cs.mekon_util.remote.admin.client.*;
import uk.ac.manchester.cs.mekon_util.gui.*;

/**
 * @author Colin Puleston
 */
class LocksPanel extends EntitiesPanel<RLock> {

	static private final long serialVersionUID = -1;

	static private final String RESOURCES_TITLE = "Resource";
	static private final String OWNERS_TITLE = "Owner";

	static private final Color RESOURCE_TEXT_CLR = Color.BLACK;
	static private final Color OWNER_TEXT_CLR = Color.BLUE;

	LocksPanel(RAdminClient adminClient) {

		initialise(new LockManager(this, adminClient));
	}

	void populateUpdateButtonsPanel(JPanel panel) {

		panel.add(new DeleteButton());
	}

	void populateCustomButtonsPanel(JPanel panel) {
	}

	void addTableColumns(GTable table) {

		table.addColumns(RESOURCES_TITLE, OWNERS_TITLE);
	}

	void addTableRow(GTable table, RLock lock) {

		table.addRow(createResourceLabel(lock), createOwnerLabel(lock));
	}

	private JLabel createOwnerLabel(RLock lock) {

		return createTableLabel(lock.getOwnerName(), OWNER_TEXT_CLR);
	}

	private JLabel createResourceLabel(RLock lock) {

		return createTableLabel(lock.getResourceId(), RESOURCE_TEXT_CLR);
	}
}
