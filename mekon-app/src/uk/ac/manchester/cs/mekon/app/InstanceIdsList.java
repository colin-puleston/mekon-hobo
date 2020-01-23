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

import java.awt.Color;
import java.awt.Component;
import java.util.*;
import javax.swing.*;

import uk.ac.manchester.cs.mekon.model.*;
import uk.ac.manchester.cs.mekon.gui.*;
import uk.ac.manchester.cs.mekon.gui.icon.*;

/**
 * @author Colin Puleston
 */
class InstanceIdsList extends JList<InstanceIdListElement> {

	static private final long serialVersionUID = -1;

	static private final Icon ASSERTION_ICON = createIcon(Color.CYAN);
	static private final Icon QUERY_ICON = createIcon(Color.GREEN);

	static private final Color SELECTION_CLR = UIManager.getColor("Tree.selectionBackground");

	static private GIcon createIcon(Color clr) {

		return new GIcon(new GDiamondRenderer(clr, 12));
	}

	private boolean queryInstances;
	private Set<CIdentity> instanceIds = new HashSet<CIdentity>();

	private DefaultListModel<InstanceIdListElement> model
				= new DefaultListModel<InstanceIdListElement>();

	private class IdsCellRenderer implements ListCellRenderer<InstanceIdListElement> {

		static private final long serialVersionUID = -1;

		public Component getListCellRendererComponent(
							JList<? extends InstanceIdListElement> list,
							InstanceIdListElement value,
							int index,
							boolean sel,
							boolean hasFocus) {

			JLabel label = new JLabel(value.toString());

			GFonts.setLarge(label);
			label.setIcon(queryInstances ? QUERY_ICON : ASSERTION_ICON);

			if (sel) {

				label.setOpaque(true);
				label.setBackground(SELECTION_CLR);
			}

			return label;
		}
	}

	InstanceIdsList(boolean queryInstances, boolean multiSelect) {

		this.queryInstances = queryInstances;

		setModel(model);
		setCellRenderer(new IdsCellRenderer());

		setSelectionMode(getSelectionMode(multiSelect));
		setSelectedIndex(-1);
	}

	InstanceIdsList(
		boolean queryInstances,
		boolean multiSelect,
		Collection<CIdentity> instanceIds) {

		this(queryInstances, multiSelect);

		update(instanceIds);
	}

	InstanceIdsList deriveList(boolean multiSelect) {

		return new InstanceIdsList(queryInstances, multiSelect, instanceIds);
	}

	void update(Collection<CIdentity> instanceIds) {

		this.instanceIds.addAll(instanceIds);

		updateList();
	}

	void add(CIdentity instanceId) {

		instanceIds.add(instanceId);

		updateList();
	}

	void remove(CIdentity instanceId) {

		instanceIds.remove(instanceId);

		updateList();
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

		for (InstanceIdListElement element : getSelectedValuesList()) {

			ids.add(element.id);
		}

		return ids;
	}

	private void updateList() {

		model.clear();

		for (InstanceIdListElement element : getSortedElements()) {

			model.addElement(element);
		}

		revalidate();
	}

	private int getSelectionMode(boolean multiSelect) {

		return multiSelect
				? ListSelectionModel.MULTIPLE_INTERVAL_SELECTION
				: ListSelectionModel.SINGLE_SELECTION;
	}

	private Set<InstanceIdListElement> getSortedElements() {

		Set<InstanceIdListElement> elements = new TreeSet<InstanceIdListElement>();

		for (CIdentity instanceId : instanceIds) {

			elements.add(new InstanceIdListElement(instanceId));
		}

		return elements;
	}
}