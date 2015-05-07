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

package uk.ac.manchester.cs.mekon.gui.util;

import java.util.*;
import javax.swing.*;
import javax.swing.event.*;

import uk.ac.manchester.cs.mekon.*;

/**
 * @author Colin Puleston
 */
public class GList<E> extends JList {

	static private final long serialVersionUID = -1;

	private LocalListModel model;
	private E selectedEntity = null;

	private GSelectionListeners<E> selectionListeners = new GSelectionListeners<E>();

	private class SelectionProcessor implements ListSelectionListener {

		public void valueChanged(ListSelectionEvent event) {

			if (!event.getValueIsAdjusting()) {

				checkUpdateSelection();
			}
		}
	}

	private class DisplayList extends GCellDisplaySortedList<GListElement<E>> {

		DisplayList(boolean orderAlphabetically) {

			super(orderAlphabetically);
		}

		GCellDisplay getDisplay(GListElement<E> element) {

			return element;
		}
	}

	private class LocalListModel extends DefaultListModel {

		static private final long serialVersionUID = -1;

		private DisplayList displayList;
		private boolean upToDate = false;

		public void clear() {

			super.clear();
			displayList.clear();
		}

		public GListElement<E> getElementAt(int index) {

			checkUpToDate();

			return asListElement(super.getElementAt(index));
		}

		public int getSize() {

			checkUpToDate();

			return super.getSize();
		}

		LocalListModel(boolean orderAlphabetically) {

			displayList = new DisplayList(orderAlphabetically);
		}

		void add(E entity, GCellDisplay display) {

			displayList.add(new GListElement<E>(entity, display));

			upToDate = false;
		}

		void remove(E entity) {

			GListElement<E> element = getElementOrNull(entity);

			if (element != null) {

				displayList.remove(element);

				upToDate = false;
			}
		}

		void applyFilter(GLexicalFilter filter) {

			super.clear();
			addElements(filter);

			checkRestoreSelection();
		}

		DisplayList getDisplayList() {

			return displayList;
		}

		List<E> getEntityList() {

			List<E> entities = new ArrayList<E>();

			for (GListElement<E> element : displayList.asList()) {

				entities.add(element.getEntity());
			}

			return entities;
		}

		GListElement<E> getElement(E entity) {

			GListElement<E> element = getElementOrNull(entity);

			if (element == null) {

				throw new KAccessException("Cannot find element: " + element);
			}

			return element;
		}

		private void checkUpToDate() {

			if (!upToDate) {

				upToDate = true;

				applyFilter(null);
			}
		}

		private void addElements(GLexicalFilter filter) {

			for (GListElement<E> element : displayList.asList()) {

				if (filter == null || filter.pass(element.getLabel())) {

					addElement(element);
				}
			}
		}

		private GListElement<E> getElementOrNull(E entity) {

			for (GListElement<E> element : displayList.asList()) {

				if (element.getEntity().equals(entity)) {

					return element;
				}
			}

			return null;
		}

		private GListElement<E> asListElement(Object elementObj) {

			List<GListElement<E>> listEls = displayList.asList();

			return listEls.get(listEls.indexOf(elementObj));
		}
	}

	public GList(boolean orderAlphabetically) {

		model = new LocalListModel(orderAlphabetically);

		setModel(model);

		GCellRenderers.get().set(this);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setSelectedIndex(-1);
		addListSelectionListener(new SelectionProcessor());
	}

	public void addSelectionListener(GSelectionListener<E> selectionListener) {

		selectionListeners.add(selectionListener);
	}

	public void addEntity(E entity, GCellDisplay display) {

		model.add(entity, display);
		revalidate();
	}

	public void removeEntity(E entity) {

		if (selectedEntity == entity) {

			selectedEntity = null;
		}

		model.remove(entity);
		revalidate();
	}

	public void applyFilter(GLexicalFilter filter) {

		model.applyFilter(filter);
		revalidate();
	}

	public void clearFilter() {

		model.applyFilter(null);
		revalidate();
	}

	public void clearList() {

		model.clear();
		revalidate();

		selectedEntity = null;
	}

	public void select(E entity) {

		setSelectedValue(model.getElement(entity), true);
	}

	public List<E> getEntityList() {

		return model.getEntityList();
	}

	public E getEntity(int index) {

		return getEntityList().get(index);
	}

	public boolean containsEntity(E entity) {

		return getEntityList().contains(entity);
	}

	public E getSelectedEntity() {

		return selectedEntity;
	}

	private void checkUpdateSelection() {

		int index = getSelectedIndex();

		if (index != -1) {

			E entity = model.getElementAt(index).getEntity();

			if (entity != selectedEntity) {

				selectedEntity = entity;
				selectionListeners.poll(selectedEntity);
			}
		}
	}

	private void checkRestoreSelection() {

		if (selectedEntity != null) {

			select(selectedEntity);
		}
	}
}
