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

	static private final Icon ASSERTION_ICON = MekonAppIcons.ASSERTION_REF;
	static private final Icon QUERY_ICON = MekonAppIcons.QUERY_REF;

	static private final Color SELECTION_CLR = UIManager.getColor("Tree.selectionBackground");

	static private GIcon createIcon(Color clr) {

		return new GIcon(new GDiamondRenderer(clr, 12));
	}

	private boolean queryInstances;
	private Set<CIdentity> ids = new HashSet<CIdentity>();

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
		Collection<CIdentity> ids) {

		this(queryInstances, multiSelect);

		addAll(ids);
	}

	InstanceIdsList deriveList(boolean multiSelect) {

		return new InstanceIdsList(queryInstances, multiSelect, ids);
	}

	void update(Collection<CIdentity> ids) {

		this.ids.clear();

		addAll(ids);
	}

	void add(CIdentity id) {

		ids.add(id);

		updateList();
	}

	void addAll(Collection<CIdentity> ids) {

		this.ids.addAll(ids);

		updateList();
	}

	void remove(CIdentity id) {

		ids.remove(id);

		updateList();
	}

	void replace(CIdentity id, CIdentity newId) {

		ids.remove(id);
		ids.add(newId);

		updateList();
	}

	void clear() {

		ids.clear();

		updateList();
	}

	void selectId(CIdentity id) {

		Enumeration<InstanceIdListElement> elements = model.elements();

		while (elements.hasMoreElements()) {

			InstanceIdListElement element = elements.nextElement();

			if (element.id.equals(id)) {

				setSelectedValue(element, true);

				break;
			}
		}
	}

	boolean isSelectedId() {

		return getSelectedValue() != null;
	}

	CIdentity getSelectedId() {

		List<CIdentity> selectedIds = getSelectedIds();

		if (selectedIds.size() == 1) {

			return selectedIds.get(0);
		}

		throw new Error("Expected single selection, found: " + selectedIds.size());
	}

	List<CIdentity> getSelectedIds() {

		List<CIdentity> selectedIds = new ArrayList<CIdentity>();

		for (InstanceIdListElement element : getSelectedValuesList()) {

			selectedIds.add(element.id);
		}

		return selectedIds;
	}

	boolean isEmpty() {

		return ids.isEmpty();
	}

	Set<CIdentity> getAllIds() {

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

		for (CIdentity id : ids) {

			elements.add(new InstanceIdListElement(id));
		}

		return elements;
	}
}