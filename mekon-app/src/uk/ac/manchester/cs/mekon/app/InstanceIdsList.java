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
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;

/**
 * @author Colin Puleston
 */
class InstanceIdsList extends JList<String> {

	static private final long serialVersionUID = -1;

	private DefaultListModel<String> model = new DefaultListModel<String>();

	InstanceIdsList(boolean multiSelect) {

		setModel(model);

		setSelectionMode(getSelectionMode(multiSelect));
		setSelectedIndex(-1);

		GFonts.setLarge(this);
	}

	InstanceIdsList(boolean multiSelect, Collection<CIdentity> instanceIds) {

		this(multiSelect);

		update(instanceIds);
	}

	void update(Collection<CIdentity> instanceIds) {

		model.clear();

		for (String element : getSortedElements(instanceIds)) {

			model.addElement(element);
		}

		revalidate();
	}

	boolean isSelectedId() {

		return getSelectedValue() != null;
	}

	CIdentity getSelectedId() {

		List<CIdentity> ids = getSelectedIds();

		if (ids.size() == 1) {

			return ids.get(0);
		}

		throw new Error("Expected single selection, found: " + ids.size());
	}

	List<CIdentity> getSelectedIds() {

		List<CIdentity> ids = new ArrayList<CIdentity>();

		for (String idValue : getSelectedValuesList()) {

			ids.add(new CIdentity(idValue, idValue));
		}

		return ids;
	}

	private int getSelectionMode(boolean multiSelect) {

		return multiSelect
				? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
				: ListSelectionModel.SINGLE_SELECTION;
	}

	private Set<String> getSortedElements(Collection<CIdentity> instanceIds) {

		Set<String> elements = new TreeSet<String>();

		for (CIdentity instanceId : instanceIds) {

			elements.add(instanceId.getIdentifier());
		}

		return elements;
	}
}